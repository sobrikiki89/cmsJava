package app.core.service;

import app.core.registry.Module;

public interface FunctionService {
	public void updateFunctions(Module module);

	public boolean isAuthorizedByPath(Long roleId, String path);

	public boolean isAuthorizedByCode(Long roleId, String code);
}
