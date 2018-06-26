
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

import repositories.FustRepository;
import security.LoginService;
import domain.Administrator;
import domain.Fust;
import domain.Newspaper;

;

@Service
@Transactional
public class FustService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private FustRepository	fustRepository;

	@Autowired
	private LoginService	loginService;

	@Autowired
	private Validator		validator;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------
	public FustService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Fust create() {
		final Fust r = new Fust();
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		r.setAdministrator(admin);
		r.setTicker(this.generateCode());
		r.setNewspaper(null);
		return r;
	}
	public Collection<Fust> findAll() {
		final Collection<Fust> res = this.fustRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Fust findOne(final int fustId) {
		return this.fustRepository.findOne(fustId);
	}

	public Fust save(final Fust c) {
		Assert.notNull(c);
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(c.getAdministrator().equals(admin), "not.principal");
		final Date actual = new Date();

		if (c.getId() == 0) {
			if (c.getMoment() != null)
				Assert.isTrue(c.getMoment().after(actual), "moment.before");
			Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
		} else {
			final Fust bd = this.fustRepository.findOne(c.getId());
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
		return this.fustRepository.save(c);
	}
	public void delete(final Fust fust) {
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(fust.getAdministrator().equals(admin), "not.principal");
		final Fust bd = this.fustRepository.findOne(fust.getId());
		Assert.isTrue(bd.isFinalMode() == false, "in.finalMode");
		this.fustRepository.delete(fust);
	}
	public void flush() {
		this.fustRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public void verify(final Fust c) {

		final Fust bd = this.fustRepository.findOne(c.getId());
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
		return day + FustService.generateStringAux() + "-" + month + year;

	}

	private static String generateStringAux() {
		int index;
		final int random = (int) (Math.floor(Math.random() * (2 - 0)) + 0);
		if (random != 0)
			index = 2;
		else
			index = 5;
		final int length = index;
		final String characters = "_0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final Random rng = new Random();
		final char[] text = new char[length];
		for (int i = 0; i < index; i++)
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		return new String(text);
	}
	public Collection<Fust> getFinalModeMomentAfter(final Newspaper newspaper) {
		final Date actual = new Date();
		return this.fustRepository.getFinalModeMomentAfter(newspaper, actual);
	}

	public Collection<Fust> getAvailable(final Administrator a) {
		final Date actual = new Date();
		final Collection<Fust> c = this.fustRepository.getAvailable(actual, a);
		final Collection<Fust> all = a.getFustList();
		all.removeAll(c);
		return all;
	}

	public Fust reconstruct(final Fust c, final BindingResult binding) {
		Fust result;
		Fust bd;

		if (c.getId() == 0) {
			c.setAdministrator((Administrator) this.loginService.getPrincipalActor());
			c.setTicker(this.generateCode());
			result = c;

		} else {
			bd = this.fustRepository.findOne(c.getId());
			c.setAdministrator(bd.getAdministrator());
			c.setTicker(bd.getTicker());

			result = c;

		}
		this.validator.validate(c, binding);

		return result;
	}
}
