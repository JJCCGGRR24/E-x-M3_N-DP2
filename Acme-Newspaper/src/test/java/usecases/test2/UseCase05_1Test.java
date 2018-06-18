
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AdvertisementService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.CreditCard;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase05_1Test extends AbstractTest {

	//SUT

	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private NewspaperService		newspaperService;


	//	5. An actor who is authenticated as an administrator must be able to:
	//		1. List the advertisements that contain taboo words in its title.

	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		final Object testingData[][] = {
			{
				//Añado un advertisement con palabras tabues por lo que debe haber 
				//un elemento mas en la lista de advertisements con palabras tabu.

				"agent1", "Advertisement sex", null
			}, {
				//Añadimos un advertisement sin palabras tabues por lo que al hacer
				//la comprobacion de que hay un elemento mas en la lista de
				//advertisements con palabras tabues debe saltar la excepcion.
				"agent1", "Advertisement", IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void template(final String username, final String title, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			super.authenticate(username);

			final int sizeAntes = this.advertisementService.getAdvertisementsWithTabbooWords().size();

			final Advertisement adv = this.advertisementService.create();

			adv.setBanner("http://localhost:8080/Acme-Newspaper/");
			final CreditCard cc = new CreditCard();
			cc.setBrandName("VISA");
			cc.setCVV(123);
			cc.setExpirationMonth(11);
			cc.setExpirationYear(2020);
			cc.setHolderName("VISA");
			cc.setNumber("4000990618525905");
			adv.setCreditCard(cc);
			adv.setNewspaper(this.newspaperService.getPublishedNewspapers().get(0));
			adv.setTargetPage("http://localhost:8080/Acme-Newspaper/");
			adv.setTitle(title);
			this.advertisementService.save(adv);

			final int sizeDespues = this.advertisementService.getAdvertisementsWithTabbooWords().size();

			Assert.isTrue(sizeAntes + 1 == sizeDespues);

			super.unauthenticate();
			this.advertisementService.flush();

		} catch (final Throwable oops) {
			System.out.println("----");
			caught = oops.getClass();
			System.out.println("Expected: " + caught);
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}

}
