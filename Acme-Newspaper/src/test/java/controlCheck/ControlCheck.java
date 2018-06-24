
package controlCheck;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.ComodinService;
import utilities.AbstractTest;
import domain.Comodin;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ControlCheck extends AbstractTest {

	//SUT

	@Autowired
	private ComodinService	comodinService;


	//	Administrator who writes a XXXX, saves it in draft mode, then changes it, and saves it in final mode.

	@Test
	public void driver() {
		final Object testingData[][] = {
			{

				"admin", 2, "12/09/2018 10:00", "shortTitle", "description", false, "12/05/2019 10:00", null
			}, {
				"admin", 2, "12/09/2018 10:00", "shortTitle", "description", false, "12/05/2018 10:00", IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (int) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (boolean) testingData[i][5], (String) testingData[i][6], (Class<?>) testingData[i][7]);

	}
	protected void template(final String username, final int gauge, final String moment, final String shortTitle, final String description, final boolean finalMode, final String newMoment, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			//Nos logueamos como administrador
			super.authenticate(username);
			//Creamos el objeto
			final Comodin c = this.comodinService.create();
			//Introducimos los datos
			final SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			final Date date = pattern.parse(moment);
			c.setDescription(description);
			c.setFinalMode(finalMode);
			c.setGauge(gauge);
			c.setMoment(date);
			c.setShortTitle(shortTitle);
			//Guardamos en base de datos el objeto
			final Comodin save = this.comodinService.save(c);

			//Editamos el parámetro moment del objeto y guardamos en modo final

			final SimpleDateFormat pattern2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			final Date date2 = pattern2.parse(newMoment);
			save.setMoment(date2);
			save.setFinalMode(true);

			//guardamos
			this.comodinService.save(save);
			this.comodinService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
