
package services;

import java.sql.Timestamp;
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

	public Comodin save(final Comodin comodin) {
		Assert.notNull(comodin);

		//		final Comodin result = this.comodinRepository.findOne(comodin.getId());
		//
		//		if (comodin.getId() != 0 && result.isFinalMode()) {
		//			Assert.isTrue(result.getNewspaper() == null, "yet.asociated");
		//			result.setNewspaper(comodin.getNewspaper());
		//			final Timestamp stamp = (Timestamp) result.getMoment();
		//			final Date date = new Date(stamp.getTime());
		//			result.setMoment(date);
		//			Assert.isTrue(result.getMoment().equals(comodin.getMoment()));
		//			Assert.isTrue(result.equals(comodin), "in.finalMode");
		//		}
		//		if (comodin.isFinalMode() == false) {
		//			if (comodin.getMoment() != null) {
		//				final Date actual = new Date();
		//				Assert.isTrue(comodin.getMoment().after(actual), "moment.before");
		//			}
		//			Assert.isTrue(comodin.getNewspaper() == null, "necessary.finalMode");
		//
		//		}
		return this.comodinRepository.save(comodin);
	}
	public void delete(final Comodin comodin) {
		Assert.isTrue(comodin.isFinalMode() == false, "in.finalMode");
		this.comodinRepository.delete(comodin);
	}
	public void flush() {
		this.comodinRepository.flush();
	}

	// Other business methods -------------------------------------------------

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

	public Collection<Comodin> comodinFinalModeMomentAfter(final Newspaper newspaper) {
		final Date actual = new Date();
		return this.comodinRepository.comodinFinalModeMomentAfter(newspaper, actual);
	}

	public Comodin reconstruct(final Comodin c, final BindingResult binding) {
		Comodin result;

		if (c.getId() == 0) {
			if (c.getMoment() != null) {
				final Date actual = new Date();
				Assert.isTrue(c.getMoment().after(actual), "moment.before");
				Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
			}
			result = c;
		} else {
			result = this.comodinRepository.findOne(c.getId());
			if (result.isFinalMode()) {
				Assert.isTrue(result.getNewspaper() == null, "yet.asociated");
				result.setNewspaper(c.getNewspaper());
				final Timestamp stamp = (Timestamp) result.getMoment();
				final Date date = new Date(stamp.getTime());
				result.setMoment(date);
				Assert.isTrue(result.getMoment().equals(c.getMoment()));
				Assert.isTrue(result.equals(c), "in.finalMode");
			}
			if (result.isFinalMode() == false) {
				if (c.getMoment() != null) {
					final Date actual = new Date();
					Assert.isTrue(c.getMoment().after(actual), "moment.before");
				}
				Assert.isTrue(c.getNewspaper() == null, "necessary.finalMode");
			}

			result.setDescription(c.getDescription());
			result.setFinalMode(c.isFinalMode());
			result.setGauge(c.getGauge());
			result.setMoment(c.getMoment());
			result.setNewspaper(c.getNewspaper());
			result.setAdministrator(c.getAdministrator());
			result.setShortTitle(c.getShortTitle());
			result.setTicker(c.getTicker());

			this.validator.validate(result, binding);
		}
		return result;
	}
}
