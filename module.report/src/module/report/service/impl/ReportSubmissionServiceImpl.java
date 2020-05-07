package module.report.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.security.UserPrincipal;
import app.core.service.impl.AbstractServiceImpl;
import module.report.dto.ReportSubmissionDTO;
import module.report.dto.ReportSubmissionSearchCriteria;
import module.report.model.ReportAccessControl;
import module.report.model.ReportSubmission;
import module.report.service.ReportSubmissionService;

@Service
public class ReportSubmissionServiceImpl extends AbstractServiceImpl implements ReportSubmissionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportSubmissionServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ReportSubmissionDTO> searchReportSubmission(ReportSubmissionSearchCriteria criteria) {
		Map<String, Object> param = new LinkedHashMap<String, Object>();
		StringBuilder whereClause = new StringBuilder();
		if (criteria != null) {
			if (!criteria.getCategoryCode().isEmpty()) {
				whereClause.append(" AND ");
				whereClause.append(" o.outputFormat.definition.category.code = :categoryCode");
				param.put("categoryCode", criteria.getCategoryCode());
			}

			if (criteria.getDefinitionId() != null) {
				whereClause.append(" AND ");
				whereClause.append(" o.outputFormat.definition.id = :definitionId");
				param.put("definitionId", criteria.getDefinitionId());
			}

			if (criteria.getStatus() != null) {
				whereClause.append(" AND ");
				whereClause.append(" o.status = :status");
				param.put("status", criteria.getStatus());
			}

			if (criteria.getRequestedDateFrom() != null && criteria.getRequestedDateTo() == null) {
				whereClause.append(" AND ");
				whereClause.append(" o.requestedDate >= :requestedDateFrom");

				DateTime dt = new DateTime(criteria.getRequestedDateFrom()).millisOfDay().withMinimumValue();
				param.put("requestedDateFrom", new Date(dt.getMillis()));
			} else if (criteria.getRequestedDateFrom() == null && criteria.getRequestedDateTo() != null) {
				whereClause.append(" AND ");
				whereClause.append(" o.requestedDate <= :requestedDateTo");

				DateTime dt = new DateTime(criteria.getRequestedDateTo()).millisOfDay().withMaximumValue();
				param.put("requestedDateTo", new Date(dt.getMillis()));
			} else if (criteria.getRequestedDateFrom() != null && criteria.getRequestedDateTo() != null) {
				whereClause.append(" AND ");
				whereClause.append(" o.requestedDate BETWEEN :requestedDateFrom AND :requestedDateTo");

				DateTime dt = new DateTime(criteria.getRequestedDateFrom()).millisOfDay().withMinimumValue();
				param.put("requestedDateFrom", new Date(dt.getMillis()));

				dt = new DateTime(criteria.getRequestedDateTo()).millisOfDay().withMaximumValue();
				param.put("requestedDateTo", new Date(dt.getMillis()));
			}
		}

		StringBuilder sql = new StringBuilder("SELECT DISTINCT o FROM " + ReportSubmission.class.getName() + " o, "
				+ ReportAccessControl.class.getName()
				+ " a WHERE o.outputFormat.definition = a.definition AND a.role.id = :roleId AND o.createUserId = :createUserId ");
		if (!param.isEmpty()) {
			sql.append(whereClause);
		}
		sql.append(" ORDER BY o.id DESC");
		LOGGER.info("SQL to search report : [" + sql.toString() + "]");

		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(sql.toString());
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		q.setParameter("roleId", principal.getCurrentRoleId()).setParameter("createUserId", principal.getUserId());

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List<ReportSubmission> submissionList = q.list();
		LOGGER.info("Total report submission found : " + submissionList.size());

		List<ReportSubmissionDTO> result = new ArrayList<ReportSubmissionDTO>();
		for (ReportSubmission submission : submissionList) {
			ReportSubmissionDTO dto = new ReportSubmissionDTO();
			dto.setId(submission.getId());
			dto.setCategory(submission.getOutputFormat().getDefinition().getCategory().getName());
			dto.setReportName(submission.getOutputFormat().getDefinition().getName());
			dto.setFormat(submission.getOutputFormat().getId().getFormat().name());
			dto.setRequestedBy(submission.getRequestedBy());
			dto.setRequestedDate(submission.getRequestedDate());
			dto.setEndDate(submission.getEndDate());
			dto.setStatus(submission.getStatus().name());
			result.add(dto);
		}
		return result;
	}

	@Transactional
	public Long createSubmission(ReportSubmission submission) {
		Session session = sessionFactory.getCurrentSession();
		LOGGER.info("Creating new report submission, report name ["
				+ submission.getOutputFormat().getDefinition().getName() + "]");
		return (Long) session.save(submission);
	}

	@Transactional
	public ReportSubmission updateSubmission(ReportSubmission submission) {
		Session session = sessionFactory.getCurrentSession();
		LOGGER.info("Updating report submission, report format[" + submission.getOutputFormat().getId().getFormat()
				+ "], Definition ID : [" + submission.getOutputFormat().getId().getDefinitionId() + "]");
		return (ReportSubmission) session.merge(submission);
	}

	@Transactional(readOnly = true)
	public ReportSubmission getSubmissionById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		ReportSubmission submission = (ReportSubmission) session.get(ReportSubmission.class, id);
		Hibernate.initialize(submission.getStatus());
		Hibernate.initialize(submission.getOutputFormat());
		if (submission.getOutputFormat() != null) {
			Hibernate.initialize(submission.getOutputFormat().getDefinition());
			Hibernate.initialize(submission.getOutputFormat().getId());
		}
		return submission;
	}
}
