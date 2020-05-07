package web.core.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String getIndexPage(HttpServletRequest req, Model model) {
		// You may add in any logic here which you want it to be executed before
		// rendering the page
		// logger.info("This is a index page controller.");
		// return "public/indexpage";
		logger.info("This is a login page controller.");
		return "public/loginpage";
	}
	

	@RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
	public String getUnauthorizedPage(HttpServletRequest req, Model model) {
		return "public/unauthorized";
	}
}
