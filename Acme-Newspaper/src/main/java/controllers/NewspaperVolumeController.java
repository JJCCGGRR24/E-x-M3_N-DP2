
package controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.VolumeService;
import domain.Newspaper;
import domain.Volume;

@Controller
@RequestMapping("/newspaper/volume")
public class NewspaperVolumeController extends AbstractController {

	//Services ---------------------------------------------------------------
	@Autowired
	private VolumeService	volumeService;


	// Constructors -----------------------------------------------------------
	public NewspaperVolumeController() {
		super();
	}

	// List ---------------------------------------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int volumeId) {
		final ModelAndView res = new ModelAndView("newspaper/list");

		final Volume volume = this.volumeService.findOne(volumeId);
		final ArrayList<Newspaper> collection = new ArrayList<>(volume.getNewspapers());
		final ArrayList<Newspaper> newspapers = new ArrayList<>();

		for (final Newspaper newspaper : collection)
			if (newspaper.getDeprived() == false && newspaper.getPublicationDate() != null)
				newspapers.add(newspaper);

		res.addObject("newspapers", newspapers);
		res.addObject("requestURI", "newspaper/volume/list.do?volumeId=" + volumeId);

		return res;
	}

}
