
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
	private Collection<Fust>	fustList;


	@Valid
	@OneToMany(mappedBy = "administrator")
	public Collection<Fust> getFustList() {
		return this.fustList;
	}

	public void setFustList(final Collection<Fust> fustList) {
		this.fustList = fustList;
	}

}
