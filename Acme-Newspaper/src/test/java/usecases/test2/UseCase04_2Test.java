
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class UseCase04_2Test extends AbstractTest {

	//SUT

	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private NewspaperService		newspaperService;


	//4.2 An actor who is authenticated as an agent must be able to 
	//	register an advertisement and place it in a newspaper
	//DRIVERS-------------------------------------------------------------------------------

	@Test
	public void driver() {
		final Object testingData[][] = {
			{
				//introducimos datos válidos para que un agent pueda registrar un anuncio 
				"agent1", "title1", "https://www.google.es/", "https://www.google.es/", "Phaidros Randi", "VISA", "4000990618525905", 2020, 12, 134, "newspaper1", null

			}, {
				//dejamos el titulo en blanco para que no nos permita guardar el anuncio
				"agent1", "", "https://www.google.es/", "https://www.google.es/", "Phaidros Randi", "VISA", "4000990618525905", 2020, 12, 134, "newspaper1", javax.validation.ConstraintViolationException.class

			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (int) testingData[i][7],
				(int) testingData[i][8], (int) testingData[i][9], super.getEntityId((String) testingData[i][10]), (Class<?>) testingData[i][11]);

	}

	//TEMPLATES------------------------------------------------------------------------------

	protected void template(final String username, final String title, final String banner, final String targetPage, final String holderName, final String brandName, final String number, final int year, final int month, final int cvv,
		final int newspaperId, final Class<?> expected) {
		Class<?> caught;

		caught = null;

		try {
			//me logueo como agent1
			super.authenticate(username);

			final Advertisement a = this.advertisementService.create();

			//introducimos la tarjeta de credito 
			final CreditCard cc = new CreditCard();
			cc.setBrandName(brandName);
			cc.setCVV(cvv);
			cc.setExpirationMonth(month);
			cc.setExpirationYear(year);
			cc.setHolderName(holderName);
			cc.setNumber(number);
			a.setCreditCard(cc);

			//introducimos los otros datos del anuncio
			a.setBanner(banner);
			a.setNewspaper(this.newspaperService.findOne(newspaperId));
			a.setTargetPage(targetPage);
			a.setTitle(title);

			this.advertisementService.save(a);
			this.advertisementService.flush();
			this.newspaperService.flush();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
