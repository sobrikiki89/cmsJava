package app.core.service.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.model.DocSequence;
import app.core.service.SequenceService;

@Service
public class SequenceServiceImpl extends AbstractServiceImpl implements SequenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SequenceServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Transactional
	public String getNextSequence(String docCode, String runningNumberFormat) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Generating new sequence for Document Code [" + docCode + "], Running Number Format ["
					+ runningNumberFormat + "]");
		}

		String sequenceStr = "";
		try {
			DocSequence ds = null;
			Session session = sessionFactory.getCurrentSession();
			Query query = session
					.createQuery("SELECT o FROM " + DocSequence.class.getName()
							+ " o WHERE o.code = :code AND o.month is null AND o.year is null")
					.setParameter("code", docCode);
			List<DocSequence> lstDataDocSequence = (List<DocSequence>) query.list();
			if (lstDataDocSequence != null && lstDataDocSequence.size() > 0) {
				ds = lstDataDocSequence.get(0);
			}

			String strFormater = runningNumberFormat;
			int increaseNumber = 1;

			if (ds != null) {
				if (ds.getIncreaseNumber() != null) {
					increaseNumber = ds.getIncreaseNumber() + 1;
					strFormater = ds.getFormat();
				}
				ds.setIncreaseNumber(increaseNumber);
				session.merge(ds);

			} else {
				DocSequence docS = new DocSequence();
				docS.setCode(docCode);
				docS.setIncreaseNumber(increaseNumber);
				docS.setFormat(strFormater);
				session.persist(docS);
			}

			sequenceStr = String.format(strFormater, increaseNumber);
		} catch (Exception e) {
			LOGGER.error("Error on generating sequence no", e);
		}

		LOGGER.info("Sequence generated for DOC_CODE [" + docCode + "] is [" + sequenceStr + "]");
		return sequenceStr;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String getNextSequence(String docCode, String runningNumberFormat, Date period) {
		String sequenceStr = "";

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generating new sequence for Document Code [" + docCode + "], Running Number Format ["
						+ runningNumberFormat + "], Period [" + period + "]");
			}
			LocalDate date = LocalDate.fromDateFields(period);
			int day = date.getDayOfMonth();
			int month = date.getMonthOfYear();
			int year = date.getYear();

			DocSequence ds = null;
			Session session = sessionFactory.getCurrentSession();
			Query query = session
					.createQuery("SELECT o FROM " + DocSequence.class.getName()
							+ " o WHERE o.code = :code AND o.day = :day AND o.month = :month AND o.year = :year ")
					.setParameter("code", docCode).setParameter("day", day).setParameter("month", month)
					.setParameter("year", year);
			List<DocSequence> lstDataDocSequence = (List<DocSequence>) query.list();
			if (lstDataDocSequence != null && lstDataDocSequence.size() > 0) {
				ds = lstDataDocSequence.get(0);
			}

			String strFormater = runningNumberFormat;
			int increaseNumber = 1;

			if (ds != null) {
				if (ds.getIncreaseNumber() != null) {
					increaseNumber = ds.getIncreaseNumber() + 1;
					strFormater = ds.getFormat();
				}
				ds.setIncreaseNumber(increaseNumber);
				session.merge(ds);

			} else {
				DocSequence docS = new DocSequence();
				docS.setCode(docCode);
				docS.setIncreaseNumber(increaseNumber);
				docS.setFormat(strFormater);
				docS.setYear(year);
				docS.setMonth(month);
				docS.setDay(day);
				session.persist(docS);
			}

			sequenceStr = String.format(strFormater, increaseNumber);
		} catch (Exception e) {
			LOGGER.error("Error on generating sequence no", e);
		}

		LOGGER.info("Sequence generated for DOC_CODE [" + docCode + "] is [" + sequenceStr + "]");
		return sequenceStr;
	}
}
