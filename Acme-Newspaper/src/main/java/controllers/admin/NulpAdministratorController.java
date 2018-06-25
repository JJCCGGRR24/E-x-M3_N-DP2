/*
 * AdministratorController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.NewspaperService;
import services.NulpService;
import controllers.AbstractController;
import domain.Administrator;
import domain.Nulp;

@Controller
@RequestMapping("/nulp/administrator")
public class NulpAdministratorController extends AbstractController {

	// Managed repository -----------------------------------------------------
	@Autowired
	private NulpService			nulpService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private LoginService		loginService;


	// Constructors -----------------------------------------------------------

	public NulpAdministratorController() {
		super();
	}

	// List ---------------------------------------------------------------		

	@RequestMapping("/myList")
	public ModelAndView myList() {
		ModelAndView result;
		final Administrator a = (Administrator) this.loginService.getPrincipalActor();
		result = new ModelAndView("nulp/list");
		result.addObject("nulpList", this.nulpService.getAvailable(a));
		result.addObject("requestURI", "nulp/administrator/myList.do");
		return result;
	}
	//Creating------------------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		final ModelAndView modelAndView;

		final Nulp n = this.nulpService.create();
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int nulpId) {
		final ModelAndView modelAndView;
		final Nulp n = this.nulpService.findOne(nulpId);
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Nulp c, final BindingResult binding) {
		ModelAndView modelAndView;

		c = this.nulpService.reconstruct(c, binding);
		if (binding.hasErrors())
			modelAndView = this.createEditModelAndView(c);
		else
			try {
				this.nulpService.save(c);
				modelAndView = new ModelAndView("redirect:/nulp/administrator/myList.do");

			} catch (final Throwable e) {
				if (e.getMessage().equals("not.principal"))
					modelAndView = this.createEditModelAndView(c, "not.principal");
				else if (e.getMessage().equals("moment.before"))
					modelAndView = this.createEditModelAndView(c, "moment.before");
				else if (e.getMessage().equals("in.finalMode"))
					modelAndView = this.createEditModelAndView(c, "in.finalMode");
				else if (e.getMessage().equals("yet.asociated"))
					modelAndView = this.createEditModelAndView(c, "yet.asociated");
				else if (e.getMessage().equals("necessary.finalMode"))
					modelAndView = this.createEditModelAndView(c, "necessary.finalMode");
				else
					modelAndView = this.createEditModelAndView(c, "commit.error");
			}
		return modelAndView;
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Nulp c, final BindingResult binding) {
		ModelAndView modelAndView;

		c = this.nulpService.reconstruct(c, binding);
		if (binding.hasErrors())
			modelAndView = this.createEditModelAndView(c);
		else
			try {
				this.nulpService.delete(c);
				modelAndView = new ModelAndView("redirect:/nulp/administrator/myList.do");
			} catch (final Throwable e) {
				if (e.getMessage().equals("not.principal"))
					modelAndView = this.createEditModelAndView(c, "not.principal");
				if (e.getMessage().equals("in.finalMode"))
					modelAndView = this.createEditModelAndView(c, "in.finalMode");
				else
					modelAndView = this.createEditModelAndView(c, "commit.error");
			}
		return modelAndView;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Nulp n) {
		ModelAndView result;
		result = this.createEditModelAndView(n, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Nulp n, final String message) {
		ModelAndView result;
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(n.getAdministrator().equals(admin), "not.isprincipal");
		result = new ModelAndView("nulp/edit");
		result.addObject("nulpList", this.nulpService.getAvailable(admin));
		result.addObject("newspapers", this.newspaperService.getPublishedNewspapers());
		result.addObject("nulp", n);
		result.addObject("nulpBD", this.nulpService.findOne(n.getId()));
		result.addObject("message", message);
		return result;
	}
}
