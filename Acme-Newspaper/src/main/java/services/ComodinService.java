
package services;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
		final Administrator admin = (Administrator) this.loginService.getPrincipalActor();
		Assert.isTrue(comodin.getAdministrator() == admin, "not.principal");
		if (comodin.getId() != 0) {
			final Comodin bd = this.comodinRepository.findOne(comodin.getId());
			Assert.isTrue(bd.isFinalMode() == false, "in.finalMode");
		}
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

	public Collection<Comodin> comodinFinalMode(final Newspaper newspaper) {
		return this.comodinRepository.comodinFinalMode(newspaper);
	}

}
