package app.core.security.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionCreatedEvent;

public class HttpSessionCreatedListener implements ApplicationListener<HttpSessionCreatedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionCreatedListener.class);

	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	private int sessionCount = 0;

	@Override
	public void onApplicationEvent(HttpSessionCreatedEvent event) {
		List<Object> activeUsers = sessionRegistry.getAllPrincipals();

		LOGGER.info("#### Session created with id [" + event.getSession().getId() + "]");
		LOGGER.info("#### Total current login user is [" + activeUsers.size() + "]");
		LOGGER.info("#### Total session created so far is [" + ++sessionCount + "]");
		// TODO: can put in additional action items
	}
}
