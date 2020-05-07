package module.notification.object;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import module.notification.constant.JMSMessageConstant;

public class JMSMessage {

	private Map<String, Object> header;

	private Object body;

	private Map<String, byte[]> attachment;

	public JMSMessage() {
		header = new HashMap<>();
		attachment = new HashMap<>();
	}

	public Map<String, Object> getHeader() {
		return header;
	}

	public void addHeader(String key, String value) {
		header.put(key, value);
	}

	public Object getBody() {
		return body;
	}

	public Object getMergedBody() {
		Map<String, Object> bodyMap = new LinkedHashMap<>();
		bodyMap.put(JMSMessageConstant.INTERNAL_XML, body);

		if (getAttachment() != null && getAttachment().size() > 0) {
			for (Map.Entry<String, byte[]> entry : getAttachment().entrySet()) {
				if (entry.getValue() != null) {
					bodyMap.put(entry.getKey(), entry.getValue());
				}
			}
		}

		return bodyMap;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public void addAttachment(String key, byte[] value) {
		attachment.put(key, value);
	}

	public Map<String, byte[]> getAttachment() {
		return attachment;
	}
}