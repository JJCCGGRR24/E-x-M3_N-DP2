
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import security.UserAccount;
import services.AgentService;
import utilities.AbstractTest;
import domain.Agent;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase03_1Test extends AbstractTest {

	// 3.1. An actor who is not authenticated must be able to register to the system as a agent. 

	@Autowired
	private AgentService	agentService;


	@Test
	public void driver() {
		final Object testingData[][] = {

			//Intentamos registrar un agente y el resultado debe ser positivo. 
			{

				"Juan", "Rodríguez", "churrasquito@gmail.com", "646597809", "Churrasquito", "pimpo", "El Rubio", null
			},
			//Intentamos registrar un agente, y el resultado debe ser negativo ya que el usuario ya existe.
			{

				"Juana Patricia", "Escobar", "lapatri@gmail.com", "646597807", "user1", "Labrador", "Cuartel", DataIntegrityViolationException.class
			},
			//Intentamos registrar un agente y el resultado debe ser negativo ya que la contraseña excede el tamaño máximo.
			{

				"Juan", "Rodríguez", "churrasquito@gmail.com", "646597809", "nellyfurtado", "use62626262651ñlkohjvgfxzxfgcvhbnr11", "El Rubio", DataIntegrityViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (Class<?>) testingData[i][7]);

	}
	protected void template(final String name, final String surname, final String email, final String phone, final String user, final String password, final String postalAddress, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			//Creo el agent
			final Agent a = this.agentService.create();

			//Creo el userAccount
			final UserAccount userAccount = a.getUserAccount();
			userAccount.setPassword(password);
			userAccount.setUsername(user);

			//Meto los datos del agent

			a.setUserAccount(userAccount);
			a.setName(name);
			a.setSurname(surname);
			a.setPostalAddress(postalAddress);
			a.setPhone(phone);
			a.setEmail(email);

			//Guardo el agent
			this.agentService.save(a);

			this.agentService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

	}
}
