package app.core.usermgmt.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * Keeps track of the user's recent passwords (in encrypted form) so that when
 * the user changes their password they can be prevented from selecting a
 * password they have recently used.
 * <p>
 * A user's current password is kept on the User object.
 */
@Entity
@Table(name = "PASSWORD_HISTORY")
public class PasswordHistory extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;
	private User user;
	private String salt;
	private String password;
	private Long createUser;
	private Date createDate;

	// Constructors

	/** default constructor */
	public PasswordHistory() {
	}

	/** constructor with id */
	public PasswordHistory(Long id) {
		this.id = id;
	}

	// Property accessors

	/** Synthetic ID */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The user this passwords belongs to.
	 */
	@ManyToOne
	@JoinColumn(name = "USER_ID")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * The "salt" used when encrypting the user's password.
	 * <p>
	 * All encryption algorithms are vulnerable to table-lookup-based attacks,
	 * ie where someone precomputes the encrypted form of thousands of common
	 * passwords, then looks at the encrypted data to see whether any of the
	 * encrypted patterns match something in their lookup table. This attack can
	 * be foiled by using a random "salt" together with the user password when
	 * encrypting it. The salt doesn't need to remain secret, it just needs to
	 * be different for each user so that the same common password encrypted
	 * with different salts results in different output, thereby foiling any
	 * lookups.
	 */
	@Column(name = "SALT")
	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * The encrypted form of the user's original password. See PasswordManager
	 * for Encryption algorithm
	 */
	@Column(name = "PASSWORD")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * The user who created the parent entity.
	 */
	@Column(name = "CREUSER")
	public Long getCreateUserId() {
		return this.createUser;
	}

	public void setCreateUserId(Long createUser) {
		this.createUser = createUser;
	}

	/**
	 * The date at which the parent entity was first persisted into the
	 * database.
	 */
	@Column(name = "CREDATE")
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
