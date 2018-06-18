
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import domain.CreditCard;
import domain.SubscribeVol;
import services.SubscribeVolService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase09_1Test extends AbstractTest {

	//SUT

	@Autowired
	private SubscribeVolService subscribeVolService;

	//	9. An actor who is authenticated as a customer must be able to:
	//		1. Subscribe to a volume by providing a credit card.
	//		Note that subscribing to a volume implies subscribing automatically
	//		to all of the newspapers of which it is composed, including newspapers
	//		that might be published after the subscription takes place.


	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		System.out.println("---SUBSCRIBE TO VOLUME---");
		final Object testingData[][] = {
			{

				"customer3", "volume3", null
			}, {

				"user1", "volume3", java.lang.ClassCastException.class
			}, {

				"user1000", "volume3", java.lang.IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void template(final String username, final String volume, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			super.authenticate(username);
			final int volumeId = this.getEntityId(volume);
			final SubscribeVol sv = this.subscribeVolService.create(volumeId);
			final CreditCard creditCard = new CreditCard();
			creditCard.setBrandName("Visa");
			creditCard.setCVV(222);
			creditCard.setExpirationMonth(6);
			creditCard.setExpirationYear(2025);
			creditCard.setHolderName("Julian Lopez");
			creditCard.setNumber("4496257604663723");
			sv.setCreditCard(creditCard);
			this.subscribeVolService.save(sv);

			System.out.println(sv.getVolume().getTitle());

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
