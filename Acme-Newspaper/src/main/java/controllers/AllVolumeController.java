
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.VolumeService;
import domain.Volume;

@Controller
@RequestMapping("/volume/all")
public class AllVolumeController extends AbstractController {

	//Services ---------------------------------------------------------------
	@Autowired
	private VolumeService	volumeService;

	@Autowired
	private LoginService	loginService;


	// Constructors -----------------------------------------------------------
	public AllVolumeController() {
		super();
	}

	// List ---------------------------------------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		final ModelAndView res = new ModelAndView("volume/list");

		final Collection<Volume> volumes = this.volumeService.findAll();

		res.addObject("volumes", volumes);
		res.addObject("requestURI", "volume/all/list.do");
		if (LoginService.isPrincipalCustomer())
			res.addObject("mySubs", this.volumeService.volumesByCustomer(this.loginService.getPrincipalActor()));
		return res;
	}

}
