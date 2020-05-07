package web.module.claim.model;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import module.claim.model.ClaimStatusEnum;

public class ClaimStatusEnumConverter extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isNotBlank(text)) {
			String upperCaseText = text.toUpperCase();
			setValue(ClaimStatusEnum.valueOf(upperCaseText));
		}
		else {
			setValue(null);
		}
	}
}
