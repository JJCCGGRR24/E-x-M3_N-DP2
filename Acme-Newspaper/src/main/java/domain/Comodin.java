
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(name = "index_finalMode", columnList = "finalMode"), @Index(name = "index_newspaper", columnList = "newspaper_id"), @Index(name = "index_administrator", columnList = "administrator_id"), @Index(name = "index_moment", columnList = "moment"),

})
public class Comodin extends DomainEntity {

	//Attributes
	private String	ticker;
	private int		gauge;
	private Date	moment;
	private String	shortTitle;
	private String	description;
	private boolean	finalMode;


	@Column(unique = true)
	@NotBlank
	@SafeHtml(whitelistType = WhiteListType.NONE)
	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(final String ticker) {
		this.ticker = ticker;
	}

	@Range(min = 1, max = 3)
	public int getGauge() {
		return this.gauge;
	}

	public void setGauge(final int gauge) {
		this.gauge = gauge;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getMoment() {
		return this.moment;
	}

	public void setMoment(final Date moment) {
		this.moment = moment;
	}

	@SafeHtml(whitelistType = WhiteListType.NONE)
	@NotBlank
	@Size(max = 255)
	public String getShortTitle() {
		return this.shortTitle;
	}

	public void setShortTitle(final String shortTitle) {
		this.shortTitle = shortTitle;
	}
	@NotBlank
	@SafeHtml(whitelistType = WhiteListType.NONE)
	@Size(max = 65000)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isFinalMode() {
		return this.finalMode;
	}

	public void setFinalMode(final boolean finalMode) {
		this.finalMode = finalMode;
	}


	//Relationship

	private Newspaper		newspaper;
	private Administrator	administrator;


	@Valid
	@ManyToOne(optional = true)
	public Newspaper getNewspaper() {
		return this.newspaper;
	}

	public void setNewspaper(final Newspaper newspaper) {
		this.newspaper = newspaper;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Administrator getAdministrator() {
		return this.administrator;
	}

	public void setAdministrator(final Administrator administrator) {
		this.administrator = administrator;
	}
}
