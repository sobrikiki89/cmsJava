package module.notification.listener;

import org.springframework.stereotype.Component;

import app.core.registry.Module;

@Component("NotificationListenerModule")
public class NotificationListenerModule extends Module {
	@Override
	protected void init() throws Exception {
		// Initialization
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Notification Listener Module";
	}
}
