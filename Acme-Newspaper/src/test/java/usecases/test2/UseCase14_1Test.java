
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import forms.BroadcastForm;
import services.MesageService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase14_1Test extends AbstractTest {

	//SUT

	@Autowired
	private MesageService mesageService;

	//	4. An actor who is authenticated as a administrator must be able to:
	//		1. Broadcast a message to the actors of the system.


	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		System.out.println("---BROADCAST MESSAGE---");
		final Object testingData[][] = {
			{

				"admin", null
			}, {

				"user1", java.lang.IllegalArgumentException.class
			}, {

				"user1000", java.lang.IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void template(final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			super.authenticate(username);

			final BroadcastForm mesage = new BroadcastForm();
			mesage.setPriority("NEUTRAL");
			mesage.setSubject("Test");
			mesage.setBody("Esto es una prueba para el test.");

			this.mesageService.sendBroadcast(mesage);

			System.out.println(mesage.getBody());

			System.out.println("Operation successful!");

		} catch (final Throwable oops) {
			System.out.println("----");
			caught = oops.getClass();
			System.out.println("Expected: " + caught);
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}

}
