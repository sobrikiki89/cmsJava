package module.report.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.model.EntityBase;

@Entity
@Table(name = "REPORT_DEFINITION")
public class ReportDefinition extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String paramHandlerClass;

	private String jspPath;

	private ReportCategory category;

	private GeneratorType generatorType;

	private List<ReportOutputFormat> outputFormats;

	@Id
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PARAM_HANDLER_CLASS")
	public String getParamHandlerClass() {
		return paramHandlerClass;
	}

	public void setParamHandlerClass(String paramHandlerClass) {
		this.paramHandlerClass = paramHandlerClass;
	}

	@Column(name = "JSP_PATH")
	public String getJspPath() {
		return jspPath;
	}

	public void setJspPath(String jspPath) {
		this.jspPath = jspPath;
	}

	@ManyToOne
	@JoinColumn(name = "CATEGORY_CODE")
	public ReportCategory getCategory() {
		return category;
	}

	public void setCategory(ReportCategory category) {
		this.category = category;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "GENERATOR_TYPE")
	public GeneratorType getGeneratorType() {
		return generatorType;
	}

	public void setGeneratorType(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

	@OneToMany(mappedBy = "definition", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
	public List<ReportOutputFormat> getOutputFormats() {
		return outputFormats;
	}

	public void setOutputFormats(List<ReportOutputFormat> outputFormats) {
		this.outputFormats = outputFormats;
	}
}
