
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.AdvertisementRepository;
import security.LoginService;
import domain.Advertisement;
import domain.Agent;
import domain.Taboo;

@Service
@Transactional
public class AdvertisementService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private AdvertisementRepository	advertisementRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private LoginService			loginService;

	@Autowired
	private TabooService			tabooService;

	@Autowired
	private NewspaperService		newspaperService;


	// Constructors -----------------------------------------------------------
	public AdvertisementService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Advertisement create() {
		final Advertisement r = new Advertisement();
		r.setAgent((Agent) this.loginService.getPrincipalActor());
		r.setTabooWord(false);
		return r;
	}

	public Collection<Advertisement> findAll() {
		final Collection<Advertisement> res = this.advertisementRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Advertisement findOne(final int advertisementId) {
		return this.advertisementRepository.findOne(advertisementId);
	}

	public Advertisement save(final Advertisement advertisement) {
		Assert.notNull(advertisement);
		advertisement.setTabooWord(this.isTaboo(advertisement));
		return this.advertisementRepository.save(advertisement);
	}

	public void delete(final Advertisement advertisement) {
		Assert.isTrue(LoginService.isPrincipalAdmin());
		this.advertisementRepository.delete(advertisement);
	}

	public void flush() {
		this.advertisementRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public Collection<Advertisement> getAdvertisementsWithTabbooWords() {
		//		return this.advertisementRepository.getAdvertisementsWithTabooWords();
		final List<Advertisement> res = new ArrayList<Advertisement>();
		for (final Taboo t : this.tabooService.findAll()) {
			final List<Advertisement> l = this.advertisementRepository.getAdvertisementsTabooWordsUpdate(t.getWord());
			if (!l.equals(null) && l.size() > 0)
				res.addAll(l);
		}
		return res;
	}

	public Advertisement chooseOne(final int id) {
		final ArrayList<Advertisement> advertisements = new ArrayList<>(this.newspaperService.findOne(id).getAdvertisements());
		Advertisement a = null;
		if (advertisements != null && advertisements.size() > 0) {
			final int i = (int) (Math.random() * advertisements.size());
			a = advertisements.get(i);
		}
		return a;
	}

	@SuppressWarnings("deprecation")
	public String validate(final Advertisement c) {
		String b = null;
		final Date now = new Date();
		final Date cc = new Date(c.getCreditCard().getExpirationYear() - 1900, c.getCreditCard().getExpirationMonth(), 0);
		final long days = (cc.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
		if (days < 30)
			b = "subscribe.error.cc.dates";
		return b;
	}

	public boolean isTaboo(final Advertisement advertisement) {
		boolean b = false;
		for (final Taboo t : this.tabooService.findAll()) {
			final String s = t.getWord().toLowerCase();
			if (advertisement.getTitle().toLowerCase().contains(s)) {
				b = true;
				break;
			}
		}
		return b;
	}
}
