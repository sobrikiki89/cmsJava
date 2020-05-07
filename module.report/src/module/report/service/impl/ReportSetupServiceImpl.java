package module.report.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import app.core.registry.Module;
import app.core.security.UserPrincipal;
import app.core.service.impl.AbstractServiceImpl;
import module.report.annotation.OutputFormat;
import module.report.annotation.Report;
import module.report.annotation.ReportItem;
import module.report.dto.ReportAccessDTO;
import module.report.model.OutputFileFormat;
import module.report.model.ReportAccessControl;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.model.ReportOutputFormat;
import module.report.model.ReportOutputFormatPK;
import module.report.service.ReportSetupService;
import module.setup.model.InsuranceClass;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReportSetupServiceImpl extends AbstractServiceImpl implements ReportSetupService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportSetupServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = true)
	public List<ReportCategory> getCategories() {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + ReportCategory.class.getName() + " o");
		return (List<ReportCategory>) q.list();
	}

	@Transactional
	public String createCategory(ReportCategory category) {
		Session session = sessionFactory.getCurrentSession();
		LOGGER.info("Creating new report category, code [" + category.getCode() + "]");
		return (String) session.save(category);
	}

	@Transactional
	public void updateCategory(ReportCategory category) {
		Session session = sessionFactory.getCurrentSession();
		LOGGER.info("Updating report category, code [" + category.getCode() + "]");
		session.merge(category);
	}

	@Transactional(readOnly = true)
	public List<ReportDefinition> getReportDefinitionByCategory(ReportCategory category) {
		if (category != null && StringUtils.isNotBlank(category.getCode())) {
			Session session = sessionFactory.getCurrentSession();

			StringBuilder sql = new StringBuilder("SELECT DISTINCT o FROM ").append(ReportAccessControl.class.getName())
					.append(" a INNER JOIN a.definition o WHERE o.category.code = :categoryCode AND a.role.id = :roleId")
					.append(" ORDER BY o.name ASC");

			UserPrincipal principal = UserPrincipal.class
					.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			Query q = session.createQuery(sql.toString()).setParameter("categoryCode", category.getCode())
					.setParameter("roleId", principal.getCurrentRoleId());
			return (List<ReportDefinition>) q.list();
		}
		return new ArrayList<>();
	}

	@Transactional(readOnly = true)
	public ReportDefinition getReportDefinitionById(Long id) {
		if (id != null) {
			Session session = sessionFactory.getCurrentSession();
			ReportDefinition definition = (ReportDefinition) session.get(ReportDefinition.class, id);
			Hibernate.initialize(definition.getOutputFormats());
			return definition;
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public ReportOutputFormat getReportOutputFormatByFormat(Long definitionId, OutputFileFormat format) {
		Session session = sessionFactory.getCurrentSession();
		ReportOutputFormatPK pk = new ReportOutputFormatPK();
		pk.setDefinitionId(definitionId);
		pk.setFormat(format);
		return (ReportOutputFormat) session.get(ReportOutputFormat.class, pk);
	}

	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public DataSet<ReportAccessDTO> getReportAccessGrid(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<ReportAccessControl, ReportAccessDTO>(){
				@Override
				public ReportAccessDTO map(ReportAccessControl entity) {
					ReportAccessDTO dto = new ReportAccessDTO();
					dto.setId(entity.getId());
					dto.setReportName(entity.getDefinition().getName());
					dto.setReportCategory(entity.getDefinition().getCategory().getName());
					dto.setRole(entity.getRole().getName());
					return dto;
				}
			};
					
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + ReportAccessControl.class.getName() + " o");
			
			/**
			 * Step 1: filtering
			 */
			StringBuilder whereClause = new StringBuilder();
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				whereClause.append(" WHERE ");
				whereClause.append(" LOWER(o.role.name) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.definition.name) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.definition.category.name) LIKE :searchCrit");
			}
			
			queryBuilder.append(whereClause);
			
			/**
			 * Step 2: sorting
			 */
			StringBuilder sortClause = new StringBuilder();
			Iterator<ColumnDef> itr2 = criteria.getSortedColumnDefs().iterator();
			ColumnDef colDef;
			while (itr2.hasNext()) {
				sortClause.append(" ORDER BY ");
				colDef = itr2.next();
				if ("role.name".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.role.name " + colDef.getSortDirection());
				} else if ("definition.name".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.definition.name " + colDef.getSortDirection());
				}  else if ("definition.category.name".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.definition.category.name " + colDef.getSortDirection());
				}
				if (itr2.hasNext()) {
					sortClause.append(" , ");
				}
			}

			queryBuilder.append(sortClause);

			Session session = sessionFactory.getCurrentSession();
			session.setFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			/**
			 * Step 3: paging
			 */
			query.setFirstResult(criteria.getStart());
			query.setMaxResults(criteria.getLength());

			List objects = query.list();
			List result = new ArrayList();
			for (Object object : objects) {
				result.add(mapper.map((EntityBase) object));
			}

			String countHQL = "SELECT COUNT(o) FROM " + InsuranceClass.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);
			
		}  catch (Exception e) {
			throw new BaseApplicationException("failed getReportAccess", e);
		}
	}

	@Transactional(readOnly = true)
	public List<ReportAccessControl> getReportAccessList() {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECt o FROM " + ReportAccessControl.class.getName() + " o");
		return (List<ReportAccessControl>) q.list();
	}

	@Transactional(readOnly = true)
	public ReportAccessControl getAccessControlById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		return (ReportAccessControl) session.get(ReportAccessControl.class, id);
	}

	@Transactional
	public Long createAccessControl(ReportAccessControl accessControl) {
		Session session = sessionFactory.getCurrentSession();
		LOGGER.info("Creating new report access control, Report Name [" + accessControl.getDefinition().getName()
				+ "], Role [" + accessControl.getRole().getName() + "]");
		return (Long) session.save(accessControl);
	}

	@Transactional
	public ReportAccessControl updateAccessControl(ReportAccessControl accessControl) {
		Session session = sessionFactory.getCurrentSession();
		LOGGER.info("Updating report access control, Report Name [" + accessControl.getDefinition().getName()
				+ "], Role [" + accessControl.getRole().getName() + "]");
		return (ReportAccessControl) session.merge(accessControl);
	}

	@Transactional
	public void deleteAccessCountrol(List<ReportAccessControl> accessControls) {
		Session session = sessionFactory.getCurrentSession();

		if (accessControls != null) {
			for (ReportAccessControl accessControl : accessControls) {
				Object entity = session.load(ReportAccessControl.class, accessControl.getId());
				if (entity != null) {
					session.delete(entity);
				} else {
					LOGGER.warn("Unable to delete ReportAccessControl due to no record exists with Id ["
							+ accessControl.getId() + "]");
				}
			}
		}
	}

	@Transactional
	public void updateReport(Module module) {
		Report report = AnnotationUtils.findAnnotation(module.getClass(), Report.class);
		if (report != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("@Report found at " + module.getClass().getName());
			}
			ReportItem[] items = report.value();
			if (items != null && items.length > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total @ReportItem found [" + items.length + "]");
				}

				try {
					Session session = sessionFactory.getCurrentSession();
					for (ReportItem item : items) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("@ReportItem found with ID [" + item.id() + "], NAME [" + item.name()
									+ "], CATEGORY [" + item.category() + "], GENERATOR TYPE ["
									+ item.generatorType().name() + "], HANDLER CLASS [" + item.handlerClass()
									+ "], JSP PATH [" + item.jspPath() + "], OUTPUT FORMAT "
									+ Arrays.asList(item.format()));
						}

						Query categoryQuery = session
								.createQuery(
										"SELECT o FROM " + ReportCategory.class.getName() + " o WHERE o.code = :code")
								.setParameter("code", item.category());
						ReportCategory category = (ReportCategory) categoryQuery.uniqueResult();

						if (category == null) {
							LOGGER.warn("Skip synchronizing @ReportItem due to the given Report Category not found.");
							continue;
						}

						if (!StringUtils.isNumeric(item.id())) {
							LOGGER.warn("Skip synchronizing @ReportItem due to the given Report ID is not numeric.");
							continue;
						}

						Query query = session
								.createQuery(
										"SELECT o FROM " + ReportDefinition.class.getName() + " o WHERE o.id = :id")
								.setParameter("id", Long.parseLong(item.id()));
						ReportDefinition definition = (ReportDefinition) query.uniqueResult();
						if (definition != null) {
							definition.setName(StringUtils.defaultString(item.name()));
							definition.setCategory(category);
							definition.setGeneratorType(item.generatorType());
							definition.setParamHandlerClass(item.handlerClass());
							definition.setJspPath(item.jspPath());
							List<ReportOutputFormat> outputFormatList = new ArrayList<ReportOutputFormat>();
							for (OutputFormat format : item.format()) {
								ReportOutputFormat f = new ReportOutputFormat();
								ReportOutputFormatPK pk = new ReportOutputFormatPK();
								pk.setDefinitionId(definition.getId());
								pk.setFormat(format.outputFormat());
								f.setId(pk);
								f.setDefinition(definition);
								f.setReportClass(format.reportClass());
								outputFormatList.add(f);
							}
							definition.getOutputFormats().clear();
							definition.getOutputFormats().addAll(outputFormatList);

							session.merge(definition);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Updated report_definition [" + item.id() + "]");
							}
						} else {
							definition = new ReportDefinition();
							definition.setId(Long.parseLong(item.id()));
							definition.setName(StringUtils.defaultString(item.name()));
							definition.setCategory(category);
							definition.setGeneratorType(item.generatorType());
							definition.setParamHandlerClass(item.handlerClass());
							definition.setJspPath(item.jspPath());
							List<ReportOutputFormat> outputFormatList = new ArrayList<ReportOutputFormat>();
							for (OutputFormat format : item.format()) {
								ReportOutputFormat f = new ReportOutputFormat();
								ReportOutputFormatPK pk = new ReportOutputFormatPK();
								pk.setDefinitionId(definition.getId());
								pk.setFormat(format.outputFormat());
								f.setId(pk);
								f.setDefinition(definition);
								f.setReportClass(format.reportClass());
								outputFormatList.add(f);
							}
							definition.setOutputFormats(outputFormatList);

							session.persist(definition);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Insert report_definition [" + item.id() + "]");
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error on synchronizing @Report annotation", e);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Skip due to no @ReportItem found");
				}
			}
		}
	}
}
