package app.core.service.impl;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.annotation.Permission;
import app.core.registry.Module;
import app.core.service.FunctionService;

@Service
public class FunctionServiceImpl implements FunctionService {
	private static final Logger logger = LoggerFactory.getLogger(FunctionServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	@SuppressWarnings("rawtypes")
	public void updateFunctions(Module module) {
		Class clazz = module.getClass();
		Session session = sessionFactory.getCurrentSession();
		Query query;
		String code;
		int rowUpdated;
		for (Field field : clazz.getFields()) {
			// NOTE : Permission will only be picked up when field is String
			// type
			if (field.isAnnotationPresent(Permission.class) && String.class.equals(field.getType())) {
				Permission permission = field.getAnnotation(Permission.class);
				try {
					code = (String) field.get(clazz);
					logger.info("Function found in module [" + module.getModuleName() + "], code = [" + code
							+ "], name = [" + permission.name() + "], path = [" + permission.path() + "]");

					query = session.createSQLQuery("SELECT f.code FROM app_function f WHERE f.code = :code");
					query.setParameter("code", code);
					if (query.uniqueResult() != null) {
						// Function found in database, update it
						query = session
								.createSQLQuery("UPDATE app_function SET name = :name, path = :path WHERE code = :code");
						query.setParameter("name", permission.name()).setParameter("path", permission.path())
								.setParameter("code", code);
						rowUpdated = query.executeUpdate();
						if (logger.isDebugEnabled()) {
							logger.debug("Update function [" + code + "], row updated [" + rowUpdated + "]");
						}
					} else {
						// Function not found, insert a new function
						query = session
								.createSQLQuery("INSERT INTO app_function (code, name, description, path) VALUES (:code, :name, :description, :path)");
						query.setParameter("code", code).setParameter("name", permission.name())
								.setParameter("description", permission.name()).setParameter("path", permission.path());
						rowUpdated = query.executeUpdate();
						if (logger.isDebugEnabled()) {
							logger.debug("Insert function [" + code + "], row updated [" + rowUpdated + "]");
						}
					}
				} catch (Exception e) {
					logger.error("Error on getting field with @Permission annotation", e);
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public boolean isAuthorizedByPath(Long roleId, String path) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT f.code ");
		sql.append("FROM role_function rf");
		sql.append(" INNER JOIN app_function f");
		sql.append("  ON rf.function_code = f.code ");
		sql.append("WHERE rf.role_id = ?");
		sql.append(" AND f.path like ? ");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter(0, roleId).setParameter(1, path);
		@SuppressWarnings("rawtypes")
		List result = query.list();
		if (result != null && result.size() > 0) {
			return true;
		}

		return false;
	}

	@Transactional(readOnly = true)
	public boolean isAuthorizedByCode(Long roleId, String code) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT f.code ");
		sql.append("FROM role_function rf");
		sql.append(" INNER JOIN app_function f");
		sql.append("  ON rf.function_code = f.code ");
		sql.append("WHERE rf.role_id = ?");
		sql.append(" AND f.code = ?");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter(0, roleId).setParameter(1, code);
		@SuppressWarnings("rawtypes")
		List result = query.list();
		if (result != null && result.size() > 0) {
			return true;
		}

		return false;
	}
}
