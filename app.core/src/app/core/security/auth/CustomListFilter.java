package app.core.security.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.filter.GenericFilterBean;

public class CustomListFilter extends GenericFilterBean implements ApplicationEventPublisherAware, MessageSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomListFilter.class);

	private List<AbstractAuthenticationProcessingFilter> filterList = new ArrayList<AbstractAuthenticationProcessingFilter>();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException,
			ServletException {
		FilterChain filterChain = new FilterChain() {

			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
				chain.doFilter(arg0, arg1);

			}
		};
		Vector<FilterChain> filterChains = new Vector<FilterChain>();
		filterChains.add(filterChain);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Filtering {} filters", filterList.size());
		}
		for (final GenericFilterBean filter : filterList) {
			final FilterChain lastChain = filterChains.lastElement();
			FilterChain loopChain = new FilterChain() {

				@Override
				public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("running filter {}", filter.getClass().getName());
					}
					filter.doFilter(arg0, arg1, lastChain);
				}
			};
			filterChains.add(loopChain);
		}
		filterChains.lastElement().doFilter(request, response);
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		for (MessageSourceAware filter : filterList) {
			filter.setMessageSource(messageSource);
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		for (ApplicationEventPublisherAware applicationEventPublisherAware : filterList) {
			applicationEventPublisherAware.setApplicationEventPublisher(applicationEventPublisher);
		}
	}

	public List<AbstractAuthenticationProcessingFilter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<AbstractAuthenticationProcessingFilter> filterList) {
		this.filterList = filterList;
		Collections.reverse(this.filterList);
	}
}