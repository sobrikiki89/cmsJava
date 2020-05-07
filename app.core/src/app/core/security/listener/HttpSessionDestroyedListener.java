package app.core.security.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import app.core.service.UserDetailService;

public class HttpSessionDestroyedListener implements ApplicationListener<HttpSessionDestroyedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionDestroyedListener.class);

	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	@Autowired
	private UserDetailService userService;

	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		userService.updateLogoutInfo(event.getId());
		LOGGER.info("#### [Logout] Session destroyed, logout info updated, Session Id [" + event.getId() + "]");
	}
}
