package app.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> T create(final Class<T> superType, final String className) {
		try {
			final Class<?> clazz = Class.forName(className);
			final Object object = clazz.newInstance();
			if (superType.isInstance(object)) {
				LOGGER.info("Instance of class [" + className + "] has been created.");
				return (T) object; // safe cast
			} else {
				LOGGER.error(
						"Given class [" + className + "] is not sub-class / instance of [" + superType.getName() + "]");
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error("Please ensure the given [" + className + "] is loaded in the classpath.", e);
		} catch (InstantiationException e) {
			LOGGER.error("Unable to instantiate [" + className + "]", e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Unable to access class [" + className + "]", e);
		} catch (Exception e) {
			LOGGER.error("Error when instantiate [" + className + "]", e);
		}

		return null;
	}
}
