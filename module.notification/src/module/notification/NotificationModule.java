package module.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.core.registry.Module;
import module.notification.service.EmailContentService;

@Component("NotificationModule")
public class NotificationModule extends Module {
	@Autowired
	private EmailContentService emailContentService;

	@Override
	protected void init() throws Exception {
		emailContentService.initializeEmailContent();
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Notification Module";
	}
}
