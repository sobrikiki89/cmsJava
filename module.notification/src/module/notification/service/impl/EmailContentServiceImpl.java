package module.notification.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import app.core.service.impl.AbstractServiceImpl;
import app.core.spring.EncryptedPropertyPlaceholderConfigurer;
import module.notification.constant.NotificationConstant;
import module.notification.model.EmailStatus;
import module.notification.model.EmailType;
import module.notification.model.Notification;
import module.notification.model.NotificationEmail;
import module.notification.model.NotificationId;
import module.notification.model.NotificationType;
import module.notification.object.EmailContentBO;
import module.notification.service.EmailContentService;

@Service
@Transactional
public class EmailContentServiceImpl extends AbstractServiceImpl implements EmailContentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailContentServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Value("#{'${notification.transaction.list}'.split(',')}")
	private List<String> notificationTransactions;

	public static final Function<NotificationEmail, EmailContentBO> CONVERTER = new Function<NotificationEmail, EmailContentBO>() {
		@Override
		public EmailContentBO apply(NotificationEmail source) {
			EmailContentBO output = new EmailContentBO();
			output.setTrxCode(source.getNotification().getPk().getTrxCode());
			output.setTrxType(source.getNotification().getPk().getTrxType());

			output.setEmailType(source.getEmailType());
			output.setSubject(source.getSubject());
			output.setExistingContent(source.getContent());
			output.setEffectiveDate(source.getEffectiveDate());
			output.setStatus(EmailStatus.get(source.getStatus()));
			output.setCreatedBy(source.getCreateUserId());
			output.setCreatedOn(source.getCreateDate());
			output.setModifiedBy(source.getUpdateUserId());
			output.setModifiedOn(source.getUpdateDate());
			return output;
		}
	};

	public static final Function<EmailContentBO, NotificationEmail> REVERSE_CONVERTER = new Function<EmailContentBO, NotificationEmail>() {
		@Override
		public NotificationEmail apply(EmailContentBO source) {
			Notification notification = new Notification();
			NotificationId notificationId = new NotificationId();
			notificationId.setNotificationType(NotificationType.EMAIL);
			notificationId.setTrxCode(source.getTrxCode());
			notificationId.setTrxType(source.getTrxType());
			notification.setPk(notificationId);

			NotificationEmail output = new NotificationEmail();
			output.setNotification(notification);
			output.setEmailType(source.getEmailType());
			output.setSubject(source.getSubject());
			output.setContent(source.getExistingContent());
			output.setEffectiveDate(source.getEffectiveDate());
			output.setStatus(source.getStatus().getCode());
			output.setCreateUserId(source.getCreatedBy());
			output.setCreateDate(source.getCreatedOn());
			output.setUpdateUserId(source.getModifiedBy());
			output.setUpdateDate(source.getModifiedOn());
			return output;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<EmailContentBO> getActiveContent(String trxCode, String trxType, Date effectiveDate,
			EmailType emailType) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT o FROM " + NotificationEmail.class.getName() + " o WHERE o.status = 'A' "
				+ "AND o.effectiveDate <= :effectiveDate AND o.notification.pk.trxCode = :trxCode "
				+ "AND o.notification.pk.trxType = :trxType";
		if (emailType != null) {
			hql += " AND o.emailType = :emailType";
		}

		Query q = session.createQuery(hql).setParameter("trxCode", trxCode).setParameter("trxType", trxType)
				.setParameter("effectiveDate", effectiveDate);
		if (emailType != null) {
			q = q.setParameter("emailType", emailType.getCode());
		}

		return Lists.newArrayList(Iterables.transform(q.list(), CONVERTER));
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmailContentBO> getActiveContent(String trxCode, String trxType, EmailType emailType) {
		return getActiveContent(trxCode, trxType, LocalDate.now().toDate(), emailType);
	}

	@Override
	@Transactional
	public void createNotificationIfNotExists(String trxCode, String trxType, NotificationType notificationType) {
		Session session = sessionFactory.getCurrentSession();

		NotificationId pk = new NotificationId();
		pk.setTrxCode(trxCode);
		pk.setTrxType(trxType);
		pk.setNotificationType(notificationType);
		Notification obj = (Notification) session.get(Notification.class, pk);
		if (obj == null) {
			obj = new Notification();
			obj.setPk(pk);
			session.persist(obj);
		}
	}

	@Override
	@Transactional
	public void createEmailTemplate(EmailContentBO bo) {
		NotificationEmail email = REVERSE_CONVERTER.apply(bo);
		createNotificationIfNotExists(bo.getTrxCode(), bo.getTrxType(), NotificationType.EMAIL);
		Session session = sessionFactory.getCurrentSession();
		session.persist(email);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public EmailContentBO getEmailTemplate(String trxCode, String trxType, NotificationType notificationType,
			EmailType emailType) {
		Session session = sessionFactory.getCurrentSession();

		Query q = session.createQuery("SELECT o FROM " + NotificationEmail.class.getName() + " o "
				+ "WHERE o.emailType = :emailType AND o.notification.pk.trxCode = :trxCode "
				+ "AND o.notification.pk.trxType = :trxType AND o.notification.pk.notificationType = :notificationType");

		List<EmailContentBO> list = Lists.newArrayList(
				Iterables.transform((List<NotificationEmail>) q.setParameter("emailType", emailType.getCode())
						.setParameter("trxCode", trxCode).setParameter("trxType", trxType)
						.setParameter("notificationType", notificationType).list(), CONVERTER));

		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Transactional
	public void initializeEmailContent() {
		LOGGER.info("Notification Transaction found [" + notificationTransactions + "]");

		for (String trx : notificationTransactions) {
			for (EmailType emailType : EmailType.values()) {
				String keySubject = trx + "." + emailType.getCode() + "."
						+ NotificationConstant.PROP_NOTIFICATION_EMAIL_SUBJECT;
				String keyContent = trx + "." + emailType.getCode() + "."
						+ NotificationConstant.PROP_NOTIFICATION_EMAIL_CONTENT;
				String keyEffectiveDate = trx + "." + emailType.getCode() + "."
						+ NotificationConstant.PROP_NOTIFICATION_EMAIL_EFFECTIVE_DATE;

				String subject = EncryptedPropertyPlaceholderConfigurer.getProperty(keySubject);
				String content = EncryptedPropertyPlaceholderConfigurer.getProperty(keyContent);
				String effectiveDate = EncryptedPropertyPlaceholderConfigurer.getProperty(keyEffectiveDate);

				LOGGER.info("Getting value for [" + keySubject + "=" + subject + "]");
				LOGGER.info("Getting value for [" + keyContent + "=" + content + "]");
				LOGGER.info("Getting value for [" + keyEffectiveDate + "=" + effectiveDate + "]");

				if (!StringUtils.isBlank(subject) && !StringUtils.isBlank(content)
						&& !StringUtils.isBlank(effectiveDate)) {
					String[] items = keySubject.split("\\.");
					if (items.length >= 3) {
						EmailContentBO bo = getEmailTemplate(items[1], items[2], NotificationType.valueOf(items[0]),
								emailType);
						if (bo == null) {
							bo = new EmailContentBO();
							bo.setTrxCode(items[1]);
							bo.setTrxType(items[2]);
							bo.setExistingContent(content);
							bo.setSubject(subject);
							try {
								bo.setEffectiveDate(LocalDate
										.parse(effectiveDate,
												DateTimeFormat.forPattern(
														NotificationConstant.PROP_NOTIFICATION_EMAIL_EFFECTIVE_DATE_FORMAT))
										.toDate());
							} catch (IllegalArgumentException e) {
								bo.setEffectiveDate(LocalDate.now().toDate());
							}
							bo.setStatus(EmailStatus.ACTIVE);
							bo.setEmailType(emailType.getCode());
							bo.setCreatedBy(0L);
							bo.setCreatedOn(LocalDateTime.now().toDate());

							createEmailTemplate(bo);
							LOGGER.info("No email template found, create one [notification type=" + items[0]
									+ ", trx code=" + items[1] + ", trx type=" + items[2] + "]");
						}
					}
				}
			}
		}
	}
}