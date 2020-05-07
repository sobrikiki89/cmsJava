package module.setup.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.domain.setup.dao.ContactDAO;
import app.core.domain.setup.dao.SolicitorDAO;
import app.core.domain.setup.model.Contact;
import app.core.domain.setup.model.Solicitor;
import app.core.domain.setup.model.State;
import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import module.setup.dto.ContactDTO;
import module.setup.dto.SolicitorDTO;
import module.setup.service.SolicitorService;
import module.setup.service.StateService;

@Service
@Transactional
public class SolicitorServiceImpl implements SolicitorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolicitorServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SolicitorDAO solicitorDAO;

	@Autowired
	private StateService stateService;
	
	@Autowired
	private ContactDAO contactDAO;

	@Override
	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataSet<SolicitorDTO> findSolicitorBy(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<Solicitor, SolicitorDTO>() {
				@Override
				public SolicitorDTO map(Solicitor entity) {
					SolicitorDTO dto = new SolicitorDTO();
					dto.setId(entity.getId());
					dto.setFirmName(entity.getFirmName());
					dto.setActiveFlag(entity.getActiveFlag());
					ContactDTO contactDTO = new ContactDTO();
					if (entity.getContact() != null) {
						contactDTO.setTelNo(entity.getContact().getTelNo());
						contactDTO.setFaxNo(entity.getContact().getFaxNo());
						contactDTO.setEmail(entity.getContact().getEmail());
					}
					dto.setContactDTO(contactDTO);
					return dto;
				}
			};

			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + Solicitor.class.getName() + " o");

			/**
			 * Step 1: filtering
			 */
			StringBuilder whereClause = new StringBuilder();
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				whereClause.append(" WHERE ");
				whereClause.append(" LOWER(o.firmName) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.telNo) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.faxNo) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.email) LIKE :searchCrit");
			}

			queryBuilder.append(whereClause);

			/**
			 * Step 2: sorting
			 */
			StringBuilder sortClause = new StringBuilder();
			sortClause.append(" ORDER BY ");
			Iterator<ColumnDef> itr2 = criteria.getSortedColumnDefs().iterator();
			ColumnDef colDef;
			while (itr2.hasNext()) {
				colDef = itr2.next();
				if ("firmName".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.firmName " + colDef.getSortDirection());
				} else if ("contact.telNo".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.telNo " + colDef.getSortDirection());
				} else if ("contact.faxNo".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.faxNo " + colDef.getSortDirection());
				} else if ("contact.email".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.email " + colDef.getSortDirection());
				} else if ("activeFlag".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.activeFlag " + colDef.getSortDirection());
				}
				if (itr2.hasNext()) {
					sortClause.append(" , ");
				}
			}

			queryBuilder.append(sortClause);

			Session session = sessionFactory.getCurrentSession();
			session.setHibernateFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%" + criteria.getSearch().toLowerCase() + "%");
			}

			/**
			 * Step 3: paging
			 */
			query.setFirstResult(criteria.getStart());
			query.setMaxResults(criteria.getLength());

			List<Object> resultList = (List<Object>) query.getResultList();
			List<Object> objects = resultList;
			List<SolicitorDTO> result = new ArrayList<SolicitorDTO>();
			for (Object object : objects) {
				result.add((SolicitorDTO) mapper.map((EntityBase) object));
			}

			String countHQL = "SELECT COUNT(o) FROM " + Solicitor.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.getSingleResult();

			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%" + criteria.getSearch().toLowerCase() + "%");
			}

			Long countFiltered = Long.parseLong(String.valueOf(query.getResultList().size()));

			return new DataSet(result, count, countFiltered);

		} catch (Exception e) {
			throw new BaseApplicationException("failed getSolicitors", e);
		}
	}

	@Override
	public Long postSolicitor(SolicitorDTO solicitorDTO, ContactDTO contactDTO) {
		Solicitor solicitor = mapToEntity(solicitorDTO, contactDTO);
		solicitor.setActiveFlag(solicitorDTO.getActiveFlag());
		solicitor = solicitorDAO.create(solicitor);
		return solicitor.getId();
	}

	@Override
	public SolicitorDTO findSolicitorById(Long id) {
		Solicitor solicitor = solicitorDAO.findOneById(id);
		if (solicitor != null) {
			SolicitorDTO dto = mapToSolicitorDTO(solicitor);
			return dto;
		}
		return null;
	}

	public SolicitorDTO mapToSolicitorDTO(Solicitor entity) {
		SolicitorDTO dto = new SolicitorDTO();
		dto.setId(entity.getId());
		dto.setFirmName(entity.getFirmName());
		dto.setActiveFlag(entity.getActiveFlag());
		dto.setRemark(entity.getRemark());
		if (entity.getContact() != null) {
			ContactDTO contactDTO = mapToContactDTO(entity.getContact());
			dto.setContactDTO(contactDTO);
		}
		return dto;
	}

	private ContactDTO mapToContactDTO(Contact contact) {
		ContactDTO dto = new ContactDTO();
		dto.setId(contact.getId());
		dto.setContactPerson(contact.getContactPerson());
		dto.setTelNo(contact.getTelNo());
		dto.setFaxNo(contact.getFaxNo());
		dto.setMobileNo(contact.getMobileNo());
		dto.setEmail(contact.getEmail());
		dto.setAddress1(contact.getAddress1());
		dto.setAddress2(contact.getAddress2());
		dto.setAddress3(contact.getAddress3());
		dto.setCity(contact.getCity());
		dto.setPostcode(contact.getPostcode());
		if (contact.getState() != null) {
			dto.setStateCode(contact.getState().getCode());
		}
		return dto;
	}

	@Override
	public Long postEditSolicitor(SolicitorDTO solicitorDTO, ContactDTO contactDTO) {
		Solicitor solicitor = mapToEntity(solicitorDTO, contactDTO);
		solicitor = solicitorDAO.update(solicitor);
		return solicitor.getId();
	}

	private Solicitor mapToEntity(SolicitorDTO solicitorDTO, ContactDTO contactDTO) {
		Solicitor solicitor = new Solicitor();
		Contact contact = new Contact();
			if (solicitorDTO.getId() != null) {
				solicitor = solicitorDAO.findOneById(solicitorDTO.getId());
				contact = solicitor.getContact();
			}
			solicitor.setFirmName(solicitorDTO.getFirmName());
			solicitor.setActiveFlag(solicitorDTO.getActiveFlag());
			if(!contactDTO.dtoIsEmpty()) {
				if(contact == null) {
					contact = new Contact();
				}
				contact.setContactPerson(contactDTO.getContactPerson());
				contact.setTelNo(contactDTO.getTelNo());
				contact.setFaxNo(contactDTO.getFaxNo());
				contact.setMobileNo(contactDTO.getMobileNo());
				contact.setEmail(contactDTO.getEmail());
				contact.setAddress1(contactDTO.getAddress1());
				contact.setAddress2(contactDTO.getAddress2());
				contact.setAddress3(contactDTO.getAddress3());
				contact.setCity(contactDTO.getCity());
				contact.setPostcode(contactDTO.getPostcode());
			}			
		if (StringUtils.isNotEmpty(contactDTO.getStateCode())) {
			State state = stateService.findOneByCode(contactDTO.getStateCode());
			contact.setState(state);
		}
		solicitor.setContact(contact);
		return solicitor;
	}

	@Override
	public void postDeleteSolicitor(Long[] selected) {
		for (Long id : selected) {
			solicitorDAO.deleteById(id);
		}
	}

	@Override
	public String checkAssociationById(Long[] selected) {
		LOGGER.info("Selected IDs: " + selected);
		String usedAssociation = solicitorDAO.findUsedAssociationByIds(selected);
		LOGGER.info("Association: " + usedAssociation);
		// TODO when the relationship is done 
		// return usedAssociation;
		return null;
	}

	@Override
	public List<SolicitorDTO> getAllSolicitors() {
		List<Solicitor> objects = solicitorDAO.findAll();
		List<SolicitorDTO> result = new ArrayList<SolicitorDTO>();
		if (objects != null) { 
			result = objects.stream().map(temp -> {
				SolicitorDTO dto = new SolicitorDTO();
				dto.setId(temp.getId());
				dto.setFirmName(temp.getFirmName());
	            return dto;
	        }).collect(Collectors.toList());
		
	        Collections.sort(result, Comparator.comparing(SolicitorDTO::getFirmName));
		}
		return result;
	}

	@Override
	public boolean findSolicitorByNameAndActiveFlag(String firmName, boolean activeFlag) {
		return solicitorDAO.findOneByNameAndActiveFlag(firmName, activeFlag);
	}
	
	
	
}
