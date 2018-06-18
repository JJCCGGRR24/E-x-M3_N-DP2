
package usecases.test2;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.AdvertisementService;
import utilities.AbstractTest;
import domain.Advertisement;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase05_2Test extends AbstractTest {

	// 5.2. An actor who is authenticated as an administrator must be able to remove an advertisement that he or she thinks is inappropriate 

	@Autowired
	private AdvertisementService	advertisementService;


	@Test
	public void driver() {
		final Object testingData[][] = {

			//Intentamos eliminar un anuncio autenticados como el administrador, y el resultado debe ser positivo
			{

				"Admin", null
			},
			//Intentamos eliminar un anuncio autenticados como usuario, y el resultado debe ser negativo.
			{

				"user1", IllegalArgumentException.class
			},
			//Intentamos eliminar un anuncio autenticados como customer, y el resultado debe ser negativo.
			{

				"customer1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}
	protected void template(final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {

			super.authenticate(username);

			final Advertisement a = ((List<Advertisement>) this.advertisementService.findAll()).get(0);
			this.advertisementService.delete(a);

			this.advertisementService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
