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
import services.FustService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Administrator;
import domain.Fust;

@Controller
@RequestMapping("/fust/administrator")
public class FustAdministratorController extends AbstractController {

	// Managed repository -----------------------------------------------------
	@Autowired
	private FustService			fustService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private LoginService		loginService;


	// Constructors -----------------------------------------------------------

	public FustAdministratorController() {
		super();
	}

	// List ---------------------------------------------------------------		

	@RequestMapping("/myList")
	public ModelAndView myList() {
		ModelAndView result;
		final Administrator a = (Administrator) this.loginService.getPrincipalActor();
		result = new ModelAndView("fust/list");
		result.addObject("fustList", this.fustService.getAvailable(a));
		result.addObject("requestURI", "fust/administrator/myList.do");
		return result;
	}
	//Creating------------------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		final ModelAndView modelAndView;

		final Fust n = this.fustService.create();
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int fustId) {
		final ModelAndView modelAndView;
		final Fust n = this.fustService.findOne(fustId);
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Fust c, final BindingResult binding) {
		ModelAndView modelAndView;

		c = this.fustService.reconstruct(c, binding);
		if (binding.hasErrors())
			modelAndView = this.createEditModelAndView(c);
		else
			try {
				this.fustService.save(c);
				modelAndView = new ModelAndView("redirect:/fust/administrator/myList.do");

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
	public ModelAndView delete(Fust c, final BindingResult binding) {
		ModelAndView modelAndView;

		c = this.fustService.reconstruct(c, binding);
		if (binding.hasErrors())
			modelAndView = this.createEditModelAndView(c);
		else
			try {
				this.fustService.delete(c);
				modelAndView = new ModelAndView("redirect:/fust/administrator/myList.do");
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

	protected ModelAndView createEditModelAndView(final Fust n) {
		ModelAndView result;
		result = this.createEditModelAndView(n, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Fust n, final String message) {
		ModelAndView result;
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(n.getAdministrator().equals(admin), "not.principal");
		result = new ModelAndView("fust/edit");
		result.addObject("fustList", this.fustService.getAvailable(admin));
		result.addObject("newspapers", this.newspaperService.getPublishedNewspapers());
		result.addObject("fust", n);
		result.addObject("fustBD", this.fustService.findOne(n.getId()));
		result.addObject("message", message);
		return result;
	}
}
