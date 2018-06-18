
package usecases.test2;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.AdvertisementService;
import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Newspaper;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase10_1Test extends AbstractTest {

	//SUT

	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private VolumeService			volumeService;

	@Autowired
	private UserService				userService;

	@Autowired
	private NewspaperService		newspaperService;


	//	10. An actor who is authenticated as a user must be able to:
	//		1. Create a volume with as many published newspapers as he or she wishes.
	//		Note that the newspapers in a volume can be added or removed at any time.
	//		The same newspaper may be used to create different volumes.

	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		final Object testingData[][] = {
			{
				//Creo un volume y le añado el newspaper con id 181 y luego se lo borro. No debe aparecer excepcion.

				"user1", 181, null
			}, {
				//Creo un volume e intento recuperar un newspaper que no existe para añadirselo al volume. Debe saltar excepcion.
				"user1", 1900, IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (int) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void template(final String username, final int newspaperId, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			super.authenticate(username);

			//Creo volume y le añado un periodico.
			Volume vol = this.volumeService.create();
			vol.setDescription("descripcion");
			List<Newspaper> newspapers = (List<Newspaper>) vol.getNewspapers();
			final Newspaper n = this.newspaperService.findOne(newspaperId);
			newspapers.add(n);
			vol.setNewspapers(newspapers);
			vol.setTitle("titulo");
			vol.setYear(2020);

			vol = this.volumeService.save(vol);

			//Le borro el periodico anteriormente insertado.
			newspapers = (List<Newspaper>) vol.getNewspapers();
			newspapers.remove(n);
			vol.setNewspapers(newspapers);
			this.volumeService.save(vol);

			super.unauthenticate();
			this.volumeService.flush();

		} catch (final Throwable oops) {
			System.out.println("----");
			caught = oops.getClass();
			System.out.println("Expected: " + caught);
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}

}
