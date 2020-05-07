package app.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.TemporalType;


/**
 * Annotate property for Hibernate interceptor to inject timestamp.
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentTemporal {
    TemporalType value() default TemporalType.TIMESTAMP;

    PersistenceType type();
}
