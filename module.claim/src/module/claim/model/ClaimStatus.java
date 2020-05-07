package module.claim.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import app.core.model.EntityCodeActive;

@Entity
@Table(name = "CLAIM_STATUS")
public class ClaimStatus extends EntityCodeActive {

	private static final long serialVersionUID = 3776033229030864852L;

}
