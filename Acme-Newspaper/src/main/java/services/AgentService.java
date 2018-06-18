
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.AgentRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Advertisement;
import domain.Agent;
import domain.Folder;
import domain.Mesage;
import forms.RegisterForm;

;

@Service
@Transactional
public class AgentService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private AgentRepository	agentRepository;

	@Autowired
	private FolderService	folderService;


	// Supporting services ----------------------------------------------------

	// Constructors -----------------------------------------------------------
	public AgentService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Agent create() {
		Agent r = new Agent();

		final UserAccount userAccount = new UserAccount();
		final Authority auth = new Authority();
		auth.setAuthority("AGENT");
		final List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(auth);
		userAccount.setAuthorities(authorities);
		r.setUserAccount(userAccount);

		final Collection<Advertisement> advertisements = new ArrayList<Advertisement>();
		r.setAdvertisements(advertisements);

		final List<Mesage> messagesRec = new ArrayList<Mesage>();
		r.setMessagesReceiveds(messagesRec);

		final List<Mesage> messagesSend = new ArrayList<Mesage>();
		r.setMessagesSents(messagesSend);

		r = (Agent) this.folderService.addFolders(r);

		return r;
	}
	public Collection<Agent> findAll() {
		final Collection<Agent> res = this.agentRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Agent findOne(final int agentId) {
		return this.agentRepository.findOne(agentId);
	}

	public Agent save(final Agent agent) {
		Assert.notNull(agent);
		final Agent a = this.agentRepository.save(agent);
		for (final Folder f : agent.getFolders()) {
			f.setActor(a);
			this.folderService.save(f);
		}
		return a;
	}
	public void delete(final Agent agent) {
		this.agentRepository.delete(agent);
	}

	public void flush() {
		this.agentRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public Agent reconstruct(final RegisterForm registerForm) {

		final Agent a = this.create();
		final String pass = registerForm.getPassword();
		final String username = registerForm.getUsername();

		final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		final UserAccount u = a.getUserAccount();
		u.setUsername(username);
		u.setPassword(encoder.encodePassword(pass, null));
		a.setUserAccount(u);

		a.setName(registerForm.getName());
		a.setPhone(registerForm.getPhone());
		a.setPostalAddress(registerForm.getPostalAddress());
		a.setSurname(registerForm.getUsername());
		a.setEmail(registerForm.getEmail());

		return a;

	}

	public Agent findByPrincipal() {
		final UserAccount userAccount = LoginService.getPrincipal();
		final Agent user = this.agentRepository.findByPrincipal(userAccount.getId());
		Assert.isTrue(user.getUserAccount().equals(userAccount));
		return user;
	}

	public Boolean isAgent() {
		Boolean res = false;
		try {
			final Authority aut = new Authority();
			aut.setAuthority(Authority.AGENT);

			res = LoginService.getPrincipal().getAuthorities().contains(aut);
		} catch (final Exception e) {
			res = false;
		}
		return res;
	}

}
