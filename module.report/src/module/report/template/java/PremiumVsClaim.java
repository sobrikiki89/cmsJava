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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import app.core.logging.LogUtils;
import module.report.ReportModule;
import module.report.generator.DataSourceAware;
import module.report.generator.Generator;
import module.report.handler.ReportParamHandler;
import module.report.model.ReportStatus;
import module.report.model.ReportSubmission;
import module.report.service.ReportSubmissionService;
import module.setup.model.Company;
import module.setup.service.CompanyService;

public class PremiumVsClaim extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(PremiumVsClaim.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;
	private ReportGeneratorJavaStyle style;
	
	public PremiumVsClaim(ApplicationContext context, String logFile) {
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
	
	public ReportGeneratorJavaStyle getStyle() {
		if (style==null) {
			this.style = new ReportGeneratorJavaStyle();
		}
		return style;
	}
	
	public void setStyle(ReportGeneratorJavaStyle style) {
		this.style = style;
	}
	
	@Override
	public void run() {
		LogUtils.addSiftAppender(((ReportModule) context.getBean("ReportModule")).getModuleName());
		LogUtils.startLogging(submission.getFullLogFile());
		LocalDateTime startDate = LocalDateTime.now();
		LOGGER.info("Start date : " + startDate.format(DATETIME_FORMAT));

		List<PolicyDTO> policyDetails = getPolicyDetails();
		List<ClaimDTO> claimDetails = getClaimDetails();
		List<PremiumVsClaimDTO> premiumVsClaimDto = getTableBody(policyDetails, claimDetails);
		doPrintExcel(premiumVsClaimDto);
		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);

		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	private void doPrintExcel(List<PremiumVsClaimDTO> dtoList) {
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		boolean exception = false;
		try {
			wb = new HSSFWorkbook();
			HSSFSheet sheet = columnWidth(wb);
			style = new ReportGeneratorJavaStyle();
			
			HSSFCellStyle headerStyle = style.headerStyleBlock(wb);
			HSSFCellStyle tableHeaderStyle = style.tableHeaderStyleBlock(wb);
			HSSFCellStyle tableBodyStyle = style.tableBodyStyleBlock(wb);
			HSSFCellStyle tableTotalStyle = style.tableTotalStyleBlock(wb);
			
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			
			// print header : report by and report at, title
			printReportDetails(headerStyle, sheet, cell, row);
			
			// print report parameter : 
			int rowIndex = 5;
			Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("dispFromDate");
			Timestamp toLossDate = (Timestamp) handler.getParamMap().get("dispToDate");
			String companies = (String) handler.getParamMap().get("dispCompanyId");
			String insuranceClass = (String) handler.getParamMap().get("dispInsuranceClass");
			if (fromLossDate != null) {
				row = sheet.createRow(rowIndex);
				row.setRowStyle(headerStyle);
				cell = row.createCell(0);
				cell.setCellValue("Start Date : ");
				cell = row.createCell(1);
				cell.setCellValue(sdf.format(fromLossDate));
				rowIndex++;
			}
			if (toLossDate != null) {
				row = sheet.createRow(rowIndex);
				row.setRowStyle(headerStyle);
				cell = row.createCell(0);
				cell.setCellValue("End Date : ");
				cell = row.createCell(1);
				cell.setCellValue(sdf.format(toLossDate));
				rowIndex++;
			}
			if (insuranceClass != null) {
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue("Class of Insurance :");
				cell = row.createCell(1);
				cell.setCellValue(insuranceClass);
				rowIndex++;
			}
			if (companies != null) {
				row = sheet.createRow(rowIndex);
				row.setRowStyle(headerStyle);
				cell = row.createCell(0);
				cell.setCellValue("Companies : ");
				cell = row.createCell(1);
				cell.setCellValue(companies);
				rowIndex++;
			}

			rowIndex++;
						
			// printTable Header
			row = sheet.createRow(rowIndex);
			row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			cell = row.createCell(0);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("COMPANY");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("NO OF POLICIES");
			
			cell = row.createCell(2);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("PREMIUM (RM)");
			
			cell = row.createCell(3);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("NO OF CASES");
			
			cell = row.createCell(4);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("CLAIM INCURRED (RM)");
			
			cell = row.createCell(5);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("LOSS RATIO (%)");
			rowIndex++;
			
			int totalPolicies = 0; BigDecimal totalPremium = BigDecimal.ZERO;
			int totalCases = 0; BigDecimal totalClaimIncurred = BigDecimal.ZERO;
			for (int i = 0; i < dtoList.size(); i++) {
				BigDecimal subTotalPremium = BigDecimal.ZERO;
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellValue(dtoList.get(i).getCompanyCode());
				
				cell = row.createCell(1);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellValue(dtoList.get(i).getPolicyDTO().getPolicyCount());
				totalPolicies = totalPolicies + dtoList.get(i).getPolicyDTO().getPolicyCount();
				
				cell = row.createCell(2);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellType(CellType.NUMERIC);
				if (dtoList.get(i).getPolicyDTO().getTotalGross()!=null && dtoList.get(i).getPolicyDTO().getTotalRebate()!=null) {
					subTotalPremium = dtoList.get(i).getPolicyDTO().getTotalGross().subtract(dtoList.get(i).getPolicyDTO().getTotalRebate());
					cell.setCellValue(subTotalPremium.doubleValue());
					totalPremium = totalPremium.add(subTotalPremium);
					
				} else if (dtoList.get(i).getPolicyDTO().getTotalGross()!=null && dtoList.get(i).getPolicyDTO().getTotalRebate()==null) {
					subTotalPremium = dtoList.get(i).getPolicyDTO().getTotalGross();
					cell.setCellValue(subTotalPremium.doubleValue());
					totalPremium = totalPremium.add(dtoList.get(i).getPolicyDTO().getTotalGross());
					
				} else if (dtoList.get(i).getPolicyDTO().getTotalGross()==null && dtoList.get(i).getPolicyDTO().getTotalRebate()!=null) {
					subTotalPremium = dtoList.get(i).getPolicyDTO().getTotalRebate();
					cell.setCellValue(subTotalPremium.doubleValue());
					totalPremium = totalPremium.add(dtoList.get(i).getPolicyDTO().getTotalRebate());
				}
				
				cell = row.createCell(3);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellValue(dtoList.get(i).getClaimDTO().getClaimCount());
				totalCases = totalCases + dtoList.get(i).getClaimDTO().getClaimCount();
				
				cell = row.createCell(4);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellType(CellType.NUMERIC);
				if (dtoList.get(i).getClaimDTO().getClaimedAmount()!=null) {
					cell.setCellValue(dtoList.get(i).getClaimDTO().getClaimedAmount().doubleValue());
					totalClaimIncurred = totalClaimIncurred.add(dtoList.get(i).getClaimDTO().getClaimedAmount());
				}
				
				cell = row.createCell(5);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellType(CellType.NUMERIC);
				if (dtoList.get(i).getPolicyDTO().getTotalGross()!=null && dtoList.get(i).getClaimDTO().getClaimedAmount()!=null
						&& (dtoList.get(i).getPolicyDTO().getTotalGross().compareTo(BigDecimal.ZERO) > 0
						&& dtoList.get(i).getClaimDTO().getClaimedAmount().compareTo(BigDecimal.ZERO) > 0)) {
					BigDecimal lossRatio = dtoList.get(i).getClaimDTO().getClaimedAmount().divide(subTotalPremium, 2);
					lossRatio = lossRatio.scaleByPowerOfTen(2);
					cell.setCellValue(lossRatio.doubleValue());
				} else {
					cell.setCellValue(0);
				}
				rowIndex++;
			}
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue("TOTAL");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalPolicies);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPremium.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalCases);
			
			cell = row.createCell(4);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClaimIncurred.doubleValue());
			
			cell = row.createCell(5);
			cell.setCellStyle(tableTotalStyle);
			
			fileOut = new FileOutputStream(submission.getFullOutputFile());
			wb.write(fileOut);
			fileOut.close();
			wb.close();
			if (dtoList.size() == 0) {
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

	private void printReportDetails(HSSFCellStyle headerStyle, HSSFSheet sheet, HSSFCell cell, HSSFRow row) {
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
		cell = row.createCell(0);
		cell.setCellValue("Premium vs Claim");
		cell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 0, 6 ));
	}

	/** DATA  ***********************************************************************************/
	private List<ClaimDTO> getClaimDetails() {
		LOGGER.info("Getting All Policy and Endorsement");
		// PARAMETER
		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		CompanyService companyService = context.getBean(CompanyService.class);
		Map<Long, CompanyDTO> companies = new LinkedHashMap<>();

		// SIB User
		if (userCompany == null) {
			// Check if the user selected any company
			if (companyIds == null) {
				// Print out all companyq
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
		sql.append("SELECT ");
			sql.append(" COMPANY.CODE, COUNT(CLAIM.ID) AS CLAIM_CNT, ");
			sql.append(" COALESCE(SUM(COALESCE(CLAIM.APPROVAL_AMT,0.00)) FILTER (WHERE CLAIM.STATUS IN ('CPAID')),0.00) + COALESCE(SUM(COALESCE(CLAIM.RESERVE_AMOUNT,0.00)) FILTER (WHERE CLAIM.STATUS IN ('OPN','PDOC','PADJ','PACC','POFR','PPYMT')),0.00) AS CLAIM_INCURRED, ");
			sql.append(" COALESCE(SUM(COALESCE(CLAIM.APPROVAL_AMT,0.00))  FILTER (WHERE CLAIM.STATUS IN ('CPAID')),0.00) AS PAID_AMT, ");
			sql.append(" COALESCE(SUM(COALESCE(CLAIM.RESERVE_AMOUNT,0.00)) FILTER (WHERE CLAIM.STATUS IN ('OPN','PDOC','PADJ','PACC','POFR','PPYMT')),0.00) AS RESERVE_AMT ");
			sql.append(" FROM CLAIM ");
			sql.append(" INNER JOIN POLICY ON CLAIM.POLICY_ID = POLICY.ID " );
			sql.append(" LEFT JOIN COMPANY ON COMPANY.ID = POLICY.COMPANY_ID ");
		sql.append(" WHERE (CLAIM.DELETED IS NULL OR CLAIM.DELETED = FALSE)");

		Object[] insuranceClassCodes = (Object[]) handler.getParamMap().get("insuranceClass");
		if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
			sql.append(" and policy.insurance_class_code in (");
			for (int i = 0; i < insuranceClassCodes.length; i++) {
				sql.append("?");
				if (i < insuranceClassCodes.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}
		
		if (companyIds != null && companyIds.length > 0) {
			sql.append(" and company.id in (");
			for (int i = 0; i < companyIds.length; i++) {
				sql.append("?");
				if (i < companyIds.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		if (userCompany != null && userCompany.length > 0) {
			sql.append(" and company.code in (");
			for (int i = 0; i < userCompany.length; i++) {
				sql.append("?");
				if (i < userCompany.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}
		
		Timestamp fromDate = (Timestamp) handler.getParamMap().get("fromDate");
		Timestamp toDate = (Timestamp) handler.getParamMap().get("toDate");
		/***
		if (fromDate != null && toDate != null) {
			sql.append(" and policy.start_date between ? and ? ");
		} else if (fromDate != null && toDate == null) {
			sql.append(" AND policy.start_date >= ? ");
		} else if (fromDate == null && toDate != null) {
			sql.append(" AND policy.end_date <= ? ");
		}***/
		
		if (fromDate != null && toDate != null) {
			sql.append(" and claim.loss_date between ? and ? ");
		} else if (fromDate != null && toDate == null) {
			sql.append(" AND claim.loss_date >= ? ");
		} else if (fromDate == null && toDate != null) {
			sql.append(" AND claim.loss_date <= ? ");
		}
		
		sql.append(" GROUP BY COMPANY.CODE, COMPANY.ID ");
		sql.append(" ORDER BY COMPANY.ID ");

		LOGGER.info("SQL : [" + sql.toString() + "]");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<ClaimDTO> dtoList = new ArrayList<ClaimDTO>();
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql.toString());

			int parameterIdx = 1;

			if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
				for (int i = 0; i < insuranceClassCodes.length; i++) {
					ps.setString(parameterIdx++, (String) insuranceClassCodes[i]);
				}
			}
			
			if (companyIds != null && companyIds.length > 0) {
				for (Object companyId : companyIds) {
					ps.setLong(parameterIdx++, (Long) companyId);
				}
			}

			if (userCompany != null && userCompany.length > 0) {
				for (Object uc : userCompany) {
					ps.setString(parameterIdx++, (String) uc);
				}
			}
			if (fromDate != null && toDate != null) {
				ps.setTimestamp(parameterIdx++, fromDate);
				ps.setTimestamp(parameterIdx++, toDate);
			} else if (fromDate != null && toDate == null) {
				ps.setTimestamp(parameterIdx++, fromDate);
			} else if (fromDate == null && toDate != null) {
				ps.setTimestamp(parameterIdx++, toDate);
			}
			/***
			if (fromDate != null && toDate != null) {
				ps.setTimestamp(parameterIdx++, fromDate);
				ps.setTimestamp(parameterIdx++, toDate);
			} else if (fromDate != null && toDate == null) {
				ps.setTimestamp(parameterIdx++, fromDate);
			} else if (fromDate == null && toDate != null) {
				ps.setTimestamp(parameterIdx++, toDate);
			}***/
			
			rs = ps.executeQuery();
			while(rs.next()) {
				ClaimDTO dto = new ClaimDTO();
				dto.setCompanyCode(rs.getString(1));
				dto.setClaimCount(rs.getInt(2));
				dto.setClaimedAmount(rs.getBigDecimal(3));
				dto.setPaidAmount(rs.getBigDecimal(4));
				dto.setReserveAmount(rs.getBigDecimal(5));
				dtoList.add(dto);
			}
		} catch (SQLException e) {
			LOGGER.error("Error in getting policy info for company", e);
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
		return dtoList;
	}

	private List<PolicyDTO> getPolicyDetails() {
		LOGGER.info("Getting All Policy and Endorsement");
		// PARAMETER
		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		CompanyService companyService = context.getBean(CompanyService.class);
		Map<Long, CompanyDTO> companies = new LinkedHashMap<>();

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
		sql.append("SELECT ");
			sql.append(" COMPANY.CODE, COUNT(POLICY.ID) AS POLICY_CNT, "); 
			sql.append(" SUM(COALESCE(PREMIUM_GROSS,0.00)) + SUM(COALESCE(GROSS_PREMIUM,0.00)) AS TOTAL_GROSS, ");
			sql.append(" SUM(COALESCE(PREMIUM_REBATE,0.00)) + SUM(COALESCE(REBATE_PREMIUM,0.00)) AS TOTAL_REBATE ");
		sql.append(" FROM POLICY ");
		sql.append(" LEFT JOIN (SELECT POLICY_ID, SUM(GROSS_PREMIUM) AS GROSS_PREMIUM, SUM(REBATE_PREMIUM) AS REBATE_PREMIUM ");
						sql.append(" FROM POLICY_ENDORSEMENT GROUP BY POLICY_ID) ENDORSEMENT ");
		sql.append(" ON POLICY.ID=ENDORSEMENT.POLICY_ID" ); 
		sql.append(" INNER JOIN COMPANY ON COMPANY.ID = POLICY.COMPANY_ID ");
		sql.append(" WHERE 1 = 1 ");

		Object[] insuranceClassCodes = (Object[]) handler.getParamMap().get("insuranceClass");
		if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
			sql.append(" and policy.insurance_class_code in (");
			for (int i = 0; i < insuranceClassCodes.length; i++) {
				sql.append("?");
				if (i < insuranceClassCodes.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}
		
		if (companyIds != null && companyIds.length > 0) {
			sql.append(" and company.id in (");
			for (int i = 0; i < companyIds.length; i++) {
				sql.append("?");
				if (i < companyIds.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		if (userCompany != null && userCompany.length > 0) {
			sql.append(" and company.code in (");
			for (int i = 0; i < userCompany.length; i++) {
				sql.append("?");
				if (i < userCompany.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}
		
		Timestamp fromDate = (Timestamp) handler.getParamMap().get("fromDate");
		Timestamp toDate = (Timestamp) handler.getParamMap().get("toDate");
		if (fromDate != null && toDate != null) {
			sql.append(" AND policy.start_date between ? AND ? ");
		} else if (fromDate != null && toDate == null) {
			sql.append(" AND policy.start_date >= ? ");
		} else if (fromDate == null && toDate != null) {
			sql.append(" AND policy.end_date <= ? ");
		}
		
		sql.append(" GROUP BY COMPANY.CODE, COMPANY.ID ");
		sql.append(" ORDER BY COMPANY.ID ");

		LOGGER.info("SQL : [" + sql.toString() + "]");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<PolicyDTO> dtoList = new ArrayList<PolicyDTO>();
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql.toString());

			int parameterIdx = 1;

			if (insuranceClassCodes != null && insuranceClassCodes.length > 0) {
				for (int i = 0; i < insuranceClassCodes.length; i++) {
					ps.setString(parameterIdx++, (String) insuranceClassCodes[i]);
				}
			}
			
			if (companyIds != null && companyIds.length > 0) {
				for (Object companyId : companyIds) {
					ps.setLong(parameterIdx++, (Long) companyId);
				}
			}

			if (userCompany != null && userCompany.length > 0) {
				for (Object uc : userCompany) {
					ps.setString(parameterIdx++, (String) uc);
				}
			}
			if (fromDate != null && toDate != null) {
				ps.setTimestamp(parameterIdx++, fromDate);
				ps.setTimestamp(parameterIdx++, toDate);
			} else if (fromDate != null && toDate == null) {
				ps.setTimestamp(parameterIdx++, fromDate);
			} else if (fromDate == null && toDate != null) {
				ps.setTimestamp(parameterIdx++, toDate);
			}
			
			rs = ps.executeQuery();
			while(rs.next()) {
				PolicyDTO dto = new PolicyDTO();
				dto.setCompanyCode(rs.getString(1));
				dto.setPolicyCount(rs.getInt(2));
				dto.setTotalGross(rs.getBigDecimal(3));
				dto.setTotalRebate(rs.getBigDecimal(4));
				dtoList.add(dto);
			}
		} catch (SQLException e) {
			LOGGER.error("Error in getting policy info for company", e);
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
		return dtoList;
	}
	
	private List<PremiumVsClaimDTO> getTableBody(List<PolicyDTO> policyDetails, List<ClaimDTO> claimDetails) {
		CompanyService companyService = context.getBean(CompanyService.class);
		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		List<CompanyDTO> companies = new ArrayList<CompanyDTO>();
		// SIB User
		if (userCompany == null) {
			// Check if the user selected any company
			if (companyIds == null) {
				// Print out all company
				List<Company> dbCompanies = companyService.getAllCompaniesForSIB();
				for (Company dbCompany : dbCompanies) {
					CompanyDTO dto = new CompanyDTO();
					dto.setCode(dbCompany.getCode());
					dto.setName(dbCompany.getName());
					companies.add(dto);
				}
			} else {
				// Print only selected company
				for (Object companyId : companyIds) {
					Company dbCompany = companyService.getCompany((Long) companyId);
					if (dbCompany != null) {
						CompanyDTO dto = new CompanyDTO();
						dto.setCode(dbCompany.getCode());
						dto.setName(dbCompany.getName());
						companies.add(dto);
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
						dto.setCode(dbCompany.getCode());
						dto.setName(dbCompany.getName());
						companies.add(dto);
					}
				}
			} else {
				// Print only selected company
				for (Object companyId : companyIds) {
					Company dbCompany = companyService.getCompany((Long) companyId);
					if (dbCompany != null) {
						CompanyDTO dto = new CompanyDTO();
						dto.setCode(dbCompany.getCode());
						dto.setName(dbCompany.getName());
						companies.add(dto);
					}
				}
			}
		}
		
		Map<String, PolicyDTO> policyMap = new HashMap<String, PolicyDTO>();
		for (PolicyDTO policyDto : policyDetails) {
			policyMap.put(policyDto.getCompanyCode(), policyDto);
		}
		
		Map<String, ClaimDTO> claimMap = new HashMap<String, ClaimDTO>();
		for (ClaimDTO claimDto : claimDetails) {
			claimMap.put(claimDto.getCompanyCode(), claimDto);
		}
		
		List<PremiumVsClaimDTO> dtoList = new ArrayList<PremiumVsClaimDTO>();
		for (CompanyDTO comp : companies) {
			PremiumVsClaimDTO dto = new PremiumVsClaimDTO();
			dto.setCompanyCode(comp.getCode());
			if (policyMap.containsKey(comp.getCode())) {
				PolicyDTO policy = policyMap.get(comp.getCode());
				dto.setPolicyDTO(policy);
			}
			if (claimMap.containsKey(comp.getCode())) {
				ClaimDTO claim = claimMap.get(comp.getCode());
				dto.setClaimDTO(claim);
			}
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	/** WIDTH ***********************************************************************************/
	private HSSFSheet columnWidth(HSSFWorkbook wb) {
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, 4000); // CODE
		sheet.setColumnWidth(1, 4000); // NO OF POLICY
		sheet.setColumnWidth(2, 6500); // PREMIUM
		sheet.setColumnWidth(3, 4000); // NO OF CASES
		sheet.setColumnWidth(4, 6500); // CLAIM INCURRED
		sheet.setColumnWidth(5, 4000); // LOSS RATIO
		return sheet;
	}
	
	/** CLASS ***********************************************************************************/
	class CompanyDTO {
		private Long companyId;
		private String code;
		private String name;
		
		public Long getCompanyId() {
			return companyId;
		}
		
		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
	
	
	class PremiumVsClaimDTO {
		private String companyCode;
		private Long companyId;
		private PolicyDTO policyDTO;
		private ClaimDTO claimDTO;
		
		public PremiumVsClaimDTO() {
			policyDTO = new PolicyDTO();
			policyDTO.setPolicyCount(0);
			policyDTO.setEndorsementGross(BigDecimal.ZERO);
			policyDTO.setPolicyGross(BigDecimal.ZERO);
			policyDTO.setTotalGross(BigDecimal.ZERO);
			
			claimDTO = new ClaimDTO();
			claimDTO.setClaimCount(0);
			claimDTO.setReserveAmount(BigDecimal.ZERO);
			claimDTO.setPaidAmount(BigDecimal.ZERO);
			claimDTO.setClaimedAmount(BigDecimal.ZERO);
		}
		
		public String getCompanyCode() {
			return companyCode;
		}
		
		public void setCompanyCode(String companyCode) {
			this.companyCode = companyCode;
		}
		
		public Long getCompanyId() {
			return companyId;
		}
		
		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}
		
		public PolicyDTO getPolicyDTO() {
			return policyDTO;
		}
		
		public void setPolicyDTO(PolicyDTO policyDTO) {
			this.policyDTO = policyDTO;
		}
		
		public ClaimDTO getClaimDTO() {
			return claimDTO;
		}
		
		public void setClaimDTO(ClaimDTO claimDTO) {
			this.claimDTO = claimDTO;
		}
	}
	
	class PolicyDTO {
		private String companyCode;
		private int policyCount;
		private BigDecimal policyGross;
		private BigDecimal endorsementGross;
		private BigDecimal totalGross;
		private BigDecimal policyRebate;
		private BigDecimal endorsementRebate;
		private BigDecimal totalRebate;
				
		public String getCompanyCode() {
			return companyCode;
		}
		public void setCompanyCode(String companyCode) {
			this.companyCode = companyCode;
		}
		public int getPolicyCount() {
			return policyCount;
		}
		public void setPolicyCount(int policyCount) {
			this.policyCount = policyCount;
		}
		public BigDecimal getPolicyGross() {
			return policyGross;
		}
		public void setPolicyGross(BigDecimal policyGross) {
			this.policyGross = policyGross;
		}
		public BigDecimal getEndorsementGross() {
			return endorsementGross;
		}
		public void setEndorsementGross(BigDecimal endorsementGross) {
			this.endorsementGross = endorsementGross;
		}
		public BigDecimal getTotalGross() {
			return totalGross;
		}
		public void setTotalGross(BigDecimal totalGross) {
			this.totalGross = totalGross;
		}
		public BigDecimal getPolicyRebate() {
			return policyRebate;
		}
		public void setPolicyRebate(BigDecimal policyRebate) {
			this.policyRebate = policyRebate;
		}
		public BigDecimal getEndorsementRebate() {
			return endorsementRebate;
		}
		public void setEndorsementRebate(BigDecimal endorsementRebate) {
			this.endorsementRebate = endorsementRebate;
		}
		public BigDecimal getTotalRebate() {
			return totalRebate;
		}
		public void setTotalRebate(BigDecimal totalRebate) {
			this.totalRebate = totalRebate;
		}
		
	}
	
	class ClaimDTO {
		private String companyCode;
		private int claimCount;
		private BigDecimal reserveAmount;
		private BigDecimal paidAmount;
		private BigDecimal claimedAmount;
		
		public String getCompanyCode() {
			return companyCode;
		}

		public void setCompanyCode(String companyCode) {
			this.companyCode = companyCode;
		}
				
		public int getClaimCount() {
			return claimCount;
		}

		public void setClaimCount(int claimCount) {
			this.claimCount = claimCount;
		}

		public BigDecimal getReserveAmount() {
			return reserveAmount;
		}
		
		public void setReserveAmount(BigDecimal reserveAmount) {
			this.reserveAmount = reserveAmount;
		}
		
		public BigDecimal getPaidAmount() {
			return paidAmount;
		}
		
		public void setPaidAmount(BigDecimal paidAmount) {
			this.paidAmount = paidAmount;
		}
		
		public BigDecimal getClaimedAmount() {
			return claimedAmount;
		}
		
		public void setClaimedAmount(BigDecimal claimedAmount) {
			this.claimedAmount = claimedAmount;
		}
	}
}
