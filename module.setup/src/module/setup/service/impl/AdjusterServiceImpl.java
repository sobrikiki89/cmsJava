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

import app.core.domain.setup.dao.AdjusterDAO;
import app.core.domain.setup.dao.ContactDAO;
import app.core.domain.setup.model.Adjuster;
import app.core.domain.setup.model.Contact;
import app.core.domain.setup.model.State;
import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import module.setup.dto.AdjusterDTO;
import module.setup.dto.ContactDTO;
import module.setup.service.AdjusterService;
import module.setup.service.StateService;

@Service
@Transactional
public class AdjusterServiceImpl implements AdjusterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdjusterServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private ContactDAO contactDAO;
	
	@Autowired
	private AdjusterDAO adjusterDAO;
	
	@Autowired
	private StateService stateService;

	@Override
	public List<AdjusterDTO> getAllAdjusters() {
		List<Adjuster> objects = adjusterDAO.findAll();
		List<AdjusterDTO> result = new ArrayList<AdjusterDTO>();
		if (objects != null) {
			result = objects.stream().map(temp -> {
				AdjusterDTO dto = new AdjusterDTO();
				dto.setId(temp.getId());
				dto.setFirmName(temp.getFirmName());
	            return dto;
	        }).collect(Collectors.toList());
	
        Collections.sort(result, Comparator.comparing(AdjusterDTO::getFirmName)); 
		}
		return result;
	}
	
	@Override
	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataSet<AdjusterDTO> findAdjusterBy(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<Adjuster, AdjusterDTO>(){
				@Override
				public AdjusterDTO map(Adjuster entity) {
					AdjusterDTO dto = new AdjusterDTO();
					dto.setId(entity.getId());
					dto.setFirmName(entity.getFirmName());
					dto.setActiveFlag(entity.getActiveFlag());
					ContactDTO contactDTO = new ContactDTO();
					contactDTO.setTelNo(entity.getContact().getTelNo());
					contactDTO.setFaxNo(entity.getContact().getFaxNo());
					contactDTO.setEmail(entity.getContact().getEmail());
					dto.setContactDTO(contactDTO);
					return dto;
				}
			};
					
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + Adjuster.class.getName() + " o");
			
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
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			/**
			 * Step 3: paging
			 */
			query.setFirstResult(criteria.getStart());
			query.setMaxResults(criteria.getLength());

			List<Object> resultList = (List<Object>) query.getResultList();
			List<Object> objects = resultList;
			List<AdjusterDTO> result = new ArrayList<AdjusterDTO>();
			for (Object object : objects) {
				result.add((AdjusterDTO) mapper.map((EntityBase) object));
			}

			String countHQL = "SELECT COUNT(o) FROM " + Adjuster.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.getSingleResult();

			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			Long countFiltered = Long.parseLong(String.valueOf(query.getResultList().size()));

			return new DataSet(result, count, countFiltered);
			
		}  catch (Exception e) {
			throw new BaseApplicationException("failed getAdjusters", e);
		}
	}

	@Override
	public Long postAdjuster(AdjusterDTO adjusterDTO, ContactDTO contactDTO) {
		Adjuster adjuster = new Adjuster();
		adjuster.setFirmName(adjusterDTO.getFirmName());
		adjuster.setContact(null);
		if (contactDTO != null) {
			Contact contact = new Contact();			
			if(!contactDTO.dtoIsEmpty()) {
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
			contact.setState(null);
			if (StringUtils.isNotEmpty(contactDTO.getStateCode())) {
				State state = stateService.findOneByCode(contactDTO.getStateCode());
				contact.setState(state);
			}			
			adjuster.setContact(contact);
		}
		adjuster.setActiveFlag(adjusterDTO.getActiveFlag());
		adjuster = adjusterDAO.create(adjuster);
		return adjuster.getId();
	}

	@Override
	public AdjusterDTO findAdjusterById(Long id) {
		Adjuster adjuster = adjusterDAO.findOneById(id);
		if (adjuster != null) {
			AdjusterDTO dto = mapToAdjusterDTO(adjuster);
			return dto;
		}
		return null;
	}

	public AdjusterDTO mapToAdjusterDTO(Adjuster entity) {
		AdjusterDTO dto = new AdjusterDTO();
		dto.setId(entity.getId());
		dto.setFirmName(entity.getFirmName());
		dto.setActiveFlag(entity.getActiveFlag());
		dto.setRemark(entity.getRemark());
		if (entity.getContact()!=null) {
			Contact contact = contactDAO.findOneById(entity.getContact().getId());
			ContactDTO contactDTO = mapToContactDTO(contact);
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
		if (contact.getState()!=null) {
			dto.setStateCode(contact.getState().getCode());
		}
		return dto;
	}

	@Override
	public Long postEditAdjuster(AdjusterDTO adjusterDTO, ContactDTO contactDTO) {
		Adjuster adjuster = mapToEntity(adjusterDTO, contactDTO);
		adjuster = adjusterDAO.update(adjuster);
		return adjuster.getId();
	}

	private Adjuster mapToEntity(AdjusterDTO adjusterDTO, ContactDTO contactDTO) {
		Adjuster adjuster = adjusterDAO.findOneById(adjusterDTO.getId());
			adjuster.setId(adjusterDTO.getId());
			adjuster.setFirmName(adjusterDTO.getFirmName());
			adjuster.setActiveFlag(adjusterDTO.getActiveFlag());
		Contact contact = adjuster.getContact();
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
		return adjuster;
	}

	@Override
	public void postDeleteAdjuster(Long[] selected) {
		for (Long id : selected) {
			adjusterDAO.deleteById(id);
		}
	}

	@Override
	public String checkAssociationById(Long[] selected) {
		LOGGER.info("" + selected);
		String usedAssociation = adjusterDAO.findAllByIds(selected);
		// TODO when the relationship is done 
		// return usedAssociation;
		LOGGER.info("" + usedAssociation);
		return null;
	}

	@Override
	public boolean findAdjusterByNameAndActiveFlag(String firmName, Boolean activeFlag) {
		return adjusterDAO.findOneByNameAndActiveFlag(firmName, activeFlag);
	}
}
