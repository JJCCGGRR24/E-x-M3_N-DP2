
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;

@Entity
@Access(AccessType.PROPERTY)
public class Administrator extends Actor {

	//Relatioships
	private Collection<Nulp>	nulpList;


	@Valid
	@OneToMany(mappedBy = "administrator")
	public Collection<Nulp> getNulpList() {
		return this.nulpList;
	}

	public void setNulpList(final Collection<Nulp> nulpList) {
		this.nulpList = nulpList;
	}

}
