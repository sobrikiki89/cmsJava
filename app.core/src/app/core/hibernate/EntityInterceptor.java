package app.core.hibernate;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.CharArrayType;
import org.hibernate.type.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import app.core.annotation.CurrentTemporal;
import app.core.annotation.CurrentUser;
import app.core.annotation.PersistenceType;
import app.core.security.UserPrincipal;
import app.core.type.EntityCreatedType;
import app.core.utils.AnnotationUtils;

public class EntityInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;

	private static final Map<Class<?>, AuditMetaClass> processClasses = new Hashtable<Class<?>, AuditMetaClass>();

	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		for (Type type : types) {
			if (EntityCreatedType.SYSTEM.getClass().isAssignableFrom(type.getReturnedClass())) {
				throw new IllegalStateException("System created data is prohibited to delete");
			}
		}
		super.onDelete(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		return setValues(entity, null, state, propertyNames, types, PersistenceType.CREATE);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		return setValues(entity, previousState, currentState, propertyNames, types, PersistenceType.MODIFY);
	}

	private boolean setValues(Object entity, Object[] previousState, Object[] state, String[] propertyNames,
			Type[] types, PersistenceType type) {
		long currentTime = System.currentTimeMillis();
		AuditMetaClass meta = getMeta(entity.getClass());
		if (!meta.isSupported()) {
			return false;
		}

		boolean changed = false;
		if (previousState != null && state != null && (previousState.length == state.length)) {
			for (int i = 0; i < state.length; i++) {
				if (!ObjectUtils.equals(previousState[i], state[i])) {
					// Check on char[] datatype, since the content of it might
					// be same.
					if (CharArrayType.class.equals(types[i].getClass())) {
						CharArrayType cType = CharArrayType.class.cast(types[i]);
						String p = cType.toString((char[]) previousState[i]);
						String c = cType.toString((char[]) state[i]);
						if (ObjectUtils.equals(p, c)) {
							continue;
						}
					}
					changed = true;
					break;

				}
			}
		} else {
			changed = true;
		}
		if (!changed) {
			return false;
		}

		// reset changed flag
		changed = false;
		Method temporalProperty = meta.getCurrentTemporalProperty(type);
		if (temporalProperty != null) {
			String temporalPropertyName = getPropertyName(temporalProperty);
			for (int i = 0; i < propertyNames.length; i++) {
				if (propertyNames[i].equals(temporalPropertyName)) {
					CurrentTemporal temporal = temporalProperty.getAnnotation(CurrentTemporal.class);
					Object arg = null;
					switch (temporal.value()) {
					case DATE:
						arg = new Date(currentTime);
						break;
					case TIME:
						arg = new Time(currentTime);
						break;
					case TIMESTAMP:
						arg = new Timestamp(currentTime);
						break;
					}
					if (arg != null) {
						state[i] = arg;
						changed = true;
					}
				}
			}
		}

		Method userProperty = meta.getCurrentUserProperty(type);
		if (userProperty != null) {
			String userPropertyName = getPropertyName(userProperty);
			for (int i = 0; i < propertyNames.length; i++) {
				if (propertyNames[i].equals(userPropertyName)) {
					Object arg = getUserId();
					if (arg != null) {
						state[i] = arg;
						changed = true;
					}
				}
			}
		}
		return changed;
	}

	private AuditMetaClass getMeta(Class<?> clazz) {
		AuditMetaClass sc = processClasses.get(clazz);
		if (sc == null) {
			sc = new AuditMetaClass(clazz);
			processClasses.put(clazz, sc);
		}
		return sc;
	}

	private Long getUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth == null) {
			// No authentication info available
			return -1l;
		} else {
			if (auth.isAuthenticated()) {
				return UserPrincipal.class.cast(auth.getPrincipal()).getUserId();
			} else {
				// User not yet authenticated
				return 0l;
			}
		}
	}

	private static String getPropertyName(Method method) {
		String name = method.getName();
		if (name.startsWith("set") || name.startsWith("get")) {
			return Introspector.decapitalize(name.substring(3));
		}
		if (name.startsWith("is")) {
			return Introspector.decapitalize(name.substring(2));
		}
		throw new IllegalStateException(method.getName() + " method is not comply to propery getter/setter convention.");
	}

	private static class AuditMetaClass {
		private boolean supported = false;

		private Method currentUserCreateProperty;

		private Method currentUserModifyProperty;

		private Method currentTemporalCreateProperty;

		private Method currentTemporalModifyProperty;

		public Method getCurrentUserProperty(PersistenceType type) {
			switch (type) {
			case CREATE:
				return currentUserCreateProperty;
			case MODIFY:
				return currentUserModifyProperty;
			}
			throw new IllegalArgumentException("Illegal argument on type=" + type);
		}

		public Method getCurrentTemporalProperty(PersistenceType type) {
			switch (type) {
			case CREATE:
				return currentTemporalCreateProperty;
			case MODIFY:
				return currentTemporalModifyProperty;
			}
			throw new IllegalArgumentException("Illegal argument on type=" + type);
		}

		@SuppressWarnings("unchecked")
		public AuditMetaClass(Class<?> clazz) {
			Method[] methods = AnnotationUtils.findAnnotatedMethods(clazz, CurrentUser.class, CurrentTemporal.class);
			if (methods.length > 0) {
				this.supported = true;
				for (Method method : methods) {
					CurrentTemporal temporal = method.getAnnotation(CurrentTemporal.class);
					if (temporal != null) {
						switch (temporal.type()) {
						case CREATE:
							currentTemporalCreateProperty = method;
							break;
						case MODIFY:
							currentTemporalModifyProperty = method;
							break;
						}
					}
					CurrentUser currentUser = method.getAnnotation(CurrentUser.class);
					if (currentUser != null) {
						switch (currentUser.type()) {
						case CREATE:
							currentUserCreateProperty = method;
							break;
						case MODIFY:
							currentUserModifyProperty = method;
							break;
						}
					}
				}
			}
		}

		public boolean isSupported() {
			return this.supported;
		}
	}
}