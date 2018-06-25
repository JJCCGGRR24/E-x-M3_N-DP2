
package controlCheck;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import services.NulpService;
import utilities.AbstractTest;
import domain.Nulp;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ControlCheck extends AbstractTest {

	//SUT

	@Autowired
	private NulpService	nulpService;


	//	Administrator who writes a XXXX, saves it in draft mode, then changes it, and saves it in final mode.

	@Test
	public void driver() {
		final Object testingData[][] = {
			{
				//nos logueamos como admin y creamos uno con parametros válidos
				"admin", 2, "12/09/2018 10:00", "shortTitle", "description", false, "newShortTitle", null
			}, {
				//nos logueamos como admin e intentamos editarlo cambiando en titulo pequeño a una cadena vacia, el sistema no nos debe dejar
				"admin", 2, "12/09/2018 10:00", "shortTitle", "description", false, "", javax.validation.ConstraintViolationException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (int) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (boolean) testingData[i][5], (String) testingData[i][6], (Class<?>) testingData[i][7]);

	}
	protected void template(final String username, final int gauge, final String moment, final String shortTitle, final String description, final boolean finalMode, final String newShortTitle, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			//Nos logueamos como administrador
			super.authenticate(username);
			//Creamos el objeto
			final Nulp c = this.nulpService.create();
			//Introducimos los datos
			final SimpleDateFormat pattern = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			final Date date = pattern.parse(moment);
			c.setDescription(description);
			c.setFinalMode(finalMode);
			c.setGauge(gauge);
			c.setMoment(date);
			c.setShortTitle(shortTitle);
			//Guardamos en base de datos el objeto
			final Nulp save = this.nulpService.save(c);

			//Editamos el parámetro shortTitle del objeto y guardamos en modo final

			save.setShortTitle(newShortTitle);
			save.setFinalMode(true);

			//guardamos
			this.nulpService.save(save);
			this.nulpService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
