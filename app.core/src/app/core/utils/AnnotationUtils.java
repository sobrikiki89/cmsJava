package app.core.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

public class AnnotationUtils {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationUtils.class);

	private AnnotationUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns all the methods along the class hierarchy which have the
	 * specified annotation(s).
	 * 
	 * @param annotation
	 *            Annotation to check against.
	 * @param clazz
	 *            Class to inspect.
	 * @return
	 */
	public static Method[] findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation>... annotations) {
		Set<Method> annotated = new HashSet<Method>();
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			for (Class<? extends Annotation> anno : annotations) {
				if (m.isAnnotationPresent(anno)) {
					annotated.add(m);
				}
			}
		}
		return annotated.toArray(new Method[] {});
	}

	/**
	 * Returns the methods which have the specified annotation(s).
	 * 
	 * @param annotation
	 *            Annotation to check against.
	 * @param clazz
	 *            Class to inspect.
	 * @return
	 */
	public static Method[] findAnnoatatedDeclaredMethods(Class<?> clazz, Class<? extends Annotation>... annotations) {
		Set<Method> annotated = new HashSet<Method>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods) {
			for (Class<? extends Annotation> anno : annotations) {
				if (m.isAnnotationPresent(anno)) {
					annotated.add(m);
				}
			}
		}
		return annotated.toArray(new Method[] {});
	}

	/**
	 * Return the fields which have the specified annotation.
	 * 
	 * @param annotation
	 * @param clazz
	 * @return
	 */
	public static Field[] findAnnotatedFields(Class<?> clazz, Class<? extends Annotation>... annotations) {
		Set<Field> annotated = new HashSet<Field>();
		Field[] fields = clazz.getFields();
		for (Field f : fields) {
			for (Class<? extends Annotation> anno : annotations) {
				if (f.isAnnotationPresent(anno)) {
					annotated.add(f);
				}
			}
		}
		return annotated.toArray(new Field[] {});
	}

	/**
	 * Return the fields along the class hierarchy which have the specified
	 * annotation(s).
	 * 
	 * @param annotation
	 * @param clazz
	 * @return
	 */
	public static Field[] findAnnotatedDeclaredFields(Class<?> clazz, Class<? extends Annotation>... annotations) {
		Set<Field> annotated = new HashSet<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			for (Class<? extends Annotation> anno : annotations) {
				if (f.isAnnotationPresent(anno)) {
					annotated.add(f);
				}
			}
		}
		return annotated.toArray(new Field[] {});
	}

	public static String getPatternForPackage(String packageName) {
		if (packageName == null)
			packageName = "";
		packageName = packageName.replace('.', '/');
		if (!packageName.endsWith("/")) {
			packageName += '/';
		}

		return "classpath*:" + packageName + "**/*.class";
	}

	/**
	 * Scan package and find all class which have the specified annotation.
	 * 
	 * @param package name to scan
	 * @param annotation
	 *            to find for.
	 */
	public static List<Class<?>> scanForPackageName(String packageName, Class<? extends Annotation> annotationClass,
			ClassLoader classLoader) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		String packagePattern = AnnotationUtils.getPatternForPackage(packageName);
		packagePattern = packagePattern.replace('\\', '/');

		PathMatchingResourcePatternResolver match = new PathMatchingResourcePatternResolver();
		try {
			Resource[] resources = match.getResources(packagePattern);
			MetadataReaderFactory f = new SimpleMetadataReaderFactory();
			for (Resource r : resources) {
				MetadataReader meta = f.getMetadataReader(r);
				AnnotationMetadata anno = meta.getAnnotationMetadata();
				Set<String> types = anno.getAnnotationTypes();
				if (types.contains(annotationClass.getName())) {

					try {
						Class<?> clazz = classLoader.loadClass(anno.getClassName());
						classes.add(clazz);
					} catch (ClassNotFoundException e) {
						logger.error(e.getMessage(), e);
					}

				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return classes;
	}

}
