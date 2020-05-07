package app.core.json;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import app.core.utils.AppConstant;

@Component
public class JsonNumberSerializer extends JsonSerializer<BigDecimal> implements ContextualSerializer {
	private String format;

	private JsonNumberSerializer(final String format) {
		this.format = format;
	}

	public JsonNumberSerializer() {
		this.format = AppConstant.NUMBER_FORMAT;
	}

	@Override
	public void serialize(BigDecimal number, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		gen.writeString(new DecimalFormat(format).format(number));
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider arg0, BeanProperty beanProperty)
			throws JsonMappingException {
		final AnnotatedElement annotated = beanProperty.getMember().getAnnotated();
		if (annotated.isAnnotationPresent(NumberFormat.class)) {
			return new JsonNumberSerializer(annotated.getAnnotation(NumberFormat.class).value());
		} else {
			return new JsonNumberSerializer();
		}
	}
}
