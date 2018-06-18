
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.MesageRepository;
import security.LoginService;
import domain.Actor;
import domain.Folder;
import domain.Mesage;
import domain.Taboo;
import forms.BroadcastForm;
import forms.MessageForm;

@Service
@Transactional
public class MesageService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private MesageRepository		messageRepository;

	// Supporting services ----------------------------------------------------
	@Autowired
	private LoginService			loginService;

	@Autowired
	private FolderService			folderService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private AdministratorService	adminService;

	@Autowired
	private TabooService			tabooService;


	// Constructors -----------------------------------------------------------
	public MesageService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public Mesage create() {
		final Mesage r = new Mesage();
		r.setDate(new Date(System.currentTimeMillis() - 1000));
		r.setSender(this.loginService.getPrincipalActor());
		r.setPriority("NEUTRAL");
		return r;
	}

	public Collection<Mesage> findAll() {
		final Collection<Mesage> res = this.messageRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Mesage findOne(final int messageId) {
		return this.messageRepository.findOne(messageId);
	}

	public Mesage save(final Mesage mesage) {
		mesage.setSender(this.loginService.getPrincipalActor());
		this.checkPrincipalIsSender(mesage);
		Assert.notNull(mesage);
		mesage.setDate(new Date(System.currentTimeMillis() - 1000));
		return this.messageRepository.save(mesage);
	}

	public Mesage saveBySystem(final Mesage mesage) {
		Assert.notNull(mesage);
		mesage.setDate(new Date(System.currentTimeMillis() - 1000));
		return this.messageRepository.save(mesage);
	}

	public void delete(final Mesage mesage) {
		this.checkPrincipal(mesage);
		final String nameFolder = mesage.getFolder().getName().toLowerCase();
		if (nameFolder.equals("trash box"))
			this.messageRepository.delete(mesage);
		else {
			final Folder trashbox = this.folderService.getTrashbox(this.loginService.getPrincipalActor());
			mesage.setFolder(trashbox);
			final Folder actual = mesage.getFolder();
			actual.getMesages().remove(mesage);
			trashbox.getMesages().add(mesage);
			this.folderService.save(actual);
			this.folderService.save(trashbox);
			this.messageRepository.save(mesage);
		}
	}

	public void flush() {
		this.messageRepository.flush();
	}

	// Other business methods -------------------------------------------------

	public Mesage moveTo(final Mesage mesage, final Folder folder) {

		final List<Mesage> mns = folder.getMesages();
		mns.add(mesage);
		folder.setMesages(mns);
		this.folderService.save(folder);

		final Folder actual = mesage.getFolder();
		actual.getMesages().remove(mesage);
		this.folderService.save(actual);

		mesage.setFolder(folder);
		return this.save(mesage);

	}

	public Mesage sendMesage(final Mesage mesage) {
		final Actor sender = this.loginService.getPrincipalActor();
		final Actor recipient = mesage.getRecipient();

		//Mensaje enviado
		mesage.setDate(new Date(System.currentTimeMillis() - 1000));
		mesage.setSender(sender);
		mesage.setFolder(this.folderService.getOutbox(sender));

		//Mensaje recibido
		final Mesage message2 = this.create();
		message2.setDate(new Date(System.currentTimeMillis() - 1000));
		message2.setRecipient(recipient);
		message2.setSender(mesage.getSender());
		message2.setPriority(mesage.getPriority());
		message2.setBody(mesage.getBody());
		message2.setSubject(mesage.getSubject());
		message2.setFolder(this.folderService.getInbox(recipient));

		final Mesage res = this.messageRepository.save(mesage);

		if (this.detectSpam(mesage))
			message2.setFolder(this.folderService.getSpambox(message2.getRecipient()));
		this.messageRepository.save(message2);
		return res;
	}

	public Boolean detectSpam(final Mesage m) {
		Boolean res = false;
		for (final Taboo t : this.tabooService.findAll()) {
			final String s = t.getWord().toLowerCase();
			if (m.getSubject().toLowerCase().contains(s) || m.getBody().toLowerCase().contains(s)) {
				res = true;
				final Actor sender = m.getSender();
				//				sender.setSuspicious(res);
				this.actorService.save(sender);
				break;
			}
		}
		return res;
	}
	public void checkPrincipal(final Mesage obj) {
		boolean res = false;
		if (LoginService.getPrincipal().equals(obj.getFolder().getActor().getUserAccount()))
			res = true;
		Assert.isTrue(res);
	}

	public void checkPrincipalIsSender(final Mesage obj) {
		boolean res = false;
		if (LoginService.getPrincipal().equals(obj.getSender().getUserAccount()))
			res = true;
		Assert.isTrue(res);
	}

	public List<String> getPriorities() {
		final List<String> l = new ArrayList<String>();
		l.add("LOW");
		l.add("NEUTRAL");
		l.add("HIGH");
		return l;
	}

	public void send(final Mesage m1) {
		Assert.notNull(m1);
		final Mesage m2 = new Mesage();
		m2.setBody(m1.getBody());
		if (this.isSpam(m1))
			m2.setFolder(this.folderService.getSpambox(m1.getRecipient()));
		else
			m2.setFolder(this.folderService.getInbox(m1.getRecipient()));
		m2.setId(0);
		m2.setVersion(0);
		m2.setRecipient(m1.getRecipient());
		m2.setSender(m1.getSender());
		m2.setSubject(m1.getSubject());
		m2.setPriority(m1.getPriority());
		m2.setDate(m1.getDate());

		final Folder f1 = m1.getFolder();
		final Folder f2 = m2.getFolder();

		final List<Mesage> ms1 = f1.getMesages();
		final List<Mesage> ms2 = f2.getMesages();

		ms1.add(m1);
		ms2.add(m2);

		f1.setMesages(ms1);
		f2.setMesages(ms2);

		this.save(m2);
		this.save(m1);
		//		folderService.save(f1);
		//		folderService.save(f2);
	}

	public void sendNotificationBox(final Actor a) {
		final Folder notification = this.folderService.getNotificationmbox(a);
		final Mesage msn = this.create();

		msn.setRecipient(a);
		msn.setSender(this.adminService.findAll().iterator().next());
		msn.setSubject("Querido usuario...." + "\n" + "Dear user....");
		msn.setBody("Cambio de estado en alguna de tus aplicaciones." + "\n" + "Change in one of your applications.");
		msn.setDate(new Date(System.currentTimeMillis() - 1000));
		msn.setFolder(notification);
		final Mesage msn2 = this.saveBySystem(msn);

		final List<Mesage> msns = notification.getMesages();
		msns.add(msn2);
		notification.setMesages(msns);

		this.folderService.save2(notification);

	}

	private boolean isSpam(final Mesage m1) {
		boolean res = false;
		for (final Taboo t : this.tabooService.findAll()) {
			final String sw = t.getWord().toLowerCase();
			if (m1.getBody().toLowerCase().contains(sw.toLowerCase()) || m1.getSubject().toLowerCase().contains(sw.toLowerCase())) {
				res = true;
				break;
			}
		}
		return res;
	}
	public Mesage sendBroadcast(final BroadcastForm mesage) {
		Assert.isTrue(this.adminService.isAdmin());
		Mesage res = null;
		for (final Actor a : this.actorService.findAll()) {
			//Mensaje recibido
			final Mesage message2 = this.create();
			message2.setDate(new Date(System.currentTimeMillis() - 1000));
			message2.setRecipient(a);
			message2.setSender(this.loginService.getPrincipalActor());
			message2.setPriority(mesage.getPriority());
			message2.setBody(mesage.getBody());
			message2.setSubject(mesage.getSubject());
			message2.setFolder(this.folderService.getNotificationmbox(a));
			if (message2.getRecipient().getId() == this.loginService.getPrincipalActor().getId())
				res = this.messageRepository.save(message2);
			else
				this.messageRepository.save(message2);
		}
		return res;
	}

	public Mesage reconstruct(final MessageForm messageForm) {
		final Mesage m = this.create();
		m.setBody(messageForm.getBody());
		m.setRecipient(messageForm.getRecipient());
		m.setSubject(messageForm.getSubject());
		m.setDate(messageForm.getDate());
		m.setPriority(messageForm.getPriority());
		return m;
	}
}
