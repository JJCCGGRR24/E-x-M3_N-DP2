
package usecases.test2;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import security.LoginService;
import services.FolderService;
import utilities.AbstractTest;
import domain.Folder;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UseCase13_2 extends AbstractTest {

	@Autowired
	private FolderService	folderService;

	@Autowired
	private LoginService	loginService;


	//	13. An actor who is authenticated must be able to:
	//		2. Manage his or her message folders, except for the system folders.

	@Test
	public void driver() {
		final Object testingData[][] = {

			//Creamos carpeta, renombramos y eliminamos carpeta
			{
				"fresa", null, "Test1"
			},
			//Eliminamos carpeta del system
			{
				"in box", IllegalArgumentException.class, "Test2"
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (Class<?>) testingData[i][1], (String) testingData[i][2]);

	}
	protected void template(final String username, final Class<?> expected, final String test) {
		Class<?> caught;

		caught = null;
		try {
			System.out.println(test);
			super.authenticate("admin");
			Folder f = null;
			if (test == "Test2") {
				f = this.folderService.getFolderByName(this.loginService.getPrincipalActor(), "in box");
				System.out.println("Folder '" + f.getName() + "' founded");
			} else {
				f = this.folderService.create();
				f.setActor(this.loginService.getPrincipalActor());
				f.setName(username);
				System.out.println("Folder '" + f.getName() + "' created");
			}
			final Folder fsaved = this.folderService.save(f);
			System.out.println("Folder '" + fsaved.getName() + "' saved (" + fsaved + ")");
			this.folderService.delete(fsaved);
			System.out.println("Folder '" + fsaved.getName() + "' deleted");
		} catch (final Throwable oops) {
			caught = oops.getClass();
			System.out.println("Excepción controlada correctamente");
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();
		System.out.println("Fin Test");
		System.out.println("");
	}
}
