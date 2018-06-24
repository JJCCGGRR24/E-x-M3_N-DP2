
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

import repositories.ComodinRepository;
import security.LoginService;
import domain.Administrator;
import domain.Comodin;
import domain.Newspaper;

;

@Service
@Transactional
public class ComodinService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private ComodinRepository	comodinRepository;

	@Autowired
	private LoginService		loginService;

	@Autowired
	private Validator			validator;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------
	public ComodinService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Comodin create() {
		final Comodin r = new Comodin();
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		r.setAdministrator(admin);
		r.setTicker(this.generateCode());
		r.setNewspaper(null);
		return r;
	}
	public Collection<Comodin> findAll() {
		final Collection<Comodin> res = this.comodinRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Comodin findOne(final int comodinId) {
		return this.comodinRepository.findOne(comodinId);
	}

	public Comodin save(final Comodin c) {
		Assert.notNull(c);
		final Date actual = new Date();

		if (c.getId() == 0) {
			if (c.getMoment() != null)
				Assert.isTrue(c.getMoment().after(actual), "moment.before");
			Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
		} else {
			final Comodin bd = this.comodinRepository.findOne(c.getId());
			if (bd.isFinalMode()) {
				Assert.isTrue(bd.getNewspaper() == null, "yet.asociated");
				if (c.getMoment() != null)
					Assert.isTrue(c.getMoment().after(actual), "moment.before");
				this.verify(c);

				if (bd.isFinalMode() == false) {
					if (c.getMoment() != null)
						Assert.isTrue(c.getMoment().after(actual), "moment.before");
					Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
				}
			}
		}
		return this.comodinRepository.save(c);
	}
	public void delete(final Comodin comodin) {
		Assert.isTrue(comodin.isFinalMode() == false, "in.finalMode");
		this.comodinRepository.delete(comodin);
	}
	public void flush() {
		this.comodinRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public void verify(final Comodin c) {

		final Comodin bd = this.comodinRepository.findOne(c.getId());
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
		return year + month + day + ":" + ComodinService.generateStringAux();

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

	public Collection<Comodin> getFinalModeMomentAfter(final Newspaper newspaper) {
		final Date actual = new Date();
		return this.comodinRepository.getFinalModeMomentAfter(newspaper, actual);
	}

	public Collection<Comodin> getAvailable(final Administrator a) {
		final Date actual = new Date();
		final Collection<Comodin> c = this.comodinRepository.getAvailable(actual, a);
		final Collection<Comodin> all = a.getComodines();
		all.removeAll(c);
		return all;
	}

	public Comodin reconstruct(final Comodin c, final BindingResult binding) {
		Comodin result;
		Comodin bd;

		if (c.getId() == 0) {
			c.setAdministrator((Administrator) this.loginService.getPrincipalActor());
			c.setTicker(this.generateCode());
			result = c;

		} else {
			bd = this.comodinRepository.findOne(c.getId());
			c.setAdministrator(bd.getAdministrator());
			c.setTicker(bd.getTicker());

			result = c;

		}
		this.validator.validate(c, binding);

		return result;
	}
}
