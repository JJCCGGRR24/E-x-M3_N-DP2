
package services;

import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.SubscribeVolRepository;
import security.LoginService;
import domain.Customer;
import domain.Newspaper;
import domain.Subscribe;
import domain.SubscribeVol;
import domain.Volume;

;

@Service
@Transactional
public class SubscribeVolService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private SubscribeVolRepository	subscribeVolRepository;

	@Autowired
	private SubscribeService		subscribeService;

	@Autowired
	private VolumeService			volumeService;

	@Autowired
	private LoginService			loginService;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------
	public SubscribeVolService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public SubscribeVol create(final int volumeId) {

		final SubscribeVol r = new SubscribeVol();
		r.setVolume(this.volumeService.findOne(volumeId));
		r.setCustomer((Customer) this.loginService.getPrincipalActor());
		return r;
	}
	public Collection<SubscribeVol> findAll() {
		final Collection<SubscribeVol> res = this.subscribeVolRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public SubscribeVol findOne(final int subscribeVolId) {
		return this.subscribeVolRepository.findOne(subscribeVolId);
	}

	public SubscribeVol save(final SubscribeVol sv) {
		Assert.notNull(sv);
		Assert.isTrue(!this.estaSubscrito(sv.getCustomer(), sv.getVolume()));
		final Customer c = (Customer) this.loginService.getPrincipalActor();
		sv.setCustomer(c);
		sv.getCustomer().getSubscribesVol().add(sv);
		sv.getVolume().getSubscribesVol().add(sv);
		final SubscribeVol res = this.subscribeVolRepository.save(sv);
		for (final Newspaper n : res.getVolume().getNewspapers())
			if (n.getDeprived())
				this.subscribeService.subscribe(n, sv.getCreditCard(), c);
		return res;
	}
	public void delete(final SubscribeVol subscribeVol) {
		this.subscribeVolRepository.delete(subscribeVol);
	}

	public void flush() {
		this.subscribeVolRepository.flush();
	}

	// Other business methods -------------------------------------------------

	@SuppressWarnings("deprecation")
	public String validate(final SubscribeVol c) {
		String b = null;
		final int Ynow = new Date().getYear() + 1900;
		final int Mnow = new Date().getMonth() + 1;
		if (!(Ynow < c.getCreditCard().getExpirationYear() || (Ynow == c.getCreditCard().getExpirationYear() && Mnow <= c.getCreditCard().getExpirationMonth())))
			b = "subscribe.error.cc.dates";
		return b;
	}

	public boolean estaSubscrito(final Customer c, final Volume n) {
		boolean res = false;
		final Subscribe subscribe = this.subscribeVolRepository.getSubscripcion(c, n);
		if (subscribe != null)
			res = true;
		return res;
	}
}
