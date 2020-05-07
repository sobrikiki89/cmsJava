package module.upload;

import org.springframework.stereotype.Component;

import app.core.registry.Module;

@Component("UploadModule")
public class UploadModule extends Module {

	@Override
	protected void init() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Upload Module";
	}

}
