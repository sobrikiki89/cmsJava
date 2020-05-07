package module.report.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import app.core.model.EntityCode;

@Entity
@Table(name = "REPORT_CATEGORY")
public class ReportCategory extends EntityCode {

	private static final long serialVersionUID = 1L;
}
