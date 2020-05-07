package app.core.usermgmt.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.query.Query;
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

import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import app.core.service.impl.AbstractServiceImpl;
import app.core.usermgmt.dto.RoleDTO;
import app.core.usermgmt.dto.RoleFunctionDTO;
import app.core.usermgmt.dto.UserDTO;
import app.core.usermgmt.model.ContextVarType;
import app.core.usermgmt.model.Function;
import app.core.usermgmt.model.Role;
import app.core.usermgmt.model.RoleFunction;
import app.core.usermgmt.model.RoleFunctionPK;
import app.core.usermgmt.model.User;
import app.core.usermgmt.model.UserContextVar;
import app.core.usermgmt.model.UserRole;
import app.core.usermgmt.model.UserRoleId;
import app.core.usermgmt.service.UserMgmtService;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class UserMgmtServiceImpl extends AbstractServiceImpl implements UserMgmtService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserMgmtServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	// ---------------- Init Method -----------------
	@Transactional
	public void initContextVarType() {
		// Create default context var type if it was not found from database;
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select o from " + ContextVarType.class.getName() + " o");
		List<ContextVarType> varTypes = query.list();

		boolean foundLastRoleId = false;
		for (ContextVarType varType : varTypes) {
			if ("LAST_ROLE_ID".equalsIgnoreCase(varType.getName())) {
				foundLastRoleId = true;
				break;
			}
		}
		// Create a new entity, because it is not found from DB
		if (!foundLastRoleId) {
			ContextVarType entity = new ContextVarType();
			entity.setName("LAST_ROLE_ID");
			entity.setDescription(
					"Record the last role id when the user login. When a user is created, user can set up the context var");
			session.save(entity);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("LAST_ROLE_ID is not found in ContextVarType, create one.");
			}
		}
	}

	@Transactional
	public void resetConnectedFlag() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createNativeQuery("UPDATE app_user SET connected_flag = :connected_flag");
		query.setParameter("connected_flag", Boolean.FALSE);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public List<Role> getRoleByUserId(Long userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select distinct r from " + UserRole.class.getName()
				+ " o inner join o.role r where o.user.id = :userId");
		return query.setParameter("userId", userId).list();
	}

	@Transactional
	public void updateLastRoleId(User user, Role role) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("select o from " + ContextVarType.class.getName() + " o where o.name = :name");
		List<ContextVarType> contextVarTypes = query.setParameter("name", "LAST_ROLE_ID").list();
		if (contextVarTypes != null && contextVarTypes.size() > 0) {
			query = session.createQuery("update " + UserContextVar.class.getName()
					+ " set value = :value where contextVarType.id = :contextVarTypeId and user.id = :userId");
			query.setParameter("value", String.valueOf(role.getId()))
					.setParameter("contextVarTypeId", contextVarTypes.get(0).getId())
					.setParameter("userId", user.getId());
			query.executeUpdate();
		}
	}

	@Transactional(readOnly = true)
	public DataSet<UserDTO> getUser(DatatablesCriterias criteria) throws BaseApplicationException {
		
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<User, UserDTO>() {
				@Override
				public UserDTO map(User entity) {
					UserDTO userDTO = new UserDTO();
					userDTO.setId(entity.getId());
					userDTO.setUsername(entity.getUsername());
					userDTO.setFirstName(entity.getFirstName());
					userDTO.setLastName(entity.getLastName());
					userDTO.setLastAccessTime(entity.getLastAccessTime());
					if (entity.getActiveFlag()) {
						userDTO.setActiveFlag(true);
					} else {
						userDTO.setActiveFlag(false);
					}
					return userDTO;
				}
			};

			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + User.class.getName() + " o");

			/**
			 * Step 1: filtering
			 */
			StringBuilder whereClause = new StringBuilder();
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				whereClause.append(" WHERE ");
				whereClause.append(" LOWER(o.username) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.firstName) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.lastName) LIKE :searchCrit");
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
				if ("username".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.username " + colDef.getSortDirection());
				} else if ("firstName".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.firstName " + colDef.getSortDirection());
				} else if ("lastName".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.lastName " + colDef.getSortDirection());
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

			List objects = query.list();
			List result = new ArrayList();
			for (Object object : objects) {
				result.add(mapper.map((EntityBase) object));
			}

			String countHQL = new String("SELECT COUNT(o) FROM " + User.class.getName() + " o");
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();
			
			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);

		} catch (Exception e) {
			throw new BaseApplicationException("failed getDTOObjectList", e);
		}
	}

	@Transactional(readOnly = true)
	public User getUser(Long userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"SELECT o FROM " + User.class.getName() + " o LEFT JOIN FETCH o.passwordHistories WHERE o.id = :userId")
				.setParameter("userId", userId);
		return (User) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public User getUserByUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT o FROM " + User.class.getName() + " o WHERE o.username = :username")
				.setParameter("username", username);

		return (User) query.uniqueResult();
	}

	@Transactional
	public Long createUserIfNotFound(User user) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + User.class.getName() + " o WHERE o.username = :username")
				.setParameter("username", user.getUsername());
		try {
			User dbUser = (User) q.uniqueResult();
			if (dbUser != null) {
				LOGGER.info("skip to create, user found with username [" + user.getUsername() + "]");
				return dbUser.getId();
			} else {
				return (Long) session.save(user);
			}
		} catch (NonUniqueResultException e) {
			LOGGER.error("Non-unique user found, username [" + user.getUsername() + "]", e);
		}
		return -1L;
	}

	@Transactional
	public Long createUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(user);
	}

	@Transactional
	public void updateUser(User user) {
		Session session = sessionFactory.getCurrentSession();

		// Delete existing userrole
		Query query = session.createQuery("delete UserRole ur where ur.user.id = :userId");
		query.setParameter("userId", user.getId());
		query.executeUpdate();

		List<UserRole> userRoles = user.getUserRoles();
		List<UserRole> newUserRoles = new ArrayList<UserRole>();

		for (UserRole userRole : userRoles) {
			newUserRoles.add((UserRole) session.merge(userRole));
		}

		user.setUserRoles(newUserRoles);
		session.merge(user);
	}

	@Transactional
	public void updatePassword(User user) {
		Session session = sessionFactory.getCurrentSession();
		User entity = (User) session.get(User.class, user.getId());
		entity.setSalt(user.getSalt());
		entity.setPassword(user.getPassword());
		session.merge(entity);
	}

	@Transactional
	public UserRoleId createUserRoleIfNotFound(UserRole userRole) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + UserRole.class.getName() + " o "
						+ "WHERE o.id.roleId = :roleId AND o.id.userId = :userId")
				.setParameter("roleId", userRole.getId().getRoleId())
				.setParameter("userId", userRole.getId().getUserId());
		try {
			UserRole dbUserRole = (UserRole) q.uniqueResult();
			if (dbUserRole != null) {
				LOGGER.info("skip to create, userrole found with role id [" + userRole.getId().getRoleId()
						+ "], user id [" + userRole.getId().getUserId() + "]");
				return dbUserRole.getId();
			} else {
				return (UserRoleId) session.save(userRole);
			}
		} catch (NonUniqueResultException e) {
			LOGGER.error("Non-unique userrole found, role id [" + userRole.getId().getRoleId() + "], user id ["
					+ userRole.getId().getUserId() + "]", e);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<Role> getRoles() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT o FROM " + Role.class.getName() + " o ORDER BY o.sortOrder");
		return (List<Role>) query.list();
	}

	@Transactional(readOnly = true)
	public List<Function> getFunction() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT o FROM " + Function.class.getName() + " o ORDER BY o.name");
		return (List<Function>) query.list();
	}

	// --------------------- role function service -------------------------

	@Transactional(readOnly = true)
	public DataSet<RoleFunctionDTO> getRoleFunction(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<RoleFunction, RoleFunctionDTO>() {
				@Override
				public RoleFunctionDTO map(RoleFunction entity) {
					RoleFunctionDTO obj = new RoleFunctionDTO();
					obj.setPk(entity.getPk());
					obj.setRole(entity.getRole().getName());
					obj.setFunction(entity.getFunction().getName());
					obj.setCreateable(entity.getCreateable());
					obj.setDeleteable(entity.getDeleteable());
					obj.setReadable(entity.getReadable());
					obj.setUpdateable(entity.getUpdateable());
					return obj;
				}
			};

			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + RoleFunction.class.getName() + " o");

			/**
			 * Step 1: filtering
			 */
			StringBuilder whereClause = new StringBuilder();
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				whereClause.append(" WHERE ");
				whereClause.append(" LOWER(o.role.name) LIKE '%?%'".replace("?", criteria.getSearch().toLowerCase()));
				whereClause.append(" OR");
				whereClause
						.append(" LOWER(o.function.name) LIKE '%?%'".replace("?", criteria.getSearch().toLowerCase()));
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
				if ("role".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.role.name " + colDef.getSortDirection());
				} else if ("function".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.function.name " + colDef.getSortDirection());
				}
				if (itr2.hasNext()) {
					sortClause.append(" , ");
				}
			}

			queryBuilder.append(sortClause);

			Session session = sessionFactory.getCurrentSession();
			session.setHibernateFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());

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

			String countHQL = "SELECT COUNT(o) FROM " + RoleFunction.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			query = session.createQuery(queryBuilder.toString());
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);

		} catch (Exception e) {
			throw new BaseApplicationException("failed getRoleFunction", e);
		}
	}

	@Transactional
	public Long createRoleIfNotFound(Role role) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Role.class.getName() + " o WHERE o.name = :name")
				.setParameter("name", role.getName());
		try {
			Role dbRole = (Role) q.uniqueResult();
			if (dbRole != null) {
				LOGGER.info("skip to create, role found with name [" + role.getName() + "]");
				return dbRole.getId();
			} else {
				return (Long) session.save(role);
			}
		} catch (NonUniqueResultException e) {
			LOGGER.error("Non-unique role found, name [" + role.getName() + "]", e);
		}
		return -1L;
	}

	@Transactional
	public Long createRole(Role newRole) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(newRole);
	}

	@Transactional
	public RoleFunctionPK createRoleFunctionIfNotFound(RoleFunction roleFunction) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + RoleFunction.class.getName() + " o "
						+ "WHERE o.pk.roleId = :roleId AND o.pk.functionCode = :functionCode")
				.setParameter("roleId", roleFunction.getPk().getRoleId())
				.setParameter("functionCode", roleFunction.getPk().getFunctionCode());
		try {
			RoleFunction dbRoleFunction = (RoleFunction) q.uniqueResult();
			if (dbRoleFunction != null) {
				LOGGER.info("skip to create, rolefunction found with role id [" + roleFunction.getPk().getRoleId()
						+ "], function code [" + roleFunction.getPk().getFunctionCode() + "]");
				return dbRoleFunction.getPk();
			} else {
				return (RoleFunctionPK) session.save(roleFunction);
			}
		} catch (NonUniqueResultException e) {
			LOGGER.error("Non-unique rolefunction found, role id [" + roleFunction.getPk().getRoleId()
					+ "], function code [" + roleFunction.getPk().getFunctionCode() + "]", e);
		}
		return null;
	}

	@Transactional
	public Serializable createRoleFunction(RoleFunction roleFunction) {
		Session session = sessionFactory.getCurrentSession();
		return (Serializable) session.save(roleFunction);
	}

	@Transactional(readOnly = true)
	public List<RoleFunction> getRoleFunction() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT o FROM " + RoleFunction.class.getName() + " o");
		return (List<RoleFunction>) query.list();
	}

	@Transactional(readOnly = true)
	public RoleFunction getRoleFuncEdit(RoleFunctionPK roleFunctionPk) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("SELECT rf FROM " + RoleFunction.class.getName() + " rf " + " LEFT JOIN FETCH rf.role "
						+ " LEFT JOIN FETCH rf.function " + " WHERE rf.pk = :roleFunctionPk ")
				.setParameter("roleFunctionPk", roleFunctionPk);
		return (RoleFunction) query.uniqueResult();
	}

	@Transactional
	public void updateRole(Role role) {
		Session session = sessionFactory.getCurrentSession();

		// Delete existing userrole
		Query query = session.createQuery("delete UserRole ur where ur.role.id = :roleId");
		query.setParameter("roleId", role.getId());
		query.executeUpdate();

		List<UserRole> userRoles = role.getUserRoles();
		List<UserRole> newUserRoles = new ArrayList<UserRole>();

		for (UserRole userRole : userRoles) {
			newUserRoles.add((UserRole) session.merge(userRole));
		}

		role.setUserRoles(newUserRoles);
		session.merge(role);
	}

	@Transactional
	public void updateRoleFunction(RoleFunction roleFunction) {
		Session session = sessionFactory.getCurrentSession();

		RoleFunctionPK roleFunctionPK = new RoleFunctionPK();
		roleFunctionPK.setRoleId(roleFunction.getPk().getRoleId());
		roleFunctionPK.setFunctionCode(roleFunction.getPk().getFunctionCode());
		roleFunction.setPk(roleFunctionPK);

		session.merge(roleFunction);
	}

	@Transactional
	public void deleteRoleFunction(List<RoleFunction> roleFunctions) {
		Session session = sessionFactory.getCurrentSession();

		if (roleFunctions != null) {
			for (RoleFunction roleFunction : roleFunctions) {
				Object entity = session.load(RoleFunction.class, roleFunction.getPk());
				if (entity != null) {
					session.delete(entity);
				} else {
					LOGGER.warn("Unable to delete RoleFunction due to no record exists with PK [" + roleFunction.getPk()
							+ "]");
				}
			}
		}
	}

	// get Role
	@Transactional(readOnly = true)
	public DataSet<RoleDTO> getRole(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<Role, RoleDTO>() {
				@Override
				public RoleDTO map(Role entity) {
					RoleDTO obj = new RoleDTO();
					obj.setId(entity.getId());
					obj.setName(entity.getName());
					obj.setDescription(entity.getDescription());
					obj.setSortOrder(entity.getSortOrder());
					return obj;
				}
			};

			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + Role.class.getName() + " o");

			/**
			 * Step 1: filtering
			 */
			StringBuilder whereClause = new StringBuilder();
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				whereClause.append(" WHERE ");
				whereClause.append(" LOWER(o.name) LIKE '%?%'".replace("?", criteria.getSearch().toLowerCase()));
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.description) LIKE '%?%'".replace("?", criteria.getSearch().toLowerCase()));
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
				if ("name".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.name " + colDef.getSortDirection());
				} else if ("description".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.description " + colDef.getSortDirection());
				} else if ("sortOrder".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.sortOrder " + colDef.getSortDirection());
				}
				if (itr2.hasNext()) {
					sortClause.append(" , ");
				}
			}

			queryBuilder.append(sortClause);

			Session session = sessionFactory.getCurrentSession();
			session.setHibernateFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());

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

			String countHQL = "SELECT COUNT(o) FROM " + Role.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			query = session.createQuery(queryBuilder.toString());
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);

		} catch (Exception e) {
			throw new BaseApplicationException("failed getRoleFunction", e);
		}
	}

	@Transactional(readOnly = true)
	public Role getRoleByName(String roleName) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT o FROM " + Role.class.getName() + " o WHERE LOWER(o.name) = :roleName")
				.setParameter("roleName", roleName.toLowerCase());
		return (Role) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Role getRole(Long roleId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT o FROM " + Role.class.getName() + " o WHERE o.id = :roleId")
				.setParameter("roleId", roleId);
		return (Role) query.uniqueResult();
	}

	@Transactional
	public void updateRoleObject(Role role) {
		Session session = sessionFactory.getCurrentSession();
		session.merge(role);
	}

	@Transactional
	public Role getUniqueSortOrder(Long sortOrder) {
		Session session = sessionFactory.getCurrentSession();
		List<Role> role = new ArrayList<Role>();
		Query q = session.createQuery("SELECT o FROM " + Role.class.getName() + " o WHERE o.sortOrder = :sortOrder")
				.setParameter("sortOrder", sortOrder);
		role = q.list();
		if (role.size() > 0) {
			return role.get(0);
		} else {
			return null;
		}
	}

}

