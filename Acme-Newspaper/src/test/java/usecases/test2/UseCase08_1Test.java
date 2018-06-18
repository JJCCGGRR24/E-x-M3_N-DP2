
package usecases.test2;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.VolumeService;
import utilities.AbstractTest;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase08_1Test extends AbstractTest {

	//SUT

	@Autowired
	private VolumeService	volumeService;


	//	8. An actor who is not authenticated must be able to:
	//		1. List the volumes in the system and browse their newspapers as long as they are
	//		authorised (for instance, a private newspaper cannot be fully displayed to
	//		unauthenticated actors).

	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		System.out.println("---List volumes---");
		final Object testingData[][] = {
			{
				null, null
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

			final ArrayList<Volume> newspapers = new ArrayList<>(this.volumeService.findAll());

			for (final Volume newspaper : newspapers)
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
