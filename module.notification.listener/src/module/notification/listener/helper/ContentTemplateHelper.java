package module.notification.listener.helper;

import org.apache.commons.lang.StringUtils;

public class ContentTemplateHelper {

	public static String replaceContent(String template, String key, String value) {
		if (template != null) {
			return template.replace(key, value);
		}
		return null;
	}

	/**
	 * Truncate recipientName field to only 15 chars
	 * 
	 * @param recipientName
	 * @return
	 */
	public static String truncRecipientName(String recipientName) {
		if (recipientName == null) {
			return StringUtils.EMPTY;
		}
		if (recipientName.length() > 15) {
			return recipientName.substring(0, 15);
		} else {
			return recipientName;
		}
	}
}