/*
 * AdministratorController.java
 *
 * Copyright (C) 2017 Universidad de Sevilla
 *
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers.customer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import controllers.AbstractController;
import domain.SubscribeVol;
import services.SubscribeVolService;

@Controller
@RequestMapping("/subscribeVol/customer")
public class SubscribeVolCustomerController extends AbstractController {

	// Managed repository -----------------------------------------------------
	@Autowired
	private SubscribeVolService subscribeVolService;


	// Constructors -----------------------------------------------------------

	public SubscribeVolCustomerController() {
		super();
	}

	//Creating------------------------------------------------------------------------

	@RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	public ModelAndView subscribe(@RequestParam final int volumeId) {
		final ModelAndView modelAndView;
		final SubscribeVol n = this.subscribeVolService.create(volumeId);
		modelAndView = this.createEditModelAndView(n);
		return modelAndView;
	}

	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final SubscribeVol c, final BindingResult binding) {
		ModelAndView modelAndView;
		final String error = this.subscribeVolService.validate(c);
		if (binding.hasErrors() || error != null)
			modelAndView = this.createEditModelAndView(c, error);
		else
			try {
				this.subscribeVolService.save(c);
				modelAndView = new ModelAndView("redirect:/newspaper/volume/list.do?volumeId=" + c.getVolume().getId());
			} catch (final Exception e) {
				modelAndView = this.createEditModelAndView(c, "commit.error");
			}
		return modelAndView;
	}
	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final SubscribeVol subscribe) {
		ModelAndView result;

		result = this.createEditModelAndView(subscribe, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final SubscribeVol subscribe, final String message) {
		ModelAndView result;

		result = new ModelAndView("subscribe/editVol");
		result.addObject("subscribeVol", subscribe);
		result.addObject("message", message);

		return result;
	}

}
