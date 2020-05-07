package module.setup.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.domain.setup.model.State;
import module.setup.dto.StateDTO;
import module.setup.service.StateService;

@Service
@Transactional
public class StateServiceImpl implements StateService {
	private static final Logger logger = LoggerFactory.getLogger(StateServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public void initState() {
		checkAndSaveState("JHR", "Johor", 1l);
		checkAndSaveState("KDH", "Kedah", 2l);
		checkAndSaveState("KTN", "Kelantan", 3l);
		checkAndSaveState("MLK", "Malacca", 4l);
		checkAndSaveState("NSN", "Negeri Sembilan", 5l);
		checkAndSaveState("PHG", "Pahang", 6l);
		checkAndSaveState("PNG", "Penang", 7l);
		checkAndSaveState("PRK", "Perak", 8l);
		checkAndSaveState("PLS", "Perlis", 9l);
		checkAndSaveState("SGR", "Selangor", 10l);
		checkAndSaveState("TRG", "Terengganu", 11l);
		checkAndSaveState("SBH", "Sabah", 12l);
		checkAndSaveState("SWK", "Sarawak", 13l);
		checkAndSaveState("KUL", "W.P. Kuala Lumpur", 14l);
		checkAndSaveState("LBN", "W.P. Labuan", 15l);
		checkAndSaveState("PJY", "W.P. Putrajaya", 16l);
	}

	@Transactional
	protected void checkAndSaveState(String code, String name, Long sortOrder) {
		Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        
		CriteriaQuery<State> criteriaQuery = criteriaBuilder.createQuery(State.class);
        Root<State> root = criteriaQuery.from(State.class);

        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.isNotEmpty(code)) {
            predicates.add(criteriaBuilder.equal(root.get("code"), code));
        } 
        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        
		try {
			State state = (State) session.createQuery(criteriaQuery).uniqueResult();
			if (state == null) {
				// No state object found, insert a new one
				state = new State();
				state.setCode(code);
				state.setName(name);
				state.setSortOrder(sortOrder);
				session.persist(state);
				logger.info("State not found, create and save it. State Code [" + code + "], State Name [" + name
						+ "], Sort Order [" + sortOrder + "]");
			} else {
				if(	(StringUtils.isNotEmpty(state.getName()) && !state.getName().equalsIgnoreCase(name))
						|| (state.getSortOrder() != null && !state.getSortOrder().equals(sortOrder)) ) {
					state.setName(name);
					state.setSortOrder(sortOrder);
					session.merge(state);
					logger.info("State found, update it. State Code [" + code + "], State Name [" + name + "], Sort Order [" + sortOrder + "]");
				}
			}
		} catch (Exception e) {
			logger.error("Error in getting / saving " + State.class.getName() + " entity", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<State> getStates() {
		Session session = sessionFactory.getCurrentSession();
		Query<State> query = session.createQuery("SELECT s FROM " + State.class.getName() + " s ORDER BY s.name");
		return (List<State>) query.list();
	}

	@Override
	@Transactional
	public List<StateDTO> findAll() {
		Session session = sessionFactory.getCurrentSession(); 
        CriteriaBuilder criterBuilder = session.getCriteriaBuilder();
        CriteriaQuery<State> criteriaQuery = criterBuilder.createQuery(State.class);
        criteriaQuery.from(State.class);
        List<State> states = (List<State>) session.createQuery(criteriaQuery).getResultList();
        List<StateDTO> result = new ArrayList<StateDTO>();
        if (states.size() > 0) {
        	states.forEach(state -> {
        		StateDTO dto = new StateDTO();
        		dto.setCode(state.getCode());
        		dto.setName(state.getName());
        		dto.setSortOrder(state.getSortOrder());
        		result.add(dto);
        	});
        }
        Collections.sort(result, Comparator.comparing(StateDTO::getSortOrder)); 
        return result;
	}

	@Override
	@Transactional
	public State findOneByCode(String stateCode) {
		Session session = sessionFactory.getCurrentSession(); 
        CriteriaBuilder criterBuilder = session.getCriteriaBuilder();
        CriteriaQuery<State> criteriaQuery = criterBuilder.createQuery(State.class);
        Root<State> root = criteriaQuery.from(State.class);
        criteriaQuery.where(criterBuilder.equal(root.get("code"), stateCode));
        State state = (State) session.createQuery(criteriaQuery).getSingleResult();
        return state;
	}
	
}
