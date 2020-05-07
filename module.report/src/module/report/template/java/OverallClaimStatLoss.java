package module.report.template.java;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import app.core.logging.LogUtils;
import module.claim.model.ClaimStatusEnum;
import module.report.ReportModule;
import module.report.generator.DataSourceAware;
import module.report.generator.Generator;
import module.report.handler.ReportParamHandler;
import module.report.model.ReportStatus;
import module.report.model.ReportSubmission;
import module.report.service.ReportSubmissionService;

public class OverallClaimStatLoss extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverallClaimStatLoss.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;

	public OverallClaimStatLoss(ApplicationContext context, String logFile) {
		super(context, logFile);
	}

	@Override
	public ReportSubmission getSubmission() {
		return this.submission;
	}

	@Override
	protected void setup(ReportParamHandler handler, ReportSubmission submission) {
		this.handler = handler;
		this.submission = submission;
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	@Override
	public void run() {
		LogUtils.addSiftAppender(((ReportModule) context.getBean("ReportModule")).getModuleName());
		LogUtils.startLogging(submission.getFullLogFile());
		LocalDateTime startDate = LocalDateTime.now();
		LOGGER.info("Start date : " + startDate.format(DATETIME_FORMAT));

		List<CompanyDepartmentDTO> list = getAllClaimDepartmentAndCompanyId();
		LOGGER.info("Total company department found : [" + list.size() + "]");
		Set<String> companyNameSet = new TreeSet<>();
		for (CompanyDepartmentDTO companyDepartment : list) {
			companyNameSet.add(companyDepartment.getName());
			getClaimSummary(companyDepartment);
		}

		StringBuilder companyName = new StringBuilder();
		for (String cName : companyNameSet) {
			if (companyName.length() > 0) {
				companyName.append(", ");
			}
			companyName.append(cName);
		}

		CompanyDTO grandTotalDTO = new CompanyDTO();
		grandTotalDTO.setName(companyName.toString());
		getClaimSummary(grandTotalDTO);

		doPrintExcel(grandTotalDTO, list);
		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);

		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));

		LogUtils.stopLogging();
	}

	protected void doPrintExcel(CompanyDTO grandTotalDTO, List<CompanyDepartmentDTO> list) {
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		boolean exception = false;
		try {
			wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			sheet.setColumnWidth(0, 7200);
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 6500);
			sheet.setColumnWidth(4, 3000);
			sheet.setColumnWidth(5, 7200);
			sheet.setColumnWidth(6, 4000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6500);

			// Style for the report header
			HSSFCellStyle headerStyle = wb.createCellStyle();
			HSSFFont headerFont = wb.createFont();
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setBold(true);
			headerFont.setFontName("SansSerif");
			headerStyle.setFont(headerFont);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
			headerStyle.setWrapText(true);

			// Style for the table label
			HSSFCellStyle tableLabelStyle = wb.createCellStyle();
			HSSFFont tableLabelFont = wb.createFont();
			tableLabelFont.setFontHeightInPoints((short) 10);
			tableLabelFont.setFontName("SansSerif");
			tableLabelFont.setBold(true);
			tableLabelStyle.setFont(tableLabelFont);
			tableLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			tableLabelStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
			tableLabelStyle.setWrapText(true);

			// Style for the table header
			HSSFCellStyle tableHeaderStyle = wb.createCellStyle();
			HSSFFont tableHeaderFont = wb.createFont();
			tableHeaderFont.setFontHeightInPoints((short) 10);
			tableHeaderFont.setFontName("SansSerif");
			tableHeaderFont.setBold(true);
			tableHeaderStyle.setFont(tableHeaderFont);
			tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
			tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			tableHeaderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
			tableHeaderStyle.setWrapText(true);
			tableHeaderStyle.setBorderTop(BorderStyle.THIN);
			tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
			tableHeaderStyle.setBorderRight(BorderStyle.THIN);
			tableHeaderStyle.setBorderBottom(BorderStyle.THIN);

			// Style for the table body
			HSSFCellStyle tableBodyStyle = wb.createCellStyle();
			HSSFFont tableBodyFont = wb.createFont();
			tableBodyFont.setFontHeightInPoints((short) 10);
			tableBodyFont.setFontName("SansSerif");
			tableBodyStyle.setFont(tableBodyFont);
			tableBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			tableBodyStyle.setWrapText(true);
			tableBodyStyle.setBorderLeft(BorderStyle.THIN);
			tableBodyStyle.setBorderRight(BorderStyle.THIN);

			// Style for the table footer
			HSSFCellStyle tableFooterStyle = wb.createCellStyle();
			HSSFFont tableFooterFont = wb.createFont();
			tableFooterFont.setFontHeightInPoints((short) 10);
			tableFooterFont.setFontName("SansSerif");
			tableFooterFont.setBold(true);
			tableFooterStyle.setFont(tableFooterFont);
			tableFooterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			tableFooterStyle.setWrapText(true);
			tableFooterStyle.setBorderTop(BorderStyle.THIN);
			tableFooterStyle.setBorderLeft(BorderStyle.THIN);
			tableFooterStyle.setBorderRight(BorderStyle.THIN);
			tableFooterStyle.setBorderBottom(BorderStyle.THIN);

			HSSFCellStyle tableFooterCenterStyle = wb.createCellStyle();
			tableFooterCenterStyle.setFont(tableFooterFont);
			tableFooterCenterStyle.setAlignment(HorizontalAlignment.CENTER);
			tableFooterCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			tableFooterCenterStyle.setWrapText(true);
			tableFooterCenterStyle.setBorderTop(BorderStyle.THIN);
			tableFooterCenterStyle.setBorderLeft(BorderStyle.THIN);
			tableFooterCenterStyle.setBorderRight(BorderStyle.THIN);
			tableFooterCenterStyle.setBorderBottom(BorderStyle.THIN);

			// Report date and printed by
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("Report Date :");
			cell = row.createCell(1);
			cell.setCellValue((String) handler.getParamMap().get(ReportParamHandler.REPORT_DATETIME));

			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellValue("Prepared By :");
			cell = row.createCell(1);
			cell.setCellValue((String) handler.getParamMap().get(ReportParamHandler.REPORT_CREATOR));

			// Setting header
			row = sheet.createRow(3);
			row.setHeight((short) 400);
			cell = row.createCell(1);
			cell.setCellValue("Overall Claims Statistics (by Loss Date)");
			cell.setCellStyle(headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), // first
																		// row
																		// (0-based)
					row.getRowNum(), // last row (0-based)
					1, // first column (0-based)
					7 // last column (0-based)
			));

			int departmentSpecifiedTableStartIndex = 6;

			// From-To Loss Date
			Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("dispFromLossDate");
			Timestamp toLossDate = (Timestamp) handler.getParamMap().get("dispToLossDate");

			row = sheet.createRow(departmentSpecifiedTableStartIndex++);
			cell = row.createCell(0);
			cell.setCellValue("From Loss Date :");
			cell = row.createCell(1);
			if (fromLossDate != null) {
				cell.setCellValue(sdf.format(fromLossDate));
			}
			row = sheet.createRow(departmentSpecifiedTableStartIndex++);
			cell = row.createCell(0);
			cell.setCellValue("To Loss Date :");
			cell = row.createCell(1);
			if (toLossDate != null) {
				cell.setCellValue(sdf.format(toLossDate));
			}

			// Insurance Type
			row = sheet.createRow(departmentSpecifiedTableStartIndex++);
			cell = row.createCell(0);
			cell.setCellValue("Class of Insurance :");
			cell = row.createCell(1);
			String insuranceClass = (String) handler.getParamMap().get("dispInsuranceClass");
			if (insuranceClass != null) {
				cell.setCellValue(insuranceClass);
			}

			// Companies
			String companies = (String) handler.getParamMap().get("dispCompanies");
			if (companies != null) {
				row = sheet.createRow(departmentSpecifiedTableStartIndex++);
				cell = row.createCell(0);
				cell.setCellValue("Companies:");
				cell = row.createCell(1);
				cell.setCellValue(companies);
			}

			// Two more empty rows
			departmentSpecifiedTableStartIndex++;
			departmentSpecifiedTableStartIndex++;

			// Creating grand total table

			// We only print the grand total if it has more than 1 department
			if (list.size() > 1) {
				// 1. Label
				row = sheet.createRow(departmentSpecifiedTableStartIndex++);
				cell = row.createCell(0);
				cell.setCellValue("Summary - by Company");
				cell.setCellStyle(tableLabelStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));

				departmentSpecifiedTableStartIndex++; // Empty line

				// 2. Company Name
				row = sheet.createRow(departmentSpecifiedTableStartIndex++);
				cell = row.createCell(0);
				cell.setCellValue("Company:");
				cell.setCellStyle(tableLabelStyle);
				cell = row.createCell(1);
				cell.setCellValue(grandTotalDTO.getName());
				cell.setCellStyle(tableLabelStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

				// 3. Table Header
				departmentSpecifiedTableStartIndex++; // Empty line
				row = sheet.createRow(departmentSpecifiedTableStartIndex++);
				cell = row.createCell(0);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("Claim Status");
				cell = row.createCell(1);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("No of Claims");
				cell = row.createCell(2);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("Estimated Loss Amount (RM)");
				cell = row.createCell(3);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("Offered / Paid Amount (RM)");

				// 4. Statictics Table Body
				for (int j = 0; j < grandTotalDTO.getDetails().size(); j++) {
					ClaimDetailDTO dto = grandTotalDTO.getDetails().get(j);
					row = sheet.createRow(departmentSpecifiedTableStartIndex++);
					cell = row.createCell(0);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(dto.getStatus().getLabel());
					cell = row.createCell(1);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(dto.getCount());
					cell = row.createCell(2);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(dto.getClaimedAmount().doubleValue());
					cell = row.createCell(3);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					if (ClaimStatusEnum.PPYMT.equals(dto.getStatus()) || ClaimStatusEnum.PACC.equals(dto.getStatus())) {
						cell.setCellValue(dto.getOfferedAmount().doubleValue());
					} else {
						cell.setCellValue(dto.getPaidAmount().doubleValue());
					}
				}

				// 5. Statistics Table Footer
				row = sheet.createRow(departmentSpecifiedTableStartIndex++);
				cell = row.createCell(0);
				cell.setCellStyle(tableFooterCenterStyle);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("Total");
				cell = row.createCell(1);
				cell.setCellStyle(tableFooterStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(grandTotalDTO.getTotalCount());
				cell = row.createCell(2);
				cell.setCellStyle(tableFooterStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(grandTotalDTO.getTotalClaimedAmount().doubleValue());
				cell = row.createCell(3);
				cell.setCellStyle(tableFooterStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(grandTotalDTO.getTotalOfferedOrPaidAmount().doubleValue());

				departmentSpecifiedTableStartIndex++; // Empty line
				departmentSpecifiedTableStartIndex++; // Empty line

				// 6. Label
				row = sheet.createRow(departmentSpecifiedTableStartIndex++);
				cell = row.createCell(0);
				cell.setCellValue("Summary - by Department");
				cell.setCellStyle(tableLabelStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));

				departmentSpecifiedTableStartIndex++; // Empty line
			}

			// Creating detail
			for (int i = 0; i < list.size(); i += 2) {

				// 1. Company Name
				row = sheet.createRow(departmentSpecifiedTableStartIndex + (i * 8));

				// Left table
				CompanyDepartmentDTO left = list.get(i);
				cell = row.createCell(0);
				cell.setCellValue("Company:");
				cell.setCellStyle(tableLabelStyle);
				cell = row.createCell(1);
				cell.setCellValue(left.getName());
				cell.setCellStyle(tableLabelStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

				// Right table
				if (i + 1 < list.size()) {
					CompanyDepartmentDTO right = list.get(i + 1);
					cell = row.createCell(5);
					cell.setCellValue("Company:");
					cell.setCellStyle(tableLabelStyle);
					cell = row.createCell(6);
					cell.setCellValue(right.getName());
					cell.setCellStyle(tableLabelStyle);
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 8));
				}

				// 2. Department Name
				row = sheet.createRow(departmentSpecifiedTableStartIndex + (i * 8) + 1);

				// Left table
				cell = row.createCell(0);
				cell.setCellValue("Department:");
				cell.setCellStyle(tableLabelStyle);
				cell = row.createCell(1);
				cell.setCellValue(left.getDepartmentId() != null ? left.getDepartmentName()
						: "Claim without department specified");
				cell.setCellStyle(tableLabelStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

				// Right table
				if (i + 1 < list.size()) {
					CompanyDepartmentDTO right = list.get(i + 1);
					cell = row.createCell(5);
					cell.setCellValue("Department:");
					cell.setCellStyle(tableLabelStyle);
					cell = row.createCell(6);
					cell.setCellValue(right.getDepartmentId() != null ? right.getDepartmentName()
							: "Claim without department specified");
					cell.setCellStyle(tableLabelStyle);
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 8));
				}

				// 3. Statistics Table Header
				row = sheet.createRow(departmentSpecifiedTableStartIndex + (i * 8) + 3);

				// Left table
				cell = row.createCell(0);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("Claim Status");
				cell = row.createCell(1);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("No of Claims");
				cell = row.createCell(2);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("Estimated Loss Amount (RM)");
				cell = row.createCell(3);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("Offered / Paid Amount (RM)");

				// Right table
				if (i + 1 < list.size()) {
					cell = row.createCell(5);
					cell.setCellStyle(tableHeaderStyle);
					cell.setCellValue("Claim Status");
					cell = row.createCell(6);
					cell.setCellStyle(tableHeaderStyle);
					cell.setCellValue("No of Claims");
					cell = row.createCell(7);
					cell.setCellStyle(tableHeaderStyle);
					cell.setCellValue("Estimated Loss Amount (RM)");
					cell = row.createCell(8);
					cell.setCellStyle(tableHeaderStyle);
					cell.setCellValue("Offered / Paid Amount (RM)");
				}

				// 4. Statictics Table Body
				for (int j = 0; j < left.getDetails().size(); j++) {
					ClaimDetailDTO dto = left.getDetails().get(j);
					row = sheet.createRow(departmentSpecifiedTableStartIndex + (i * 8) + 4 + j);

					// Left table
					cell = row.createCell(0);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(dto.getStatus().getLabel());
					cell = row.createCell(1);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(dto.getCount());
					cell = row.createCell(2);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(dto.getClaimedAmount().doubleValue());
					cell = row.createCell(3);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					if (ClaimStatusEnum.PPYMT.equals(dto.getStatus()) || ClaimStatusEnum.PACC.equals(dto.getStatus())) {
						cell.setCellValue(dto.getOfferedAmount().doubleValue());
					} else {
						cell.setCellValue(dto.getPaidAmount().doubleValue());
					}
				}

				// Right table
				if (i + 1 < list.size()) {
					CompanyDepartmentDTO right = list.get(i + 1);
					for (int j = 0; j < right.getDetails().size(); j++) {
						ClaimDetailDTO dto = right.getDetails().get(j);
						row = sheet.getRow(departmentSpecifiedTableStartIndex + (i * 8) + 4 + j);

						cell = row.createCell(5);
						cell.setCellStyle(tableBodyStyle);
						cell.setCellType(CellType.STRING);
						cell.setCellValue(dto.getStatus().getLabel());
						cell = row.createCell(6);
						cell.setCellStyle(tableBodyStyle);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(dto.getCount());
						cell = row.createCell(7);
						cell.setCellStyle(tableBodyStyle);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(dto.getClaimedAmount().doubleValue());
						cell = row.createCell(8);
						cell.setCellStyle(tableBodyStyle);
						cell.setCellType(CellType.NUMERIC);
						if (ClaimStatusEnum.PPYMT.equals(dto.getStatus()) || ClaimStatusEnum.PACC.equals(dto.getStatus())) {
							cell.setCellValue(dto.getOfferedAmount().doubleValue());
						} else {
							cell.setCellValue(dto.getPaidAmount().doubleValue());
						}
					}
				}

				// 5. Statistics Table Footer
				row = sheet.createRow(departmentSpecifiedTableStartIndex + (i * 8) + 4 + left.getDetails().size());

				// Left table
				cell = row.createCell(0);
				cell.setCellStyle(tableFooterCenterStyle);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("Total");
				cell = row.createCell(1);
				cell.setCellStyle(tableFooterStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(left.getTotalCount());
				cell = row.createCell(2);
				cell.setCellStyle(tableFooterStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(left.getTotalClaimedAmount().doubleValue());
				cell = row.createCell(3);
				cell.setCellStyle(tableFooterStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(left.getTotalOfferedOrPaidAmount().doubleValue());

				// Right table
				if (i + 1 < list.size()) {
					CompanyDepartmentDTO right = list.get(i + 1);
					row = sheet.getRow(departmentSpecifiedTableStartIndex + (i * 8) + 4 + right.getDetails().size());

					cell = row.createCell(5);
					cell.setCellStyle(tableFooterCenterStyle);
					cell.setCellType(CellType.STRING);
					cell.setCellValue("Total");
					cell = row.createCell(6);
					cell.setCellStyle(tableFooterStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(right.getTotalCount());
					cell = row.createCell(7);
					cell.setCellStyle(tableFooterStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(right.getTotalClaimedAmount().doubleValue());
					cell = row.createCell(8);
					cell.setCellStyle(tableFooterStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(right.getTotalOfferedOrPaidAmount().doubleValue());
				}
			}
			fileOut = new FileOutputStream(submission.getFullOutputFile());
			wb.write(fileOut);
			fileOut.close();
			wb.close();
			if (list.size() == 0) {
				submission.setStatus(ReportStatus.NO_DATA_FOUND);
			} else {
				submission.setStatus(ReportStatus.FINISHED);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("Error in opening the output file", e);
			exception = true;
		} catch (IOException e) {
			LOGGER.error("Error in writing to the output file", e);
			exception = true;
		} finally {
			if (exception) {
				submission.setOutputFile(null);
				submission.setStatus(ReportStatus.ERROR);
			}
			submission.setEndDate(new Date());

			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException ignore) {
				}
			}

			if (wb != null) {
				try {
					wb.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	protected void getClaimSummary(CompanyDTO companyDTO) {
		LOGGER.info("Counting claim summary for company (without department)");

		StringBuilder sql = new StringBuilder();
		sql.append(
				"select comp.name, count(c.id), sum(c.est_lost_amt), sum(c.offer_amt), sum(c.approval_amt), c.status ");
		sql.append("from claim c ");
		sql.append("inner join policy p ");
		sql.append(" on c.policy_id = p.id ");
		sql.append("left join company comp ");
		sql.append(" on comp.id = p.company_id ");
		sql.append("where (c.deleted IS NULL OR c.deleted = false) ");

		Object[] insuranceClassCodes = (Object[]) handler.getParamMap().get("insuranceClass");
		if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
			sql.append(" and p.insurance_class_code in (");
			for (int i = 0; i < insuranceClassCodes.length; i++) {
				sql.append("?");
				if (i < insuranceClassCodes.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		if (companyIds != null && companyIds.length > 0) {
			sql.append(" and comp.id in (");
			for (int i = 0; i < companyIds.length; i++) {
				sql.append("?");
				if (i < companyIds.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		if (userCompany != null && userCompany.length > 0) {
			sql.append(" and comp.code in (");
			for (int i = 0; i < userCompany.length; i++) {
				sql.append("?");
				if (i < userCompany.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("fromLossDate");
		Timestamp toLossDate = (Timestamp) handler.getParamMap().get("toLossDate");
		if (fromLossDate != null && toLossDate != null) {
			sql.append(" and c.loss_date between ? and ? ");
		} else if (fromLossDate != null && toLossDate == null) {
			sql.append(" and c.loss_date >= ? ");
		} else if (fromLossDate == null && toLossDate != null) {
			sql.append(" and c.loss_date <= ? ");
		}
		sql.append("group by comp.name, c.status");

		LOGGER.info("SQL : [" + sql.toString() + "]");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql.toString());

			int idx = 1;

			if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
				for (int i = 0; i < insuranceClassCodes.length; i++) {
					ps.setString(idx++, (String) insuranceClassCodes[i]);
				}
			}

			if (companyIds != null && companyIds.length > 0) {
				for (int i = 0; i < companyIds.length; i++) {
					ps.setLong(idx++, (Long) companyIds[i]);
				}
			}

			if (userCompany != null && userCompany.length > 0) {
				for (int i = 0; i < userCompany.length; i++) {
					ps.setString(idx++, (String) userCompany[i]);
				}
			}

			if (fromLossDate != null && toLossDate != null) {
				ps.setTimestamp(idx++, fromLossDate);
				ps.setTimestamp(idx++, toLossDate);
			} else if (fromLossDate != null && toLossDate == null) {
				ps.setTimestamp(idx++, fromLossDate);
			} else if (fromLossDate == null && toLossDate != null) {
				ps.setTimestamp(idx++, toLossDate);
			}

			rs = ps.executeQuery();
			BigDecimal totalClaimedAmount = BigDecimal.ZERO;
			BigDecimal totalOfferedOrPaidAmount = BigDecimal.ZERO;
			int totalCount = 0;
			while (rs.next()) {
				for (ClaimDetailDTO detail : companyDTO.getDetails()) {
					if (detail.getStatus().name().equals(rs.getString(6))) {
						BigDecimal count = rs.getBigDecimal(2);
						if (count == null) {
							count = BigDecimal.ZERO;
						}
						detail.setCount(count.intValue());

						BigDecimal claimedAmount = rs.getBigDecimal(3);
						if (claimedAmount == null) {
							claimedAmount = BigDecimal.ZERO;
						}
						detail.setClaimedAmount(claimedAmount);

						BigDecimal offeredAmount = rs.getBigDecimal(4);
						if (offeredAmount == null) {
							offeredAmount = BigDecimal.ZERO;
						}
						detail.setOfferedAmount(offeredAmount);

						BigDecimal paidAmount = rs.getBigDecimal(5);
						if (paidAmount == null) {
							paidAmount = BigDecimal.ZERO;
						}
						detail.setPaidAmount(paidAmount);

						totalClaimedAmount = totalClaimedAmount.add(detail.getClaimedAmount());
						if (ClaimStatusEnum.PPYMT.equals(detail.getStatus())
								|| ClaimStatusEnum.PACC.equals(detail.getStatus())) {
							totalOfferedOrPaidAmount = totalOfferedOrPaidAmount.add(detail.getOfferedAmount());
						} else {
							totalOfferedOrPaidAmount = totalOfferedOrPaidAmount.add(detail.getPaidAmount());
						}
						totalCount += detail.getCount();
					}
				}
			}

			LOGGER.info("Total Claimed Amount : [" + totalClaimedAmount + "]");
			LOGGER.info("Total Offred / Paid Amount : [" + totalOfferedOrPaidAmount + "]");
			LOGGER.info("Total Record : [" + totalCount + "]");
			companyDTO.setTotalClaimedAmount(totalClaimedAmount);
			companyDTO.setTotalOfferedOrPaidAmount(totalOfferedOrPaidAmount);
			companyDTO.setTotalCount(totalCount);
		} catch (SQLException e) {
			LOGGER.error("Error in getting claim info for company", e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ignore) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ignore) {
				}
			}
		}
	}

	protected void getClaimSummary(CompanyDepartmentDTO companyDepartmentDTO) {
		LOGGER.info("Counting claim summary for company [" + companyDepartmentDTO.getName() + "], Department Id ["
				+ companyDepartmentDTO.getDepartmentId() + "]");

		StringBuilder sql = new StringBuilder();
		sql.append("select count(c.id), sum(c.est_lost_amt), sum(c.offer_amt), sum(c.approval_amt), c.status ");
		sql.append("from claim c ");
		sql.append("inner join policy p ");
		sql.append(" on c.policy_id = p.id ");
		sql.append("where (c.deleted is null or c.deleted = false) ");
		sql.append(" and p.company_id = ? ");
		sql.append(" and c.department_id " + (companyDepartmentDTO.getDepartmentId() == null ? "is null " : "= ? "));

		Object[] insuranceClassCodes = (Object[]) handler.getParamMap().get("insuranceClass");
		if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
			sql.append(" and p.insurance_class_code in (");
			for (int i = 0; i < insuranceClassCodes.length; i++) {
				sql.append("?");
				if (i < insuranceClassCodes.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("fromLossDate");
		Timestamp toLossDate = (Timestamp) handler.getParamMap().get("toLossDate");
		if (fromLossDate != null && toLossDate != null) {
			sql.append(" and c.loss_date between ? and ? ");
		} else if (fromLossDate != null && toLossDate == null) {
			sql.append(" and c.loss_date >= ? ");
		} else if (fromLossDate == null && toLossDate != null) {
			sql.append(" and c.loss_date <= ? ");
		}
		sql.append("group by c.status");

		LOGGER.info("SQL : [" + sql.toString() + "]");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql.toString());
			int idx = 1;
			ps.setLong(idx++, companyDepartmentDTO.getCompanyId());
			if (companyDepartmentDTO.getDepartmentId() != null) {
				ps.setLong(idx++, companyDepartmentDTO.getDepartmentId());
			}

			if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
				for (int i = 0; i < insuranceClassCodes.length; i++) {
					ps.setString(idx++, (String) insuranceClassCodes[i]);
				}
			}

			if (fromLossDate != null && toLossDate != null) {
				ps.setTimestamp(idx++, fromLossDate);
				ps.setTimestamp(idx++, toLossDate);
			} else if (fromLossDate != null && toLossDate == null) {
				ps.setTimestamp(idx++, fromLossDate);
			} else if (fromLossDate == null && toLossDate != null) {
				ps.setTimestamp(idx++, toLossDate);
			}

			rs = ps.executeQuery();
			BigDecimal totalClaimedAmount = BigDecimal.ZERO;
			BigDecimal totalOfferedOrPaidAmount = BigDecimal.ZERO;
			int totalCount = 0;
			while (rs.next()) {
				for (ClaimDetailDTO detail : companyDepartmentDTO.getDetails()) {
					if (detail.getStatus().name().equals(rs.getString(5))) {

						BigDecimal count = rs.getBigDecimal(1);
						if (count == null) {
							count = BigDecimal.ZERO;
						}
						detail.setCount(count.intValue());

						BigDecimal claimedAmount = rs.getBigDecimal(2);
						if (claimedAmount == null) {
							claimedAmount = BigDecimal.ZERO;
						}
						detail.setClaimedAmount(claimedAmount);

						BigDecimal offeredAmount = rs.getBigDecimal(3);
						if (offeredAmount == null) {
							offeredAmount = BigDecimal.ZERO;
						}
						detail.setOfferedAmount(offeredAmount);

						BigDecimal paidAmount = rs.getBigDecimal(4);
						if (paidAmount == null) {
							paidAmount = BigDecimal.ZERO;
						}
						detail.setPaidAmount(paidAmount);

						totalClaimedAmount = totalClaimedAmount.add(detail.getClaimedAmount());
						if (ClaimStatusEnum.PPYMT.equals(detail.getStatus())
								|| ClaimStatusEnum.PACC.equals(detail.getStatus())) {
							totalOfferedOrPaidAmount = totalOfferedOrPaidAmount.add(detail.getOfferedAmount());
						} else {
							totalOfferedOrPaidAmount = totalOfferedOrPaidAmount.add(detail.getPaidAmount());
						}
						totalCount += detail.getCount();
					}
				}
			}

			LOGGER.info("Total Claimed Amount per Department : [" + totalClaimedAmount + "]");
			LOGGER.info("Total Offered / Paid Amount per Department : [" + totalOfferedOrPaidAmount + "]");
			LOGGER.info("Total Record per Department : [" + totalCount + "]");
			companyDepartmentDTO.setTotalClaimedAmount(totalClaimedAmount);
			companyDepartmentDTO.setTotalOfferedOrPaidAmount(totalOfferedOrPaidAmount);
			companyDepartmentDTO.setTotalCount(totalCount);
		} catch (SQLException e) {
			LOGGER.error("Error in getting claim info for company and department", e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ignore) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ignore) {
				}
			}
		}
	}

	protected List<CompanyDepartmentDTO> getAllClaimDepartmentAndCompanyId() {
		LOGGER.info("Getting Company and Department info from Claim table");

		StringBuilder sql = new StringBuilder();
		sql.append(
				"select distinct * from ((select distinct d.id, d.code, d.name, p.company_id, comp.name as comp_name ");
		sql.append("from claim c ");
		sql.append("inner join policy p ");
		sql.append(" on c.policy_id = p.id ");
		sql.append("left join company comp ");
		sql.append(" on comp.id = p.company_id ");
		sql.append("left join company_department d ");
		sql.append(" on comp.id = d.company_id ");
		sql.append("where (c.deleted is null or c.deleted = false) ");
		sql.append(" and p.company_id is not null ");
		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		if (companyIds != null && companyIds.length > 0) {
			sql.append(" and comp.id in (");
			for (int i = 0; i < companyIds.length; i++) {
				sql.append("?");
				if (i < companyIds.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		if (userCompany != null && userCompany.length > 0) {
			sql.append(" and comp.code in (");
			for (int i = 0; i < userCompany.length; i++) {
				sql.append("?");
				if (i < userCompany.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}
		sql.append("group by d.id, d.code, d.name, p.company_id, comp.name ");
		sql.append("order by comp.name, p.company_id, d.id, d.code, d.name) ");
		sql.append("union all ");
		sql.append("(select distinct cast(null as bigint), null, null, p.company_id, comp.name as comp_name ");
		sql.append("from claim c ");
		sql.append("inner join policy p ");
		sql.append(" on c.policy_id = p.id ");
		sql.append("left join company comp ");
		sql.append(" on comp.id = p.company_id ");
		sql.append("where (c.deleted is null or c.deleted = false) ");
		sql.append(" and p.company_id is not null ");
		companyIds = (Object[]) handler.getParamMap().get("companies");
		if (companyIds != null && companyIds.length > 0) {
			sql.append(" and comp.id in (");
			for (int i = 0; i < companyIds.length; i++) {
				sql.append("?");
				if (i < companyIds.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		userCompany = (Object[]) handler.getParamMap().get("userCompany");
		if (userCompany != null) {
			sql.append(" and comp.code in (");
			for (int i = 0; i < userCompany.length; i++) {
				sql.append("?");
				if (i < userCompany.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}
		sql.append("group by p.company_id, comp.name ");
		sql.append("order by comp.name, p.company_id) ) as tbl ");
		sql.append("order by tbl.comp_name, tbl.company_id, tbl.id, tbl.code, tbl.name");

		LOGGER.info("SQL : [" + sql.toString() + "]");
		List<CompanyDepartmentDTO> list = new ArrayList<CompanyDepartmentDTO>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql.toString());
			int index = 1;
			if (companyIds != null && companyIds.length > 0) {
				for (Object companyId : companyIds) {
					ps.setLong(index++, (Long) companyId);
				}
			}
			if (userCompany != null && userCompany.length > 0) {
				for (Object uc : userCompany) {
					ps.setString(index++, (String) uc);
				}
			}

			if (companyIds != null && companyIds.length > 0) {
				for (Object companyId : companyIds) {
					ps.setLong(index++, (Long) companyId);
				}
			}

			if (userCompany != null && userCompany.length > 0) {
				for (Object uc : userCompany) {
					ps.setString(index++, (String) uc);
				}
			}

			rs = ps.executeQuery();
			while (rs.next()) {
				CompanyDepartmentDTO dto = new CompanyDepartmentDTO();
				dto.setDepartmentId(rs.getLong(1));
				if (rs.wasNull()) {
					dto.setDepartmentId(null);
				}
				dto.setDepartmentCode(rs.getString(2));
				dto.setDepartmentName(rs.getString(3));
				dto.setCompanyId(rs.getLong(4));
				dto.setName(rs.getString(5));
				list.add(dto);
				LOGGER.info("Found Company [" + dto.getName() + "], Department ID [" + dto.getDepartmentId()
						+ "], Department Code [" + dto.getDepartmentCode() + "], Department Name ["
						+ dto.getDepartmentName() + "]");
			}

		} catch (SQLException e) {
			LOGGER.error("Error in getting company and department info", e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ignore) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ignore) {
				}
			}
		}
		return list;
	}

	protected String getInsuranceTypeNameByCode() {
		LOGGER.info("Getting Insurance Type name");

		Object[] insuranceClassCodes = (Object[]) handler.getParamMap().get("insuranceClass");
		if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {

			StringBuilder sql = new StringBuilder();
			sql.append("select code, name from insurance_class where code in (");
			for (int i = 0; i < insuranceClassCodes.length; i++) {
				sql.append("?");
				if (i < insuranceClassCodes.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");

			LOGGER.info("SQL : [" + sql.toString() + "]");
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				ps = conn.prepareStatement(sql.toString());
				int idx = 1;
				for (int i = 0; i < insuranceClassCodes.length; i++) {
					ps.setString(idx++, (String) insuranceClassCodes[i]);
				}
				rs = ps.executeQuery();
				StringBuilder output = new StringBuilder();
				while (rs.next()) {
					if (output.length() > 0) {
						output.append(", ");
					}
					output.append("(").append(rs.getString(1)).append(")");
					String name = rs.getString(2);
					if (!StringUtils.isBlank(name)) {
						output.append(" ").append(name);
					}
				}
				return output.toString();
			} catch (SQLException e) {
				LOGGER.error("Error in getting Insurance Type info", e);
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException ignore) {
					}
				}

				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException ignore) {
					}
				}
			}
		}
		return null;
	}

	class CompanyDTO {
		private String name;
		private int totalCount;
		private BigDecimal totalClaimedAmount;
		private BigDecimal totalOfferedOrPaidAmount;
		private List<ClaimDetailDTO> details;

		public CompanyDTO() {
			details = new ArrayList<ClaimDetailDTO>();
			for (ClaimStatusEnum status : ClaimStatusEnum.values()) {
				ClaimDetailDTO detail = new ClaimDetailDTO();
				detail.setStatus(status);
				detail.setCount(0);
				detail.setClaimedAmount(BigDecimal.ZERO);
				detail.setOfferedAmount(BigDecimal.ZERO);
				detail.setPaidAmount(BigDecimal.ZERO);
				details.add(detail);
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}

		public BigDecimal getTotalClaimedAmount() {
			return totalClaimedAmount;
		}

		public void setTotalClaimedAmount(BigDecimal totalClaimedAmount) {
			this.totalClaimedAmount = totalClaimedAmount;
		}

		public BigDecimal getTotalOfferedOrPaidAmount() {
			return totalOfferedOrPaidAmount;
		}

		public void setTotalOfferedOrPaidAmount(BigDecimal totalOfferedOrPaidAmount) {
			this.totalOfferedOrPaidAmount = totalOfferedOrPaidAmount;
		}

		public List<ClaimDetailDTO> getDetails() {
			return details;
		}

		public void setDetails(List<ClaimDetailDTO> details) {
			this.details = details;
		}
	}

	class CompanyDepartmentDTO {
		private String name;
		private Long companyId;
		private Long departmentId;
		private String departmentCode;
		private String departmentName;
		private int totalCount;
		private BigDecimal totalClaimedAmount;
		private BigDecimal totalOfferedOrPaidAmount;
		private List<ClaimDetailDTO> details;

		public CompanyDepartmentDTO() {
			details = new ArrayList<ClaimDetailDTO>();
			for (ClaimStatusEnum status : ClaimStatusEnum.values()) {
				ClaimDetailDTO detail = new ClaimDetailDTO();
				detail.setStatus(status);
				detail.setCount(0);
				detail.setClaimedAmount(BigDecimal.ZERO);
				detail.setOfferedAmount(BigDecimal.ZERO);
				detail.setPaidAmount(BigDecimal.ZERO);
				details.add(detail);
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getCompanyId() {
			return companyId;
		}

		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}

		public Long getDepartmentId() {
			return departmentId;
		}

		public void setDepartmentId(Long departmentId) {
			this.departmentId = departmentId;
		}

		public String getDepartmentCode() {
			return departmentCode;
		}

		public void setDepartmentCode(String departmentCode) {
			this.departmentCode = departmentCode;
		}

		public String getDepartmentName() {
			return departmentName;
		}

		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}

		public int getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}

		public BigDecimal getTotalClaimedAmount() {
			return totalClaimedAmount;
		}

		public void setTotalClaimedAmount(BigDecimal totalClaimedAmount) {
			this.totalClaimedAmount = totalClaimedAmount;
		}

		public BigDecimal getTotalOfferedOrPaidAmount() {
			return totalOfferedOrPaidAmount;
		}

		public void setTotalOfferedOrPaidAmount(BigDecimal totalOfferedOrPaidAmount) {
			this.totalOfferedOrPaidAmount = totalOfferedOrPaidAmount;
		}

		public List<ClaimDetailDTO> getDetails() {
			return details;
		}

		public void setDetails(List<ClaimDetailDTO> details) {
			this.details = details;
		}
	}

	class ClaimDetailDTO {
		private ClaimStatusEnum status;
		private BigDecimal claimedAmount;
		private BigDecimal offeredAmount;
		private BigDecimal paidAmount;
		private int count;

		public ClaimStatusEnum getStatus() {
			return status;
		}

		public void setStatus(ClaimStatusEnum status) {
			this.status = status;
		}

		public BigDecimal getClaimedAmount() {
			return claimedAmount;
		}

		public void setClaimedAmount(BigDecimal claimedAmount) {
			this.claimedAmount = claimedAmount;
		}

		public BigDecimal getOfferedAmount() {
			return offeredAmount;
		}

		public void setOfferedAmount(BigDecimal offeredAmount) {
			this.offeredAmount = offeredAmount;
		}

		public BigDecimal getPaidAmount() {
			return paidAmount;
		}

		public void setPaidAmount(BigDecimal paidAmount) {
			this.paidAmount = paidAmount;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
}
