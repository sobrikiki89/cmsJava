package module.report.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;

public abstract class ReportParamHandler {

	public static final String REPORT_DATETIME = "REPORT_DATETIME";
	public static final String REPORT_CREATOR = "REPORT_CREATOR";

	protected ApplicationContext context;
	protected Map<String, Object> paramMap;
	protected Map<String, List<? extends Object>> lookupMap;

	public ReportParamHandler() {
		paramMap = new LinkedHashMap<String, Object>();
		lookupMap = new LinkedHashMap<String, List<? extends Object>>();
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public Map<String, List<? extends Object>> getLookupMap() {
		return lookupMap;
	}

	public void setLookupMap(Map<String, List<? extends Object>> lookupMap) {
		this.lookupMap = lookupMap;
	}

	public abstract void setupLookup();

	public abstract String formatParams();

	public abstract void validateParams(BindingResult result);
}
