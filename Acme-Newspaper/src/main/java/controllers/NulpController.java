
package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.NewspaperService;
import services.NulpService;
import domain.Newspaper;
import domain.Nulp;

@Controller()
@RequestMapping("/nulp")
public class NulpController extends AbstractController {

	//Services
	@Autowired
	private NulpService			nulpService;

	@Autowired
	private NewspaperService	newspaperService;


	//Constructor
	public NulpController() {
		super();
	}

	//List

	@RequestMapping("/list")
	public ModelAndView list(@RequestParam final int newspaperId) {
		final ModelAndView res = new ModelAndView("nulp/list");
		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		final List<Nulp> nulpList = (List<Nulp>) this.nulpService.getFinalModeMomentAfter(newspaper);
		res.addObject("nulpList", nulpList);
		res.addObject("requestURI", "nulp/list.do");
		return res;

	}

}
