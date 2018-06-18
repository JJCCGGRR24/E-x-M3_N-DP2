
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.AdministratorService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase05_3Test extends AbstractTest {

	//SUT

	@Autowired
	private AdministratorService	administratorService;


	//	5.
	//	An actor who is authenticated as an administrator must be able to:

	//	3.Display a dashboard with the following information:
	//		The ratio of newspapers that have at least one advertisement versus the
	//		newspapers that haven’t any.
	//		The ratio of advertisements that have taboo words.

	@Test
	public void driver() {
		final Object testingData[][] = {
			{
				//Hacemos una llamada a una query como admin
				"admin", null
			}, {
				//Hacemos una llamada a una query como user
				"user1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.template((String) testingData[i][0], (Class<?>) testingData[i][1]);
			System.out.println("-------------------");
		}
	}
	protected void template(final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			super.authenticate(username);
			System.out.println("Llamada a la query de nivel C");
			System.out.println(this.administratorService.query2C2());
			System.out.println("Todo correcto");
			super.authenticate(null);
		} catch (final Throwable oops) {
			caught = oops.getClass();
			System.out.println("Excepcion controlada");
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
