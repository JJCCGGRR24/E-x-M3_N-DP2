
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import security.LoginService;
import services.MesageService;
import utilities.AbstractTest;
import domain.Mesage;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase13_1DeleteTest extends AbstractTest {

	// 13.1 An actor who is authenticated must be able to Exchange messages with other actors and manage them, 
	//which includes deleting and moving them from one folder to another folder.  

	@Autowired
	private MesageService	mesageService;

	@Autowired
	private LoginService	loginService;


	@Test
	public void driver() {
		final Object testingData[][] = {

			//Intentamos eliminar un mensaje  estando autenticado
			{

				"admin", null
			},
			//Intentamos eliminar un mensaje sin estar autenticado
			{

				null, IllegalArgumentException.class
			},
			// Intentamos eliminar un mensaje  estando autenticado como un actor que no existe
			{

				"Cachuli", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}
	protected void template(final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {

			super.authenticate(username);

			final Mesage m = this.loginService.getPrincipalActor().getFolders().get(0).getMesages().get(0);
			this.mesageService.delete(m);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
