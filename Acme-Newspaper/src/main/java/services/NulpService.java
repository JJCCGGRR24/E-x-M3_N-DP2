
package services;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.NulpRepository;
import security.LoginService;
import domain.Administrator;
import domain.Newspaper;
import domain.Nulp;

;

@Service
@Transactional
public class NulpService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private NulpRepository	nulpRepository;

	@Autowired
	private LoginService	loginService;

	@Autowired
	private Validator		validator;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------
	public NulpService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Nulp create() {
		final Nulp r = new Nulp();
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		r.setAdministrator(admin);
		r.setTicker(this.generateCode());
		r.setNewspaper(null);
		return r;
	}
	public Collection<Nulp> findAll() {
		final Collection<Nulp> res = this.nulpRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Nulp findOne(final int nulpId) {
		return this.nulpRepository.findOne(nulpId);
	}

	public Nulp save(final Nulp c) {
		Assert.notNull(c);
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(c.getAdministrator().equals(admin), "not.principal");
		final Date actual = new Date();

		if (c.getId() == 0) {
			if (c.getMoment() != null)
				Assert.isTrue(c.getMoment().after(actual), "moment.before");
			Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
		} else {
			final Nulp bd = this.nulpRepository.findOne(c.getId());
			if (bd.isFinalMode()) {
				Assert.isTrue(bd.getNewspaper() == null, "yet.asociated");
				if (c.getMoment() != null)
					Assert.isTrue(c.getMoment().after(actual), "moment.before");
				this.verify(c);
			}
			if (bd.isFinalMode() == false) {
				if (c.getMoment() != null)
					Assert.isTrue(c.getMoment().after(actual), "moment.before");
				Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
			}
		}
		return this.nulpRepository.save(c);
	}
	public void delete(final Nulp nulp) {
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(nulp.getAdministrator().equals(admin), "not.principal");
		final Nulp bd = this.nulpRepository.findOne(nulp.getId());
		Assert.isTrue(bd.isFinalMode() == false, "in.finalMode");
		this.nulpRepository.delete(nulp);
	}
	public void flush() {
		this.nulpRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public void verify(final Nulp c) {

		final Nulp bd = this.nulpRepository.findOne(c.getId());
		Assert.isTrue(bd.getShortTitle().equals(c.getShortTitle()), "in.finalMode");
		Assert.isTrue(bd.getDescription().equals(c.getDescription()), "in.finalMode");
		Assert.isTrue(bd.getGauge() == (c.getGauge()), "in.finalMode");
		Assert.isTrue(bd.isFinalMode() == c.isFinalMode(), "in.finalMode");

		if (bd.getMoment() != null)
			Assert.isTrue(c.getMoment().equals(bd.getMoment()), "in.finalMode");
		else
			Assert.isNull(c.getMoment(), "in.finalMode");
	}

	@SuppressWarnings("deprecation")
	public String generateCode() {
		final Date current = new Date();
		final Integer dayInt = current.getDate();
		String day = dayInt.toString();
		if (day.length() == 1)
			day = "0" + day;
		final Integer monthInt = current.getMonth() + 1;
		String month = monthInt.toString();
		if (month.length() == 1)
			month = "0" + month;
		final Integer yearInt = current.getYear();
		final String year = yearInt.toString().substring(1, 3);
		return year + month + day + ":" + NulpService.generateStringAux();

	}

	private static String generateStringAux() {
		final int length = 6;
		final String characters = "_0123456789abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
		final Random rng = new Random();
		final char[] text = new char[length];
		for (int i = 0; i < 6; i++)
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		return new String(text);
	}

	public Collection<Nulp> getFinalModeMomentAfter(final Newspaper newspaper) {
		final Date actual = new Date();
		return this.nulpRepository.getFinalModeMomentAfter(newspaper, actual);
	}

	public Collection<Nulp> getAvailable(final Administrator a) {
		final Date actual = new Date();
		final Collection<Nulp> c = this.nulpRepository.getAvailable(actual, a);
		final Collection<Nulp> all = a.getNulpList();
		all.removeAll(c);
		return all;
	}

	public Nulp reconstruct(final Nulp c, final BindingResult binding) {
		Nulp result;
		Nulp bd;

		if (c.getId() == 0) {
			c.setAdministrator((Administrator) this.loginService.getPrincipalActor());
			c.setTicker(this.generateCode());
			result = c;

		} else {
			bd = this.nulpRepository.findOne(c.getId());
			c.setAdministrator(bd.getAdministrator());
			c.setTicker(bd.getTicker());

			result = c;

		}
		this.validator.validate(c, binding);

		return result;
	}
}
