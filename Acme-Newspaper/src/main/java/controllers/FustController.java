
package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.NewspaperService;
import services.FustService;
import domain.Newspaper;
import domain.Fust;

@Controller()
@RequestMapping("/fust")
public class FustController extends AbstractController {

	//Services
	@Autowired
	private FustService			fustService;

	@Autowired
	private NewspaperService	newspaperService;


	//Constructor
	public FustController() {
		super();
	}

	//List

	@RequestMapping("/list")
	public ModelAndView list(@RequestParam final int newspaperId) {
		final ModelAndView res = new ModelAndView("fust/list");
		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		final List<Fust> fustList = (List<Fust>) this.fustService.getFinalModeMomentAfter(newspaper);
		res.addObject("fustList", fustList);
		res.addObject("requestURI", "fust/list.do");
		return res;

	}

}
