package module.report.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import module.report.model.GeneratorType;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportItem {

	String id();

	String name();

	String category();

	GeneratorType generatorType();

	String jspPath();

	String handlerClass();

	OutputFormat[] format();
}
