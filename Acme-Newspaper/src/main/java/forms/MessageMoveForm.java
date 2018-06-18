
package forms;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import domain.Folder;

public class MessageMoveForm {

	private Folder	folder;
	private int		id;


	@Valid
	@NotNull
	public Folder getFolder() {
		return this.folder;
	}

	public void setFolder(final Folder folder) {
		this.folder = folder;
	}

	@Min(0)
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

}
