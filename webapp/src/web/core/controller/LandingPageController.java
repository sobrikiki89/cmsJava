package web.core.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/secured")
public class LandingPageController extends BaseController {

	private static final long serialVersionUID = 1L;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getLandingPage(HttpServletRequest req, Model model) {
		return "secured.landing";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public String keepAlive(HttpServletRequest req, Model model) {
		return "done";
	}
}
