
package usecases.test2;

import java.util.ArrayList;

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
public class UseCase04_3Test extends AbstractTest {

	//SUT

	@Autowired
	private NewspaperService	newspaperService;


	//	4. An actor who is authenticated as a agent must be able to:
	//		3. List the newspapers in which they have placed any advertisements.

	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driverList() {
		System.out.println("---LIST---");
		final Object testingData[][] = {
			{

				"agent1", null
			}, {

				"user1", java.lang.IllegalArgumentException.class
			}, {

				"user1000", java.lang.IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateList((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void templateList(final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			super.authenticate(username);

			final int agentId = this.getEntityId(username);
			final ArrayList<Newspaper> newspapers = new ArrayList<>(this.newspaperService.findByAgent(agentId));

			for (final Newspaper newspaper : newspapers)
				System.out.println(newspaper.getTitle());

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
