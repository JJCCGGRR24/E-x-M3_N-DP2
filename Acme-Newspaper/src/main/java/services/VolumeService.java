
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.VolumeRepository;
import security.LoginService;
import domain.Actor;
import domain.Newspaper;
import domain.SubscribeVol;
import domain.User;
import domain.Volume;

;

@Service
@Transactional
public class VolumeService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private VolumeRepository	volumeRepository;

	@Autowired
	private SubscribeService	subscribeService;

	@Autowired
	private LoginService		loginService;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------
	public VolumeService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Volume create() {
		final Volume r = new Volume();
		r.setUser((User) this.loginService.getPrincipalActor());
		r.setNewspapers(new ArrayList<Newspaper>());
		r.setSubscribesVol(new ArrayList<SubscribeVol>());
		return r;
	}

	public Collection<Volume> findAll() {
		final Collection<Volume> res = this.volumeRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Volume findOne(final int volumeId) {
		return this.volumeRepository.findOne(volumeId);
	}

	public Volume save(final Volume volume) {
		Assert.notNull(volume);
		Assert.isTrue(this.checkPrincipal(volume));
		this.updateSubscribes(volume);
		return this.volumeRepository.save(volume);
	}

	private void updateSubscribes(final Volume volume) {
		for (final SubscribeVol c : volume.getSubscribesVol())
			for (final Newspaper n : volume.getNewspapers())
				if (n.getDeprived() && !this.subscribeService.estaSubscrito(c.getCustomer(), n))
					this.subscribeService.subscribe(n, c.getCreditCard(), c.getCustomer());
	}

	public void delete(final Volume volume) {
		Assert.isTrue(this.checkPrincipal(volume));
		this.volumeRepository.delete(volume);
	}

	public void flush() {
		this.volumeRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public Boolean checkPrincipal(final Volume j) {
		Boolean res = false;
		Assert.isTrue(j.getUser().getUserAccount().equals(LoginService.getPrincipal()));
		res = true;
		return res;
	}

	public List<Volume> volumesByCustomer(final Actor a) {
		return this.volumeRepository.volumesByCustomer(a.getId());
	}

}
