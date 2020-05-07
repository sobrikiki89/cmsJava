package web.core.controller;

import java.io.Serializable;

import org.springframework.web.bind.annotation.ModelAttribute;

import app.core.spring.UrlPattern;

public abstract class BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	@ModelAttribute("url_param_prefix")
	public String getUrlParamPrefix() {
		return UrlPattern.PARAM_PREFIX;
	}
}
