
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Comodin;
import domain.Newspaper;

@Repository
public interface ComodinRepository extends JpaRepository<Comodin, Integer> {

	@Query("select c from Comodin c where c.finalMode = true and c.newspaper = ?1")
	Collection<Comodin> comodinFinalMode(Newspaper newspaper);

}
