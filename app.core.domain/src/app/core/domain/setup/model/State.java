package app.core.domain.setup.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import app.core.model.EntityCode;

@Entity
@Table(name = "STATE")
public class State extends EntityCode {

	private static final long serialVersionUID = 1L;
}
