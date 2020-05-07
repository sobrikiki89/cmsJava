package module.report.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import app.core.registry.Module;
import module.report.dto.ReportAccessDTO;
import module.report.model.OutputFileFormat;
import module.report.model.ReportAccessControl;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.model.ReportOutputFormat;

public interface ReportSetupService {
	public List<ReportCategory> getCategories();

	public String createCategory(ReportCategory category);

	public void updateCategory(ReportCategory category);

	public List<ReportDefinition> getReportDefinitionByCategory(ReportCategory category);

	public ReportDefinition getReportDefinitionById(Long id);

	public ReportOutputFormat getReportOutputFormatByFormat(Long definitionId, OutputFileFormat format);

	public DataSet<ReportAccessDTO> getReportAccessGrid(DatatablesCriterias criterias) throws BaseApplicationException;

	public List<ReportAccessControl> getReportAccessList();

	public ReportAccessControl getAccessControlById(Long id);

	public Long createAccessControl(ReportAccessControl accessControl);

	public ReportAccessControl updateAccessControl(ReportAccessControl accessControl);

	public void deleteAccessCountrol(List<ReportAccessControl> accessControls);

	public void updateReport(Module module);
}
