
package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ComodinService;
import services.NewspaperService;
import domain.Comodin;
import domain.Newspaper;

@Controller()
@RequestMapping("/comodin")
public class ComodinController extends AbstractController {

	//Services
	@Autowired
	private ComodinService		comodinService;

	@Autowired
	private NewspaperService	newspaperService;


	//Constructor
	public ComodinController() {
		super();
	}

	//List

	@RequestMapping("/list")
	public ModelAndView list(@RequestParam final int newspaperId) {
		final ModelAndView res = new ModelAndView("comodin/list");
		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		final List<Comodin> comodines = (List<Comodin>) this.comodinService.getFinalModeMomentAfter(newspaper);
		res.addObject("comodines", comodines);
		res.addObject("requestURI", "comodin/list.do");
		return res;

	}

}
