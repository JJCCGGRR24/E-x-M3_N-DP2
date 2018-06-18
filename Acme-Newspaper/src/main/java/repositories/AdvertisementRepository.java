
package repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Advertisement;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {

	@Query("select a from Advertisement a where a.tabooWord = true")
	Collection<Advertisement> getAdvertisementsWithTabooWords();

	@Query("select c from Advertisement c where c.title like %?1%")
	List<Advertisement> getAdvertisementsTabooWordsUpdate(String word);
}
