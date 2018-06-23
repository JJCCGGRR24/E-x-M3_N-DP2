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

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ComodinService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Administrator;
import domain.Comodin;

@Controller
@RequestMapping("/comodin/administrator")
public class ComodinAdministratorController extends AbstractController {

	// Managed repository -----------------------------------------------------
	@Autowired
	private ComodinService		comodinService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private LoginService		loginService;


	// Constructors -----------------------------------------------------------

	public ComodinAdministratorController() {
		super();
	}

	// List ---------------------------------------------------------------		

	@RequestMapping("/myList")
	public ModelAndView myList() {
		ModelAndView result;
		final Administrator a = (Administrator) this.loginService.getPrincipalActor();
		final List<Comodin> comodines = (List<Comodin>) a.getComodines();
		result = new ModelAndView("comodin/list");
		result.addObject("comodines", comodines);
		result.addObject("requestURI", "comodin/administrator/myList.do");
		return result;
	}
	//Creating------------------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		final ModelAndView modelAndView;
		final Comodin n = this.comodinService.create();
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int comodinId) {
		final ModelAndView modelAndView;
		final Comodin n = this.comodinService.findOne(comodinId);
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Comodin c, final BindingResult binding) {
		ModelAndView modelAndView;
		try {
			c = this.comodinService.reconstruct(c, binding);
			if (binding.hasErrors())
				modelAndView = this.createEditModelAndView(c);
			else {
				this.comodinService.save(c);
				modelAndView = new ModelAndView("redirect:/comodin/administrator/myList.do");
			}
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
	public ModelAndView delete(@Valid final Comodin c, final BindingResult binding) {
		ModelAndView modelAndView;
		if (binding.hasErrors())
			modelAndView = this.createEditModelAndView(c);
		else
			try {
				this.comodinService.delete(c);
				modelAndView = new ModelAndView("redirect:/comodin/administrator/myList.do");
			} catch (final Throwable e) {
				if (e.getMessage().equals("in.finalMode"))
					modelAndView = this.createEditModelAndView(c, "in.finalMode");
				else
					modelAndView = this.createEditModelAndView(c, "commit.error");
			}
		return modelAndView;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Comodin n) {
		ModelAndView result;
		result = this.createEditModelAndView(n, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Comodin n, final String message) {
		ModelAndView result;
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(n.getAdministrator().equals(admin));
		result = new ModelAndView("comodin/edit");
		result.addObject("comodines", admin.getComodines());
		result.addObject("newspapers", this.newspaperService.getPublishedNewspapers());
		result.addObject("comodin", n);
		result.addObject("comodinBD", this.comodinService.findOne(n.getId()));
		result.addObject("message", message);
		return result;
	}
}
