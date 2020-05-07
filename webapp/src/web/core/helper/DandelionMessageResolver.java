package web.core.helper;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.github.dandelion.core.util.StringUtils;
import com.github.dandelion.datatables.extras.spring3.i18n.SpringMessageResolver;

public class DandelionMessageResolver extends SpringMessageResolver {

	// Logger
	private static Logger logger = LoggerFactory.getLogger(DandelionMessageResolver.class);

	private MessageSource messageSource;

	public DandelionMessageResolver() {
	}

	public DandelionMessageResolver(HttpServletRequest request) {
		super(request);

		// Retrieve the Spring messageSource bean
		messageSource = RequestContextUtils.findWebApplicationContext(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResource(String messageKey, String defaultValue, Object... objects) {

	      //Locale locale = RequestContextUtils.getLocale(request);
	      Locale locale = LocaleContextHolder.getLocale();
	      
	      String message = null;

	      // Both title and titleKey attributes are not used
	      if (messageKey == null || StringUtils.isBlank(messageKey) && StringUtils.isNotBlank(defaultValue)) {
	         message = StringUtils.capitalize(defaultValue);
	      }
	      // the titleKey attribute is used
	      else {
	         try {
	            message = messageSource.getMessage(messageKey, null, locale);
	         }
	         catch (NoSuchMessageException e) {
	            logger.warn("No message found with the key {} and locale {}.", messageKey, locale);
	            if (StringUtils.isBlank(message)) {
	               message = UNDEFINED_KEY + messageKey + UNDEFINED_KEY;
	            }
	         }
	      }

	      return message;
	}
}
