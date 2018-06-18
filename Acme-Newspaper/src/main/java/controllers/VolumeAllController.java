
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.NewspaperService;
import services.VolumeService;
import domain.Customer;
import domain.Newspaper;
import domain.Volume;

@Controller
@RequestMapping("/volume/all")
public class VolumeAllController extends AbstractController {

	//Services ---------------------------------------------------------------

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private VolumeService		volumeService;

	@Autowired
	private LoginService		loginService;


	// Constructors -----------------------------------------------------------
	public VolumeAllController() {
		super();
	}

	@RequestMapping("/listNewspapers")
	public ModelAndView list(@RequestParam final int volumeId) {
		final ModelAndView res = new ModelAndView("volume/newspapers");
		final Volume volume = this.volumeService.findOne(volumeId);
		res.addObject("volume", volume);
		res.addObject("newspapers", volume.getNewspapers());
		if (LoginService.isPrincipalCustomer()) {
			final Customer c = (Customer) this.loginService.getPrincipalActor();
			final Collection<Newspaper> newspapersSubs = this.newspaperService.getNewspaperSubscribes(c);
			res.addObject("newspapersSubs", newspapersSubs);
		}
		res.addObject("requestURI", "volume/all/listNewspapers.do");
		return res;

	}
}
