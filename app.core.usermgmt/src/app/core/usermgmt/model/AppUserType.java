package app.core.usermgmt.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import app.core.model.EntityCode;

@Entity
@Table(name = "APP_USER_TYPE")
public class AppUserType extends EntityCode {

	private static final long serialVersionUID = 1L;

}