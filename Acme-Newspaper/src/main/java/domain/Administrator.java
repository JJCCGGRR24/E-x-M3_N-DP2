
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@Access(AccessType.PROPERTY)
public class Administrator extends Actor {

	//Relatioships
	private Collection<Comodin>	comodines;


	@OneToMany(mappedBy = "administrator")
	public Collection<Comodin> getComodines() {
		return this.comodines;
	}

	public void setComodines(final Collection<Comodin> comodines) {
		this.comodines = comodines;
	}

}
