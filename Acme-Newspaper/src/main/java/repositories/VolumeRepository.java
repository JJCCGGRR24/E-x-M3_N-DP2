
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Volume;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Integer> {

	@Query("select s.volume from SubscribeVol s where s.customer.id = ?1")
	public List<Volume> volumesByCustomer(int id);
}
