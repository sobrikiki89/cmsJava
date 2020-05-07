package web.core.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import app.core.security.ConcurrenLoginException;

@Controller
public class LoginController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		return "public/loginpage";
	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginError(Locale locale, Model model) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(false);
		AuthenticationException authException = null;
		if (session != null
				&& (authException = (AuthenticationException) (session
						.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION))) != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("");
			}

			ResourceBundle rb = ResourceBundle.getBundle("messages", locale);

			// In order not to disclose too much info to the end-user we have to
			// some how map the message to more general / generic message

			String msg = rb.getString("login.error.general");

			if (authException instanceof BadCredentialsException) {
				// Invalid username or password
				msg = rb.getString("login.error.invalid_username_password");
			}

			if (authException instanceof UsernameNotFoundException) {
				// no user found
				// Invalid username or password
				msg = rb.getString("login.error.invalid_username_password");
			}

			if (authException instanceof DisabledException) {
				msg = rb.getString("login.error.acount_disabled");
			}

			if (authException instanceof LockedException) {
				msg = rb.getString("login.error.account_locked");
			}

			if (authException instanceof ConcurrenLoginException) {
				msg = rb.getString("login.error.concurrent_login");
			}

			model.addAttribute("error", true);
			model.addAttribute("errormsg", msg);
		}

		return "public/loginpage";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model) {
		return "public/loginpage";
	}
}
