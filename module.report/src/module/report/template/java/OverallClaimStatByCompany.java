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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import module.setup.model.Company;
import module.setup.service.CompanyService;

public class OverallClaimStatByCompany extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverallClaimStatByCompany.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;

	public OverallClaimStatByCompany(ApplicationContext context, String logFile) {
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

		doPrintExcel(getClaimByCompany());
		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);

		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	protected void doPrintExcel(List<CompanyDTO> list) {
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
			cell.setCellValue("Overall Claims Statistics (by Company)");
			cell.setCellStyle(headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), // first
																		// row
																		// (0-based)
					row.getRowNum(), // last row (0-based)
					1, // first column (0-based)
					7 // last column (0-based)
			));

			int startIndex = 6;

			// From-To Loss Date
			Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("dispFromLossDate");
			Timestamp toLossDate = (Timestamp) handler.getParamMap().get("dispToLossDate");

			row = sheet.createRow(startIndex++);
			cell = row.createCell(0);
			cell.setCellValue("From Loss Date :");
			cell = row.createCell(1);
			if (fromLossDate != null) {
				cell.setCellValue(sdf.format(fromLossDate));
			}
			row = sheet.createRow(startIndex++);
			cell = row.createCell(0);
			cell.setCellValue("To Loss Date :");
			cell = row.createCell(1);
			if (toLossDate != null) {
				cell.setCellValue(sdf.format(toLossDate));
			}

			// Insurance Type
			row = sheet.createRow(startIndex++);
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
				row = sheet.createRow(startIndex++);
				cell = row.createCell(0);
				cell.setCellValue("Companies:");
				cell = row.createCell(1);
				cell.setCellValue(companies);
			}

			// Two more empty rows
			startIndex++;
			startIndex++;

			// Creating detail
			for (int i = 0; i < list.size(); i += 2) {

				// 1. Company Name
				row = sheet.createRow(startIndex + (i * 8));

				// Left table
				CompanyDTO left = list.get(i);
				cell = row.createCell(0);
				cell.setCellValue("Company:");
				cell.setCellStyle(tableLabelStyle);
				cell = row.createCell(1);
				cell.setCellValue(left.getName());
				cell.setCellStyle(tableLabelStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));

				// Right table
				if (i + 1 < list.size()) {
					CompanyDTO right = list.get(i + 1);
					cell = row.createCell(5);
					cell.setCellValue("Company:");
					cell.setCellStyle(tableLabelStyle);
					cell = row.createCell(6);
					cell.setCellValue(right.getName());
					cell.setCellStyle(tableLabelStyle);
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 8));
				}

				// 2. Statistics Table Header
				row = sheet.createRow(startIndex + (i * 8) + 2);

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

				// 3. Statictics Table Body
				for (int j = 0; j < left.getDetails().size(); j++) {
					ClaimDetailDTO dto = left.getDetails().get(j);
					row = sheet.createRow(startIndex + (i * 8) + 3 + j);

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
					CompanyDTO right = list.get(i + 1);
					for (int j = 0; j < right.getDetails().size(); j++) {
						ClaimDetailDTO dto = right.getDetails().get(j);
						row = sheet.getRow(startIndex + (i * 8) + 3 + j);

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

				// 4. Statistics Table Footer
				row = sheet.createRow(startIndex + (i * 8) + 3 + left.getDetails().size());

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
					CompanyDTO right = list.get(i + 1);
					row = sheet.getRow(startIndex + (i * 8) + 3 + right.getDetails().size());

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

	protected List<CompanyDTO> getClaimByCompany() {
		LOGGER.info("Counting claim summary for company");
		Map<Long, CompanyDTO> companies = new LinkedHashMap<>();
		// Fill up the company, to avoid company not being shown when it has no
		// claim
		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		CompanyService companyService = context.getBean(CompanyService.class);

		// SIB User
		if (userCompany == null) {
			// Check if the user selected any company
			if (companyIds == null) {
				// Print out all company
				List<Company> dbCompanies = companyService.getAllCompaniesForSIB();
				for (Company dbCompany : dbCompanies) {
					CompanyDTO dto = new CompanyDTO();
					dto.setName(dbCompany.getName());
					companies.put(dbCompany.getId(), dto);
				}
			} else {
				// Print only selected company
				for (Object companyId : companyIds) {
					Company dbCompany = companyService.getCompany((Long) companyId);
					if (dbCompany != null) {
						CompanyDTO dto = new CompanyDTO();
						dto.setName(dbCompany.getName());
						companies.put(dbCompany.getId(), dto);
					}
				}
			}
		} else {
			if (companyIds == null) {
				// Only print all the company which belong to the user
				for (Object uc : userCompany) {
					Company dbCompany = companyService.getCompByCode((String) uc);
					if (dbCompany != null) {
						CompanyDTO dto = new CompanyDTO();
						dto.setName(dbCompany.getName());
						companies.put(dbCompany.getId(), dto);
					}
				}
			} else {
				// Print only selected company
				for (Object companyId : companyIds) {
					Company dbCompany = companyService.getCompany((Long) companyId);
					if (dbCompany != null) {
						CompanyDTO dto = new CompanyDTO();
						dto.setName(dbCompany.getName());
						companies.put(dbCompany.getId(), dto);
					}
				}
			}
		}

		StringBuilder sql = new StringBuilder();
		sql.append(
				"select comp.id, comp.name, count(c.id), sum(c.est_lost_amt), sum(c.offer_amt), sum(c.approval_amt), c.status ");
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
		sql.append("group by comp.id, comp.name, c.status");

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
				for (Object companyId : companyIds) {
					ps.setLong(idx++, (Long) companyId);
				}
			}

			if (userCompany != null && userCompany.length > 0) {
				for (Object uc : userCompany) {
					ps.setString(idx++, (String) uc);
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

			while (rs.next()) {
				Long companyId = rs.getLong(1);
				CompanyDTO companyDTO = companies.get(companyId);
				// Never has the company exists in the map, create a new one
				if (companyDTO == null) {
					companyDTO = new CompanyDTO();
					companies.put(companyId, companyDTO);
				}

				companyDTO.setName(rs.getString(2));
				for (ClaimDetailDTO detail : companyDTO.getDetails()) {
					if (detail.getStatus().name().equals(rs.getString(7))) {
						BigDecimal count = rs.getBigDecimal(3);
						if (count == null) {
							count = BigDecimal.ZERO;
						}
						detail.setCount(count.intValue());

						BigDecimal claimedAmount = rs.getBigDecimal(4);
						if (claimedAmount == null) {
							claimedAmount = BigDecimal.ZERO;
						}
						detail.setClaimedAmount(claimedAmount);

						BigDecimal offeredAmount = rs.getBigDecimal(5);
						if (offeredAmount == null) {
							offeredAmount = BigDecimal.ZERO;
						}
						detail.setOfferedAmount(offeredAmount);

						BigDecimal paidAmount = rs.getBigDecimal(6);
						if (paidAmount == null) {
							paidAmount = BigDecimal.ZERO;
						}
						detail.setPaidAmount(paidAmount);

						companyDTO.setTotalClaimedAmount(
								companyDTO.getTotalClaimedAmount().add(detail.getClaimedAmount()));

						if (ClaimStatusEnum.PPYMT.equals(detail.getStatus())
								|| ClaimStatusEnum.PACC.equals(detail.getStatus())) {
							companyDTO.setTotalOfferedOrPaidAmount(
									companyDTO.getTotalOfferedOrPaidAmount().add(detail.getOfferedAmount()));
						} else {
							companyDTO.setTotalOfferedOrPaidAmount(
									companyDTO.getTotalOfferedOrPaidAmount().add(detail.getPaidAmount()));
						}
						companyDTO.setTotalCount(companyDTO.getTotalCount() + detail.getCount());
					}
				}
			}

			for (CompanyDTO company : companies.values()) {
				LOGGER.info("Company found : [" + company.getName() + "]");
				LOGGER.info("Total Claimed Amount : [" + company.getTotalClaimedAmount() + "]");
				LOGGER.info("Total Offred / Paid Amount : [" + company.getTotalOfferedOrPaidAmount() + "]");
				LOGGER.info("Total Record : [" + company.getTotalCount() + "]");
			}
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
		return new ArrayList<CompanyDTO>(companies.values());
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
		private Long companyId;
		private String name;
		private int totalCount;
		private BigDecimal totalClaimedAmount;
		private BigDecimal totalOfferedOrPaidAmount;
		private List<ClaimDetailDTO> details;

		public CompanyDTO() {
			totalCount = 0;
			totalClaimedAmount = BigDecimal.ZERO;
			totalOfferedOrPaidAmount = BigDecimal.ZERO;

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

		public Long getCompanyId() {
			return companyId;
		}

		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
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
