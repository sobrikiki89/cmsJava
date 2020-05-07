package app.core.domain;

import org.springframework.stereotype.Component;

import app.core.registry.Module;

@Component("DomainModule")
public class DomainModule extends Module {

	@Override
	protected void init() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public String getModuleName() {
		return "[CORE] Domain Module ";
	}

}