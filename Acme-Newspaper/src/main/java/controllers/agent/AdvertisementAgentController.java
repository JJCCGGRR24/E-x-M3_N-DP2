/*
 * AdministratorController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers.agent;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.AdvertisementService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Advertisement;
import domain.Agent;

@Controller
@RequestMapping("/advertisement/agent")
public class AdvertisementAgentController extends AbstractController {

	// Managed repository -----------------------------------------------------
	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private LoginService			loginService;


	// Constructors -----------------------------------------------------------

	public AdvertisementAgentController() {
		super();
	}

	//List------------------------------------------------------------------------

	@RequestMapping("/myList")
	public ModelAndView list() {
		final ModelAndView res = new ModelAndView("advertisement/list");
		final Agent c = (Agent) this.loginService.getPrincipalActor();
		final List<Advertisement> advs = (List<Advertisement>) c.getAdvertisements();
		res.addObject("advertisements", advs);
		res.addObject("requestURI", "advertisement/agent/myList.do");
		return res;

	}
	//Creating------------------------------------------------------------------------

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		final ModelAndView modelAndView;
		final Advertisement n = this.advertisementService.create();
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Advertisement c, final BindingResult binding) {
		ModelAndView modelAndView;
		final String error = this.advertisementService.validate(c);
		if (binding.hasErrors() || error != null)
			modelAndView = this.createEditModelAndView(c, error);
		else
			try {
				this.advertisementService.save(c);
				modelAndView = new ModelAndView("redirect:/advertisement/agent/myList.do");
			} catch (final Exception e) {

				modelAndView = this.createEditModelAndView(c, "commit.error");
			}
		return modelAndView;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Advertisement adv) {
		ModelAndView result;

		result = this.createEditModelAndView(adv, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Advertisement adv, final String message) {
		ModelAndView result;

		result = new ModelAndView("advertisement/edit");
		result.addObject("newspapers", this.newspaperService.getPublishedNewspapers());
		result.addObject("message", message);
		result.addObject("advertisement", adv);

		result.addObject("requestURI", "advertisement/agent/register.do");

		return result;
	}

}
