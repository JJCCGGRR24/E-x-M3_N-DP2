
package controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.MesageService;
import domain.Actor;
import domain.Mesage;
import forms.MessageForm;

@Controller
@RequestMapping("/message")
public class MessageController extends AbstractController {

	//Services-----------------------------------------------------------------------

	@Autowired
	private MesageService	messageService;

	@Autowired
	private ActorService	actorService;


	//Constructor--------------------------------------------------------------------

	public MessageController() {
		super();
	}

	//Listing------------------------------------------------------------------------

	//	@RequestMapping(value = "/list", method = RequestMethod.GET)
	//	public ModelAndView list() {
	//		final ModelAndView modelAndView;
	//
	//		final Collection<Advertisement> advertisements = this.advertisementService.getAdvertisementsWithTabbooWords();
	//
	//		modelAndView = new ModelAndView("advertisement/list");
	//		modelAndView.addObject("advertisements", advertisements);
	//		modelAndView.addObject("requestURI", "advertisement/admin/list.do");
	//
	//		return modelAndView;
	//	}

	@RequestMapping(value = "/sent", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView r = new ModelAndView();
		final MessageForm messForm = new MessageForm();
		r = this.createEditModelAndView(messForm);
		r.addObject("requestURI", "message/sent.do");
		r.addObject("actors", this.actorService.findAll());

		return r;
	}
	@RequestMapping(value = "/sent", method = RequestMethod.POST, params = "save")
	public ModelAndView sent(@Valid final MessageForm messageForm, final BindingResult bindingResult) {

		final Mesage a = this.messageService.reconstruct(messageForm);
		ModelAndView res;

		if (bindingResult.hasErrors())
			res = this.createEditModelAndView(messageForm, "message.commit.error");
		else
			try {
				this.messageService.sendMesage(a);
				res = new ModelAndView("redirect:/folder/actor/list.do");
			} catch (final Throwable oops) {
				res = this.createEditModelAndView(messageForm, "message.commit.error");
			}

		return res;
	}
	protected ModelAndView createEditModelAndView(final MessageForm r) {
		ModelAndView result;
		result = this.createEditModelAndView(r, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final MessageForm a, final String message) {
		ModelAndView result;
		result = new ModelAndView();
		result.addObject("requestURI", "message/sent.do");

		//		result.addObject("requestURI", "message/sent.do");
		final Collection<Actor> actors = this.actorService.findAll();
		result.addObject("actors", actors);
		result.addObject("message", message);
		result.addObject("messageForm", a);

		return result;
	}
}
