
package controllers.actor;

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
import services.ActorService;
import services.FolderService;
import services.MesageService;
import controllers.AbstractController;
import domain.Actor;
import domain.Folder;
import domain.Mesage;

@Controller
@RequestMapping("/message/actor")
public class MesageActorController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private MesageService	mesageService;

	@Autowired
	private LoginService	loginService;

	@Autowired
	private FolderService	folderService;

	@Autowired
	private ActorService	actorService;


	// Constructors -----------------------------------------------------------

	public MesageActorController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int folderId) {
		ModelAndView result;
		Collection<Mesage> ms;
		final Folder folder = this.folderService.findOne(folderId);
		Assert.isTrue(this.loginService.getPrincipalActor().getId() == folder.getActor().getId());
		ms = folder.getMesages();
		result = new ModelAndView("actor/message/list");
		result.addObject("mensajes", ms);
		result.addObject("requestURI", "message/actor/list.do");
		final Actor a = this.loginService.getPrincipalActor();
		result.addObject("actor", a);
		result.addObject("box", folder.getName());
		return result;
	}

	// Creation ---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Mesage mesage;
		final Actor logueado = this.loginService.getPrincipalActor();

		mesage = this.mesageService.create();
		mesage.setSender(logueado);
		for (final Folder f : logueado.getFolders())
			if (f.getName().equals("out box"))
				mesage.setFolder(f);
		result = new ModelAndView("actor/message/edit");
		result.addObject("mesage", mesage);
		result.addObject("priorities", this.mesageService.getPriorities());
		result.addObject("actors", this.actorService.findAll());

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "send")
	public ModelAndView send(@Valid final Mesage mesage, final BindingResult binding) {
		ModelAndView result;
		Assert.isTrue(this.loginService.getPrincipalActor().getId() == mesage.getFolder().getActor().getId());
		if (binding.hasErrors()) {
			result = new ModelAndView("actor/message/edit");
			result.addObject("mesage", mesage);
			result.addObject("actors", this.actorService.findAll());
			result.addObject("priorities", this.mesageService.getPriorities());
		} else
			try {
				this.mesageService.sendMesage(mesage);
				result = new ModelAndView("redirect:../../folder/actor/list.do");
				result.addObject("message", "message.commit.ok");
			} catch (final Throwable oops) {
				result = new ModelAndView("actor/message/edit");
				result.addObject("mesage", mesage);
				result.addObject("actors", this.actorService.findAll());
				result.addObject("priorities", this.mesageService.getPriorities());
				result.addObject("message", "message.commit.error");
			}

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ModelAndView details(@RequestParam final int messageId) {
		ModelAndView result;
		final Actor log = this.loginService.getPrincipalActor();
		final Mesage mesage = this.mesageService.findOne(messageId);
		Assert.isTrue(log.getId() == mesage.getFolder().getActor().getId());
		result = new ModelAndView("actor/message/details");
		result.addObject("mesage", mesage);

		return result;
	}

	@RequestMapping(value = "/move", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int messageId) {
		ModelAndView result;
		final Actor log = this.loginService.getPrincipalActor();
		final Mesage mesage = this.mesageService.findOne(messageId);
		Assert.isTrue(log.getId() == mesage.getFolder().getActor().getId());
		result = this.createMoveModelAndView(mesage);

		return result;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST, params = "save")
	public ModelAndView move(@Valid final Mesage mesage, final BindingResult binding) {
		ModelAndView result;
		Assert.isTrue(this.loginService.getPrincipalActor().getId() == mesage.getFolder().getActor().getId());
		if (binding.hasErrors())
			result = this.createMoveModelAndView(mesage);
		else
			try {
				this.mesageService.save(mesage);
				result = new ModelAndView("redirect:../../message/actor/list.do?folderId=" + mesage.getFolder().getId());
				// result.addObject("message", "message.commit.ok");
				result.addObject("box", mesage.getFolder().getName());
			} catch (final Throwable oops) {
				result = this.createMoveModelAndView(mesage, "message.commit.error");
				// result.addObject("mensaje", message);
				// result.addObject("actors", messageService.findAllActors());
				// result.addObject("priorities", messageService.getPriorities());
				// result.addObject("message", "message.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView save(@RequestParam final int messageId) {
		ModelAndView result = null;
		final Mesage mesage = this.mesageService.findOne(messageId);
		final Folder folder = mesage.getFolder();
		Assert.isTrue(this.loginService.getPrincipalActor().getId() == mesage.getFolder().getActor().getId());
		try {
			if (mesage.getFolder().getName().toLowerCase().equals("trash box"))
				this.mesageService.delete(mesage);
			else {
				final Folder f = this.folderService.getTrashbox(this.loginService.getPrincipalActor());
				mesage.setFolder(f);
				final List<Mesage> lm = f.getMesages();
				lm.add(mesage);
				f.setMesages(lm);
				this.folderService.save(f);
				this.mesageService.save(mesage);
			}
			result = new ModelAndView("redirect:../../message/actor/list.do?folderId=" + folder.getId());
			// result.addObject("message", "message.commit.ok");
			result.addObject("box", folder.getName());
		} catch (final Throwable oops) {
			System.out.println(oops.getMessage());
			result = this.createEditModelAndView(mesage, "message.commit.error");
		}

		return result;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Mesage mesage) {
		ModelAndView result;

		result = this.createEditModelAndView(mesage, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Mesage mensaje, final String message) {
		ModelAndView result;

		result = new ModelAndView("actor/message/edit");
		result.addObject("mesage", mensaje);
		result.addObject("message", message);
		final List<Folder> folders = mensaje.getFolder().getActor().getFolders();
		folders.remove(mensaje.getFolder());
		result.addObject("folders", folders);

		return result;
	}

	protected ModelAndView createMoveModelAndView(final Mesage mesage) {
		ModelAndView result;

		result = this.createMoveModelAndView(mesage, null);

		return result;
	}

	protected ModelAndView createMoveModelAndView(final Mesage mensaje, final String message) {
		ModelAndView result;

		result = new ModelAndView("actor/message/move");
		result.addObject("mensaje", mensaje);
		result.addObject("message", message);
		final List<Folder> folders = mensaje.getFolder().getActor().getFolders();
		folders.remove(mensaje.getFolder());
		result.addObject("folders", folders);

		return result;
	}

}
