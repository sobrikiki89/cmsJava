package app.core.json;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class JsonDateSerializer extends JsonSerializer<Date> implements ContextualSerializer {
	private String format;

	private JsonDateSerializer(final String format) {
		this.format = format;
	}

	public JsonDateSerializer() {
		this.format = AppConstant.DATE_FORMAT;
	}

	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		gen.writeString(new SimpleDateFormat(format).format(date));
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider arg0, BeanProperty beanProperty)
			throws JsonMappingException {
		final AnnotatedElement annotated = beanProperty.getMember().getAnnotated();
		if (annotated.isAnnotationPresent(DateFormat.class)) {
			return new JsonDateSerializer(annotated.getAnnotation(DateFormat.class).value());
		} else {
			return new JsonDateSerializer();
		}
	}
}
