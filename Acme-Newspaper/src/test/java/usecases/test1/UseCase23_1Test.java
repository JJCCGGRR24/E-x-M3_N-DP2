
package usecases.test1;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.NewspaperService;
import utilities.AbstractTest;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase23_1Test extends AbstractTest {

	//SUT

	@Autowired
	private NewspaperService	newspaperService;


	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		System.out.println("---PRIVATE OR PUBLIC NEWSPAPER BY USER---");
		final Object testingData[][] = {
			{

				"user3", "newspaper3", null
			}, {

				"user2", "newspaper1_3", java.lang.IllegalArgumentException.class
			}, {

				"user1", "newspaper1", null
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2], i);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void template(final String username, final String newspaper, final Class<?> expected, final int i) {
		Class<?> caught;
		System.out.println("Prueba 223_1 #" + (i + 1));
		caught = null;

		try {
			super.authenticate(username);

			//Periodico Privado
			final int newspaperId = this.getEntityId(newspaper);
			final Newspaper newspaper2 = this.newspaperService.findOne(newspaperId);
			System.out.println(newspaper2);
			newspaper2.setDeprived(false);
			newspaper2.setPublicationDate(null);
			this.newspaperService.save(newspaper2);

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
