
package controllers.user;

import java.util.Collection;
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
import services.NewspaperService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Customer;
import domain.Newspaper;
import domain.User;
import domain.Volume;

@Controller
@RequestMapping("/volume/user")
public class VolumeUserController extends AbstractController {

	//Services ---------------------------------------------------------------

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private VolumeService		volumeService;

	@Autowired
	private LoginService		loginService;


	// Constructors -----------------------------------------------------------
	public VolumeUserController() {
		super();
	}

	@RequestMapping(value = "/myList", method = RequestMethod.GET)
	public ModelAndView list() {
		final ModelAndView res = new ModelAndView("volume/list");

		final Collection<Volume> volumes = ((User) this.loginService.getPrincipalActor()).getVolumes();

		res.addObject("volumes", volumes);
		res.addObject("requestURI", "volume/all/list.do");
		if (LoginService.isPrincipalCustomer())
			res.addObject("mySubs", this.volumeService.volumesByCustomer(this.loginService.getPrincipalActor()));
		return res;
	}

	@RequestMapping("/delete")
	public ModelAndView deleteNewspaper(@RequestParam final int volumeId, @RequestParam final int newspaperId) {
		final ModelAndView res = new ModelAndView("redirect:/volume/all/listNewspapers.do?volumeId=" + volumeId);
		final Volume v = this.volumeService.findOne(volumeId);
		final Newspaper n = this.newspaperService.findOne(newspaperId);
		Assert.isTrue(this.loginService.getPrincipalActor().getId() == v.getUser().getId());
		v.getNewspapers().remove(n);

		n.getVolumes().remove(v);
		this.newspaperService.rawSaveForVolumes(n);
		this.volumeService.save(v);
		res.addObject("delete", v.getUser().getId() == this.loginService.getPrincipalActor().getId());
		res.addObject("volume", v);

		return res;
	}
	// Create ---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Volume e;

		e = this.volumeService.create();
		result = this.createEditModelAndView(e);

		return result;
	}

	// Edit ---------------------------------------------------------------
	//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	//	public ModelAndView editEndorser(@RequestParam final int jisitId) {
	//		ModelAndView result;
	//		Jisit e;
	//
	//		e = this.jisitService.findOne(jisitId);
	//		Assert.notNull(e);
	//		result = this.createEditModelAndView(e);
	//
	//		return result;
	//	}

	@RequestMapping(value = "/addNewspaper", method = RequestMethod.GET)
	public ModelAndView add(@RequestParam final int volumeId) {
		ModelAndView result;

		final Volume volume = this.volumeService.findOne(volumeId);

		final List<Newspaper> l = (List<Newspaper>) volume.getNewspapers();

		result = new ModelAndView("volume/add");
		volume.setNewspapers(l);
		result.addObject("volumeId", volumeId);
		final List<Newspaper> n = this.newspaperService.getPublishedNewspapers();
		n.removeAll(volume.getNewspapers());
		result.addObject("newwspapers", n);
		if (LoginService.isPrincipalCustomer()) {
			final Customer c = (Customer) this.loginService.getPrincipalActor();
			final Collection<Newspaper> newspapersSubs = this.newspaperService.getNewspaperSubscribes(c);
			result.addObject("newspapersSubs", newspapersSubs);
		}
		return result;
	}

	@RequestMapping(value = "/addNewspaper2")
	public ModelAndView add(@RequestParam final int newspaperId, @RequestParam final int volumeId) {

		ModelAndView result = null;

		if (newspaperId == 0) {
			final Volume volume = this.volumeService.findOne(volumeId);
			final List<Newspaper> l = (List<Newspaper>) volume.getNewspapers();
			result = new ModelAndView("volume/add");
			volume.setNewspapers(l);
			result.addObject("volumeId", volumeId);
			final List<Newspaper> n = this.newspaperService.getPublishedNewspapers();
			n.removeAll(volume.getNewspapers());
			result.addObject("newwspapers", n);
			if (LoginService.isPrincipalCustomer()) {
				final Customer c = (Customer) this.loginService.getPrincipalActor();
				final Collection<Newspaper> newspapersSubs = this.newspaperService.getNewspaperSubscribes(c);
				result.addObject("newspapersSubs", newspapersSubs);
			}
			result.addObject("message", "add.newspaper.error");
		} else {
			final Volume volume = this.volumeService.findOne(volumeId);
			final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
			final List<Newspaper> l = (List<Newspaper>) volume.getNewspapers();
			newspaper.getVolumes().add(volume);
			l.add(newspaper);
			volume.setNewspapers(l);
			this.volumeService.save(volume);
			result = new ModelAndView("redirect:/volume/all/listNewspapers.do?volumeId=" + volumeId);
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveVolume(@Valid final Volume e, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(e);
		else
			try {
				this.volumeService.save(e);
				result = new ModelAndView("redirect:/volume/all/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(e, "general.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView deleteVolume(final Volume e, final BindingResult binding) {
		ModelAndView result;

		try {
			this.volumeService.delete(e);
			result = new ModelAndView("redirect:/volume/all/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(e, "general.commit.error");
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Volume e) {
		ModelAndView result;

		result = this.createEditModelAndView(e, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Volume e, final String message) {
		ModelAndView result;

		result = new ModelAndView("volume/edit");
		result.addObject("volume", e);
		result.addObject("message", message);
		final List<Newspaper> newspapers = this.newspaperService.getPublishedNewspapers();

		result.addObject("newspapers", newspapers);
		if (LoginService.isPrincipalCustomer()) {
			final Customer c = (Customer) this.loginService.getPrincipalActor();
			final Collection<Newspaper> newspapersSubs = this.newspaperService.getNewspaperSubscribes(c);
			result.addObject("newspapersSubs", newspapersSubs);
		}
		result.addObject("volumeId", e.getId());
		result.addObject("requestURI", "volume/user/edit.do");

		return result;
	}
}
