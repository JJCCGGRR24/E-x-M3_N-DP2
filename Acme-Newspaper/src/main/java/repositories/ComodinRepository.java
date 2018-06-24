
package repositories;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Administrator;
import domain.Comodin;
import domain.Newspaper;

@Repository
public interface ComodinRepository extends JpaRepository<Comodin, Integer> {

	@Query("select c from Comodin c where c.finalMode = true and c.newspaper = ?1 and (c.moment <= ?2 or c.moment = null)")
	Collection<Comodin> getFinalModeMomentAfter(Newspaper newspaper, Date actual);

	@Query("select c from Comodin c where c.administrator = ?2 and c.finalMode = true and c.moment < ?1 and c.newspaper = null")
	Collection<Comodin> getAvailable(Date actual, Administrator a);

}
