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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
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

public class ClaimSummary extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimSummary.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	private static DecimalFormat df = new DecimalFormat("0.#");

	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;

	public ClaimSummary(ApplicationContext context, String logFile) {
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

		doPrintExcel(getTableBody());
		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);

		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	private void doPrintExcel(List<CompanyDTO> summaryList) {
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		boolean exception = false;
		try {
			wb = new HSSFWorkbook();
			HSSFSheet sheet = columnWidth(wb);

			HSSFCellStyle headerStyle = headerStyleBlock(wb);
			HSSFCellStyle subHeaderStyle = subHeaderStyleBlock(wb);
			HSSFCellStyle tableHeaderStyle = tableHeaderStyleBlock(wb);
			HSSFCellStyle tableSubHeaderStyle = tableSubHeaderStyleBlock(wb);
			HSSFCellStyle tableBodyStyle = tableBodyStyleBlock(wb);
			HSSFCellStyle tableTotalStyle = tableTotalStyleBlock(wb);
			HSSFCellStyle tableGrandTotalStyle = tableGrandTotalStyleBlock(wb);

			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);

			// print header : report by and report at, title
			printReportDetails(headerStyle, sheet, cell, row);
			
			// print report parameter : 
			int rowIndex = 5;
			Timestamp fromDate = (Timestamp) handler.getParamMap().get("dispFromDate");
			Timestamp toDate = (Timestamp) handler.getParamMap().get("dispToDate");
			String companies = (String) handler.getParamMap().get("dispCompanies");
			String insuranceClass = (String) handler.getParamMap().get("dispInsuranceClass");
			
			if (fromDate != null) {
				row = sheet.createRow(rowIndex);
				row.setRowStyle(headerStyle);
				cell = row.createCell(0);
				cell.setCellValue("Start Date : ");
				cell = row.createCell(1);
				cell.setCellValue(sdf.format(fromDate));
				rowIndex++;
			}
			if (toDate != null) {
				row = sheet.createRow(rowIndex);
				row.setRowStyle(headerStyle);
				cell = row.createCell(0);
				cell.setCellValue("End Date : ");
				cell = row.createCell(1);
				cell.setCellValue(sdf.format(toDate));
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

			int totalOpenCount = 0; int totalPendingDocumentCount = 0;
			int totalPendingAdjusterCount = 0; int totalPendingAcceptanceCount = 0; int totalPendingOfferCount = 0;
			int totalPendingPaymentCount = 0; int totalClosedPaidCount = 0; int totalClosedWithinExcessCount = 0;
			int totalClosedDeclinedCount = 0; int totalClosedWithdrawnCount = 0;
			int totalOngoingClaimCount = 0; int totalResolvedCount = 0;
			int totalAllCount = 0;
			
			BigDecimal totalOpenLossAmt = BigDecimal.ZERO; BigDecimal totalPendingDocumentLossAmt = BigDecimal.ZERO;
			BigDecimal totalPendingAdjusterLossAmt = BigDecimal.ZERO; BigDecimal totalPendingAcceptanceLossAmt = BigDecimal.ZERO;
			BigDecimal totalPendingOfferLossAmt = BigDecimal.ZERO; BigDecimal totalPendingPaymentLossAmt  = BigDecimal.ZERO;
			BigDecimal totalClosedPaidLossAmt = BigDecimal.ZERO; BigDecimal totalClosedWithinExcessLossAmt = BigDecimal.ZERO;
			BigDecimal totalClosedDeclinedLossAmt = BigDecimal.ZERO; BigDecimal totalClosedWithdrawnLossAmt = BigDecimal.ZERO;
			BigDecimal totalOngoingClaimLossAmt  = BigDecimal.ZERO; BigDecimal totalResolvedLossAmt = BigDecimal.ZERO;
			BigDecimal totalLossAmt = BigDecimal.ZERO;
			
			BigDecimal totalOpenPaidAmt = BigDecimal.ZERO; BigDecimal totalPendingDocumentPaidAmt = BigDecimal.ZERO;
			BigDecimal totalPendingAdjusterPaidAmt = BigDecimal.ZERO;
			BigDecimal totalPendingAcceptancePaidAmt = BigDecimal.ZERO; BigDecimal totalPendingOfferPaidAmt = BigDecimal.ZERO;
			BigDecimal totalPendingPaymentPaidAmt = BigDecimal.ZERO; BigDecimal totalClosedPaidPaidAmt = BigDecimal.ZERO;
			BigDecimal totalClosedWithinExcessPaidAmt = BigDecimal.ZERO; BigDecimal totalClosedDeclinedPaidAmt = BigDecimal.ZERO;
			BigDecimal totalClosedWithdrawnPaidAmt = BigDecimal.ZERO;
			BigDecimal totalOngoingClaimPaidAmt = BigDecimal.ZERO; BigDecimal totalResolvedPaidAmt = BigDecimal.ZERO;
			BigDecimal totalClaimPaid = BigDecimal.ZERO;
			
			for (int i = 0; i < summaryList.size(); i++) {
				String statusGroup = "";
				CompanyDTO dto = summaryList.get(i);

				// print report table header
				row = sheet.createRow(rowIndex);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
				cell = row.createCell(0);
				cell.setCellStyle(subHeaderStyle);
				cell.setCellValue("COMPANY: " + dto.getName());
				rowIndex++;
				
				row = sheet.createRow(rowIndex);
				row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
				cell = row.createCell(0);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("CLAIM STATUS");
				
				cell = row.createCell(1);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("NO OF CASES");
				
				cell = row.createCell(2);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("ESTIMATED LOSS AMOUNT (RM)");
				
				cell = row.createCell(3);
				cell.setCellStyle(tableHeaderStyle);
				cell.setCellValue("OFFERED / PAID AMOUNT (RM)");
				rowIndex++;
				
				int subTotalCount = 0; 
				BigDecimal subTotalPaid = BigDecimal.ZERO;
				BigDecimal subTotalEstLoss = BigDecimal.ZERO;
				BigDecimal totalPaid = BigDecimal.ZERO;
				BigDecimal totalEstLoss = BigDecimal.ZERO;
				int totalOnGoingCount = 0;
				int totalResolveCount = 0;
				
				for (int j = 0; j < dto.getDetails().size(); j++) {
					
					ClaimDetailDTO detail = dto.getDetails().get(j);
					
					ClaimStatusEnum stat = detail.getStatus();
					switch (stat.name()) {
					case "OPN":
						totalOpenCount = totalOpenCount + detail.getCount();
						totalOpenLossAmt = totalOpenLossAmt.add(detail.getClaimedAmount());
						totalOpenPaidAmt = totalOpenPaidAmt.add(detail.getPaidAmount());
						break;
					case "PDOC":
						totalPendingDocumentCount = totalPendingDocumentCount + detail.getCount();
						totalPendingDocumentLossAmt = totalPendingDocumentLossAmt.add(detail.getClaimedAmount());
						totalPendingDocumentPaidAmt = totalPendingDocumentPaidAmt.add(detail.getPaidAmount());
						break;
					case "PADJ":
						totalPendingAdjusterCount = totalPendingAdjusterCount + detail.getCount();
						totalPendingAdjusterLossAmt = totalPendingAdjusterLossAmt.add(detail.getClaimedAmount());
						totalPendingAdjusterPaidAmt = totalPendingAdjusterPaidAmt.add(detail.getPaidAmount());
						break;
					case "PACC":
						totalPendingAcceptanceCount = totalPendingAcceptanceCount + detail.getCount();
						totalPendingAcceptanceLossAmt = totalPendingAcceptanceLossAmt.add(detail.getClaimedAmount());
						totalPendingAcceptancePaidAmt = totalPendingAcceptancePaidAmt.add(detail.getOfferedAmount());
						break;
					case "POFR":
						totalPendingOfferCount = totalPendingOfferCount + detail.getCount();
						totalPendingOfferLossAmt = totalPendingOfferLossAmt.add(detail.getClaimedAmount());
						totalPendingOfferPaidAmt = totalPendingOfferPaidAmt.add(detail.getPaidAmount());
						break;
					case "PPYMT":
						totalPendingPaymentCount = totalPendingPaymentCount + detail.getCount();
						totalPendingPaymentLossAmt = totalPendingPaymentLossAmt.add(detail.getClaimedAmount());
						totalPendingPaymentPaidAmt = totalPendingPaymentPaidAmt.add(detail.getOfferedAmount());
						break;
					case "CPAID":
						totalClosedPaidCount = totalClosedPaidCount + detail.getCount();
						totalClosedPaidLossAmt = totalClosedPaidLossAmt.add(detail.getClaimedAmount());
						totalClosedPaidPaidAmt = totalClosedPaidPaidAmt.add(detail.getPaidAmount());
						break;
					case "CUEX":
						totalClosedWithinExcessCount = totalClosedWithinExcessCount + detail.getCount();
						totalClosedWithinExcessLossAmt = totalClosedWithinExcessLossAmt.add(detail.getClaimedAmount());
						totalClosedWithinExcessPaidAmt = totalClosedWithinExcessPaidAmt.add(detail.getPaidAmount());
						break;
					case "CDCL":
						totalClosedDeclinedCount = totalClosedDeclinedCount + detail.getCount();
						totalClosedDeclinedLossAmt = totalClosedDeclinedLossAmt.add(detail.getClaimedAmount());
						totalClosedDeclinedPaidAmt = totalClosedDeclinedPaidAmt.add(detail.getPaidAmount());
						break;
					case "CWDTH":
						totalClosedWithdrawnCount = totalClosedWithdrawnCount + detail.getCount();
						totalClosedWithdrawnLossAmt = totalClosedWithdrawnLossAmt.add(detail.getClaimedAmount());
						totalClosedWithdrawnPaidAmt = totalClosedWithdrawnPaidAmt.add(detail.getPaidAmount());
						break;
					default:
						break;
					}
					
					if (detail.getStatusGroup()!=null 
							&& !statusGroup.equals(detail.getStatusGroup())) {						
						if (!"".equals(statusGroup)) {
							row = sheet.createRow(rowIndex);
							cell = row.createCell(0);
							cell.setCellStyle(tableTotalStyle);
							cell.setCellType(CellType.STRING);
							cell.setCellValue(statusGroup + " SUB TOTAL");
							
							cell = row.createCell(1);
							cell.setCellStyle(tableTotalStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(subTotalCount);
							
							cell = row.createCell(2);
							cell.setCellStyle(tableTotalStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(subTotalPaid.doubleValue());
							
							cell = row.createCell(3);
							cell.setCellStyle(tableTotalStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(subTotalEstLoss.doubleValue());
							rowIndex++;
							
							if ("ON-GOING".equals(statusGroup)) {
								totalOnGoingCount = subTotalCount; 
								totalPaid = totalPaid.add(subTotalPaid);
								totalEstLoss = totalEstLoss.add(subTotalEstLoss);
								totalOngoingClaimCount = totalOngoingClaimCount + subTotalCount;
								totalOngoingClaimLossAmt = totalOngoingClaimLossAmt.add(totalEstLoss);
								totalOngoingClaimPaidAmt = totalOngoingClaimPaidAmt.add(totalPaid);
							}
						}
						
						row = sheet.createRow(rowIndex);
						cell = row.createCell(0);
						cell.setCellStyle(tableSubHeaderStyle);
						cell.setCellValue(detail.getStatusGroup());
						cell = row.createCell(3);
						cell.setCellStyle(tableSubHeaderStyle);
						sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
						rowIndex++;
						
						subTotalCount = 0; 
						subTotalPaid = BigDecimal.ZERO;
						subTotalEstLoss = BigDecimal.ZERO;
					}
					
					row = sheet.createRow(rowIndex);
					cell = row.createCell(0);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(detail.getStatus().getLabel());
					
					cell = row.createCell(1);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(detail.getCount());
					subTotalCount = subTotalCount + detail.getCount();
					
					cell = row.createCell(2);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(detail.getClaimedAmount().doubleValue()); 
					subTotalPaid = subTotalPaid.add(detail.getClaimedAmount());
					
					cell = row.createCell(3);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellType(CellType.NUMERIC);
					if (ClaimStatusEnum.PPYMT.equals(detail.getStatus()) || ClaimStatusEnum.PACC.equals(detail.getStatus())) {
						cell.setCellValue(detail.getOfferedAmount().doubleValue());
						subTotalEstLoss = subTotalEstLoss.add(detail.getOfferedAmount());
					} else {
						cell.setCellValue(detail.getPaidAmount().doubleValue());
						subTotalEstLoss = subTotalEstLoss.add(detail.getPaidAmount());
					}
					
					statusGroup = detail.getStatusGroup();
					rowIndex++;
				}
				
				if ("RESOLVED".equals(statusGroup)) {
					totalResolveCount = subTotalCount;
					totalResolvedCount = totalResolvedCount + subTotalCount;
					totalResolvedLossAmt = totalResolvedLossAmt.add(subTotalEstLoss);
					totalResolvedPaidAmt = totalResolvedPaidAmt.add(subTotalPaid);
				}
				
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(statusGroup + " SUB TOTAL");
				
				cell = row.createCell(1);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(subTotalCount);
				
				cell = row.createCell(2);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(subTotalPaid.doubleValue());
				
				cell = row.createCell(3);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(subTotalEstLoss.doubleValue());
				rowIndex++;
				
				double totalCount = dto.getTotalCount();
				totalPaid = totalPaid.add(subTotalPaid);
				totalEstLoss = totalEstLoss.add(subTotalEstLoss);
				
				// Calculate all
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("TOTAL ");
				
				cell = row.createCell(1);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(totalCount);
				
				cell = row.createCell(2);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(totalPaid.doubleValue());
				
				cell = row.createCell(3);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(totalEstLoss.doubleValue());
				rowIndex++;
				
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(" % ON-GOING ");
				
				cell = row.createCell(1);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				double percentOngoing = (totalOnGoingCount  / totalCount)*100;
				cell.setCellValue(df.format(percentOngoing));
				cell = row.createCell(2);
				cell.setCellStyle(tableSubHeaderStyle);
				cell = row.createCell(3);
				cell.setCellStyle(tableSubHeaderStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
				cell.setCellStyle(tableSubHeaderStyle);
				rowIndex++;
				
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(" % RESOLVED ");
				
				cell = row.createCell(1);
				cell.setCellStyle(tableTotalStyle);
				cell.setCellType(CellType.NUMERIC);
				double percentResolve = (totalResolveCount / totalCount)*100;
				cell.setCellValue(df.format(percentResolve));
				cell = row.createCell(2);
				cell.setCellStyle(tableSubHeaderStyle);
				cell = row.createCell(3);
				cell.setCellStyle(tableSubHeaderStyle);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
				rowIndex++;
				
				rowIndex++;
			}
		
			// print report total table 
			
			row = sheet.createRow(rowIndex);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
			cell = row.createCell(0);
			cell.setCellStyle(subHeaderStyle);
			cell.setCellValue("TOTAL TABLE");
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			cell = row.createCell(0);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("CLAIM STATUS");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("NO OF CASES");
			
			cell = row.createCell(2);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("ESTIMATED LOSS AMOUNT (RM)");
			
			cell = row.createCell(3);
			cell.setCellStyle(tableHeaderStyle);
			cell.setCellValue("OFFERED / PAID AMOUNT (RM)");
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableSubHeaderStyle);
			cell.setCellValue("ON-GOING");
			cell = row.createCell(3);
			cell.setCellStyle(tableSubHeaderStyle);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Open");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalOpenCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalOpenLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalOpenPaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Pending Document");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingDocumentCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingDocumentLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingDocumentPaidAmt.doubleValue());
			rowIndex++;

			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Pending Adjuster");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingAdjusterCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingAdjusterLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingAdjusterPaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Pending Acceptance");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingAcceptanceCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingAcceptanceLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingAcceptancePaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Pending Offer");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingOfferCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingOfferLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingOfferPaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Pending Payment");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingPaymentCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingPaymentLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalPendingPaymentPaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue("ON-GOING SUB TOTAL");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalOngoingClaimCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalOngoingClaimPaidAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalOngoingClaimLossAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableSubHeaderStyle);
			cell.setCellValue("RESOLVED");
			cell = row.createCell(3);
			cell.setCellStyle(tableSubHeaderStyle);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Closed - Paid");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedPaidCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedPaidLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedPaidPaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Closed - Within Policy Excess");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedWithinExcessCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedWithinExcessLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedWithinExcessPaidAmt.doubleValue());
			rowIndex++;

			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Closed - Declined");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedDeclinedCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedDeclinedLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedDeclinedPaidAmt.doubleValue());
			rowIndex++;

			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellValue("Closed - Withdrawn");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedWithdrawnCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedWithdrawnLossAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableBodyStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClosedWithdrawnPaidAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue("RESOLVED SUB TOTAL");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalResolvedCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalResolvedPaidAmt.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalResolvedLossAmt.doubleValue());
			rowIndex++;
			
			// CALCULATE GRAND TOTAL			
			totalAllCount = totalOngoingClaimCount + totalResolvedCount;
			totalLossAmt = totalOngoingClaimPaidAmt.add(totalResolvedPaidAmt);
			totalClaimPaid = totalOngoingClaimLossAmt.add(totalResolvedLossAmt);
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellValue("GRAND TOTAL");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalAllCount);
			
			cell = row.createCell(2);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalClaimPaid.doubleValue());
			
			cell = row.createCell(3);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(totalLossAmt.doubleValue());
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(" % ON-GOING ");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			double percentOngoing = ((double) totalOngoingClaimCount / (double) totalAllCount) * 100;
			cell.setCellValue(df.format(percentOngoing));
			cell = row.createCell(2);
			cell.setCellStyle(tableGrandTotalStyle);
			cell = row.createCell(3);
			cell.setCellStyle(tableGrandTotalStyle);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
			cell.setCellStyle(tableGrandTotalStyle);
			rowIndex++;
			
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(" % RESOLVED ");
			
			cell = row.createCell(1);
			cell.setCellStyle(tableGrandTotalStyle);
			cell.setCellType(CellType.NUMERIC);
			double percentResolve = ((double) totalResolvedCount / (double) totalAllCount) * 100;
			cell.setCellValue(df.format(percentResolve));
			cell = row.createCell(2);
			cell.setCellStyle(tableGrandTotalStyle);
			cell = row.createCell(3);
			cell.setCellStyle(tableGrandTotalStyle);
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
			rowIndex++;
			
			fileOut = new FileOutputStream(submission.getFullOutputFile());
			wb.write(fileOut);
			fileOut.close();
			wb.close();
			if (summaryList.size() == 0) {
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
		cell.setCellValue("CLAIM SUMMARY");
		cell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 4));
	}

	/*** DATA ***********************************************************************************/
	private List<CompanyDTO> getTableBody() {
		LOGGER.info("Getting All Claim");
		// PARAMETER
		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		Object[] companyIds = (Object[]) handler.getParamMap().get("companies");
		String groupOfInsuranceCode = (String) handler.getParamMap().get("groupOfInsuranceCode");
		Object[] insuranceClassCode = (Object[]) handler.getParamMap().get("insuranceClassCode");
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
		sql.append(
				"select comp.id, comp.name, count(c.id), sum(c.est_lost_amt), ");
		sql.append("sum(c.offer_amt), sum(c.approval_amt), c.status, ");
		sql.append("case when c.status in ('OPN','PDOC','PADJ','PACC','POCC','POFR','PPYMT') then 'ON-GOING' ");
		sql.append("when c.status in ('CPAID','CUEX','CDCL','CWDTH') then 'RESOLVED'  else 'ERROR' end as summaryStatus ");
		sql.append("from claim c ");
			sql.append("inner join policy p on c.policy_id = p.id ");
			sql.append("inner join insurance_class ic on ic.code = p.insurance_class_code ");
			sql.append("inner join insurance_class_category icc on icc.code = ic.category ");
			sql.append("left join company comp on comp.id = p.company_id ");
		sql.append("where (c.deleted IS NULL OR c.deleted = false) and comp.code != 'SIB' ");

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
		
		if (groupOfInsuranceCode!=null && !groupOfInsuranceCode.isEmpty()) {
			sql.append(" and icc.code = ? ");
		}
		
		if (insuranceClassCode != null && insuranceClassCode.length > 0) {
			sql.append(" and ic.code in (");
			for (int i = 0; i < insuranceClassCode.length; i++) {
				sql.append("?");
				if (i < insuranceClassCode.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") ");
		}

		Timestamp fromDate = (Timestamp) handler.getParamMap().get("fromLossDate");
		Timestamp toDate = (Timestamp) handler.getParamMap().get("toLossDate");
		if (fromDate != null && toDate != null) {
			sql.append(" and c.loss_date between ? and ? ");
		} else if (fromDate != null && toDate == null) {
			sql.append(" and c.loss_date >= ? ");
		} else if (fromDate == null && toDate != null) {
			sql.append(" and c.loss_date <= ? ");
		}
		sql.append("group by comp.id, comp.name, c.status ");
		sql.append("order by comp.id, summaryStatus, status ");

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

			if (groupOfInsuranceCode!=null && !groupOfInsuranceCode.isEmpty()) {
				ps.setString(idx++, (String) groupOfInsuranceCode);
			}
			
			if (insuranceClassCode != null && insuranceClassCode.length > 0) {
				for (Object ic : insuranceClassCode) {
					ps.setString(idx++, (String) ic);
				}
			}
			
			if (fromDate != null && toDate != null) {
				ps.setTimestamp(idx++, fromDate);
				ps.setTimestamp(idx++, toDate);
			} else if (fromDate != null && toDate == null) {
				ps.setTimestamp(idx++, fromDate);
			} else if (fromDate == null && toDate != null) {
				ps.setTimestamp(idx++, toDate);
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
	

	/*** STYLE ***********************************************************************************/
	private HSSFSheet columnWidth(HSSFWorkbook wb) {
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, 7500); // CLAIM STATUS
		sheet.setColumnWidth(1, 4000); // CLAIM COUNT
		sheet.setColumnWidth(2, 6500); // PAID AMT
		sheet.setColumnWidth(3, 6500); // EST LOST AMT
		return sheet;
	}

	private HSSFCellStyle headerStyleBlock(HSSFWorkbook wb) {
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
		return headerStyle;
	}
	
	private HSSFCellStyle subHeaderStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont subTotalFontFont = wb.createFont();
		subTotalFontFont.setFontHeightInPoints((short) 10);
		subTotalFontFont.setFontName("SansSerif");
		subTotalFontFont.setBold(true);
		subTotalFontFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(subTotalFontFont);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	private HSSFCellStyle tableSubHeaderStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont subTotalFontFont = wb.createFont();
		subTotalFontFont.setFontHeightInPoints((short) 10);
		subTotalFontFont.setFontName("SansSerif");
		subTotalFontFont.setBold(true);
		subTotalFontFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(subTotalFontFont);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.AQUA.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.AQUA.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}
	
	private HSSFCellStyle tableHeaderStyleBlock(HSSFWorkbook wb) {
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
		return tableHeaderStyle;
	}

	private HSSFCellStyle tableBodyStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont tableBodyFont = wb.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		tableBodyFont.setFontName("SansSerif");
		style.setFont(tableBodyFont);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		return style;
	}

	private HSSFCellStyle tableTotalStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont subTotalFontFont = wb.createFont();
		subTotalFontFont.setFontHeightInPoints((short) 10);
		subTotalFontFont.setFontName("SansSerif");
		subTotalFontFont.setBold(true);
		subTotalFontFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(subTotalFontFont);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.PALE_BLUE.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.PALE_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	private HSSFCellStyle tableGrandTotalStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont subTotalFontFont = wb.createFont();
		subTotalFontFont.setFontHeightInPoints((short) 10);
		subTotalFontFont.setFontName("SansSerif");
		subTotalFontFont.setBold(true);
		subTotalFontFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(subTotalFontFont);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.BLUE_GREY.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.BLUE_GREY.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	/*** CLASS ***********************************************************************************/
	class CompanyDTO {
		private Long companyId;
		private String name;
		private int totalCount;
		private BigDecimal totalClaimedAmount;
		private BigDecimal totalOfferedOrPaidAmount;
		private BigDecimal totalClaimAmountOngoing;
		private BigDecimal totalOfferedOrPaidAmountOngoing;
		private BigDecimal totalClaimAmountResolve;
		private BigDecimal totalOfferedOrPaidAmountResolve;
		private List<ClaimDetailDTO> details;
		
		private int totalOpenCount;
		private int totalPendingDocumentCount;
		private int totalPendingAdjusterCount;
		private int totalPendingAcceptanceCount;
		private int totalPendingOfferCount;
		private int totalPendingPaymentCount;
		private int totalOngoingClaimCount;
		private int totalResolvedCount;
		private int totalClosedPaidCount;
		private int totalClosedWithinExcessCount;
		private int totalClosedDeclinedCount;
		private int totalClosedWithdrawnCount;
		private int totalAllCount;
		
		private BigDecimal totalOpenLossAmt;
		private BigDecimal totalPendingDocumentLossAmt;
		private BigDecimal totalPendingAdjusterLossAmt;
		private BigDecimal totalPendingAcceptanceLossAmt;
		private BigDecimal totalPendingOfferLossAmt;
		private BigDecimal totalPendingPaymentLossAmt;
		private BigDecimal totalOngoingClaimLossAmt;
		private BigDecimal totalResolvedLossAmt;
		private BigDecimal totalClosedPaidLossAmt;
		private BigDecimal totalClosedWithinExcessLossAmt;
		private BigDecimal totalClosedDeclinedLossAmt;
		private BigDecimal totalClosedWithdrawnLossAmt;
		private BigDecimal totalLossAmt;
		
		private BigDecimal totalOpenPaidAmt;
		private BigDecimal totalPendingDocumentPaidAmt;
		private BigDecimal totalPendingAdjusterPaidAmt;
		private BigDecimal totalPendingAcceptancePaidAmt;
		private BigDecimal totalPendingOfferPaidAmt;
		private BigDecimal totalPendingPaymentPaidAmt;
		private BigDecimal totalOngoingClaimPaidAmt;
		private BigDecimal totalResolvedPaidAmt;
		private BigDecimal totalClosedPaidPaidAmt;
		private BigDecimal totalClosedWithinExcessPaidAmt;
		private BigDecimal totalClosedDeclinedPaidAmt;
		private BigDecimal totalClosedWithdrawnPaidAmt;
		private BigDecimal totalPaidAmt;

		public CompanyDTO() {
			totalCount = 0;
			totalClaimedAmount = BigDecimal.ZERO;
			totalOfferedOrPaidAmount = BigDecimal.ZERO;
			totalClaimAmountOngoing = BigDecimal.ZERO;
			totalOfferedOrPaidAmountOngoing = BigDecimal.ZERO;
			totalClaimAmountResolve = BigDecimal.ZERO;
			totalOfferedOrPaidAmountResolve = BigDecimal.ZERO;
			
			details = new ArrayList<ClaimDetailDTO>();
			for (ClaimStatusEnum status : ClaimStatusEnum.values()) {
				ClaimDetailDTO detail = new ClaimDetailDTO();
				detail.setStatus(status);
				detail.setCount(0);
				detail.setClaimedAmount(BigDecimal.ZERO);
				detail.setOfferedAmount(BigDecimal.ZERO);
				detail.setPaidAmount(BigDecimal.ZERO);
				if (ClaimStatusEnum.CPAID.equals(detail.getStatus())
						|| ClaimStatusEnum.CUEX.equals(detail.getStatus())
						|| ClaimStatusEnum.CDCL.equals(detail.getStatus())
						|| ClaimStatusEnum.CWDTH.equals(detail.getStatus())) {
					detail.setStatusGroup("RESOLVED");
				} else {
					detail.setStatusGroup("ON-GOING");
				}
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

		public BigDecimal getTotalClaimAmountOngoing() {
			return totalClaimAmountOngoing;
		}

		public void setTotalClaimAmountOngoing(BigDecimal totalClaimAmountOngoing) {
			this.totalClaimAmountOngoing = totalClaimAmountOngoing;
		}

		public BigDecimal getTotalOfferedOrPaidAmountOngoing() {
			return totalOfferedOrPaidAmountOngoing;
		}

		public void setTotalOfferedOrPaidAmountOngoing(BigDecimal totalOfferedOrPaidAmountOngoing) {
			this.totalOfferedOrPaidAmountOngoing = totalOfferedOrPaidAmountOngoing;
		}

		public BigDecimal getTotalClaimAmountResolve() {
			return totalClaimAmountResolve;
		}

		public void setTotalClaimAmountResolve(BigDecimal totalClaimAmountResolve) {
			this.totalClaimAmountResolve = totalClaimAmountResolve;
		}

		public BigDecimal getTotalOfferedOrPaidAmountResolve() {
			return totalOfferedOrPaidAmountResolve;
		}

		public void setTotalOfferedOrPaidAmountResolve(BigDecimal totalOfferedOrPaidAmountResolve) {
			this.totalOfferedOrPaidAmountResolve = totalOfferedOrPaidAmountResolve;
		}

		public List<ClaimDetailDTO> getDetails() {
			return details;
		}

		public void setDetails(List<ClaimDetailDTO> details) {
			this.details = details;
		}

		public int getTotalOpenCount() {
			return totalOpenCount;
		}

		public void setTotalOpenCount(int totalOpenCount) {
			this.totalOpenCount = totalOpenCount;
		}

		public int getTotalPendingDocumentCount() {
			return totalPendingDocumentCount;
		}

		public void setTotalPendingDocumentCount(int totalPendingDocumentCount) {
			this.totalPendingDocumentCount = totalPendingDocumentCount;
		}

		public int getTotalPendingAdjusterCount() {
			return totalPendingAdjusterCount;
		}

		public void setTotalPendingAdjusterCount(int totalPendingAdjusterCount) {
			this.totalPendingAdjusterCount = totalPendingAdjusterCount;
		}

		public int getTotalPendingAcceptanceCount() {
			return totalPendingAcceptanceCount;
		}

		public void setTotalPendingAcceptanceCount(int totalPendingAcceptanceCount) {
			this.totalPendingAcceptanceCount = totalPendingAcceptanceCount;
		}

		public int getTotalPendingOfferCount() {
			return totalPendingOfferCount;
		}

		public void setTotalPendingOfferCount(int totalPendingOfferCount) {
			this.totalPendingOfferCount = totalPendingOfferCount;
		}

		public int getTotalPendingPaymentCount() {
			return totalPendingPaymentCount;
		}

		public void setTotalPendingPaymentCount(int totalPendingPaymentCount) {
			this.totalPendingPaymentCount = totalPendingPaymentCount;
		}

		public int getTotalOngoingClaimCount() {
			return totalOngoingClaimCount;
		}

		public void setTotalOngoingClaimCount(int totalOngoingClaimCount) {
			this.totalOngoingClaimCount = totalOngoingClaimCount;
		}
		
		public int getTotalResolvedCount() {
			return totalResolvedCount;
		}

		public void setTotalResolvedCount(int totalResolvedCount) {
			this.totalResolvedCount = totalResolvedCount;
		}

		public int getTotalClosedPaidCount() {
			return totalClosedPaidCount;
		}

		public void setTotalClosedPaidCount(int totalClosedPaidCount) {
			this.totalClosedPaidCount = totalClosedPaidCount;
		}

		public int getTotalClosedWithinExcessCount() {
			return totalClosedWithinExcessCount;
		}

		public void setTotalClosedWithinExcessCount(int totalClosedWithinExcessCount) {
			this.totalClosedWithinExcessCount = totalClosedWithinExcessCount;
		}

		public int getTotalClosedDeclinedCount() {
			return totalClosedDeclinedCount;
		}

		public void setTotalClosedDeclinedCount(int totalClosedDeclinedCount) {
			this.totalClosedDeclinedCount = totalClosedDeclinedCount;
		}

		public int getTotalClosedWithdrawnCount() {
			return totalClosedWithdrawnCount;
		}

		public void setTotalClosedWithdrawnCount(int totalClosedWithdrawnCount) {
			this.totalClosedWithdrawnCount = totalClosedWithdrawnCount;
		}

		public int getTotalAllCount() {
			return totalAllCount;
		}

		public void setTotalAllCount(int totalAllCount) {
			this.totalAllCount = totalAllCount;
		}

		public BigDecimal getTotalOpenLossAmt() {
			return totalOpenLossAmt;
		}

		public void setTotalOpenLossAmt(BigDecimal totalOpenLossAmt) {
			this.totalOpenLossAmt = totalOpenLossAmt;
		}

		public BigDecimal getTotalPendingDocumentLossAmt() {
			return totalPendingDocumentLossAmt;
		}

		public void setTotalPendingDocumentLossAmt(BigDecimal totalPendingDocumentLossAmt) {
			this.totalPendingDocumentLossAmt = totalPendingDocumentLossAmt;
		}

		public BigDecimal getTotalPendingAdjusterLossAmt() {
			return totalPendingAdjusterLossAmt;
		}

		public void setTotalPendingAdjusterLossAmt(BigDecimal totalPendingAdjusterLossAmt) {
			this.totalPendingAdjusterLossAmt = totalPendingAdjusterLossAmt;
		}

		public BigDecimal getTotalPendingAcceptanceLossAmt() {
			return totalPendingAcceptanceLossAmt;
		}

		public void setTotalPendingAcceptanceLossAmt(BigDecimal totalPendingAcceptanceLossAmt) {
			this.totalPendingAcceptanceLossAmt = totalPendingAcceptanceLossAmt;
		}

		public BigDecimal getTotalPendingOfferLossAmt() {
			return totalPendingOfferLossAmt;
		}

		public void setTotalPendingOfferLossAmt(BigDecimal totalPendingOfferLossAmt) {
			this.totalPendingOfferLossAmt = totalPendingOfferLossAmt;
		}

		public BigDecimal getTotalPendingPaymentLossAmt() {
			return totalPendingPaymentLossAmt;
		}

		public void setTotalPendingPaymentLossAmt(BigDecimal totalPendingPaymentLossAmt) {
			this.totalPendingPaymentLossAmt = totalPendingPaymentLossAmt;
		}

		public BigDecimal getTotalOngoingClaimLossAmt() {
			return totalOngoingClaimLossAmt;
		}

		public void setTotalOngoingClaimLossAmt(BigDecimal totalOngoingClaimLossAmt) {
			this.totalOngoingClaimLossAmt = totalOngoingClaimLossAmt;
		}

		public BigDecimal getTotalResolvedLossAmt() {
			return totalResolvedLossAmt;
		}

		public void setTotalResolvedLossAmt(BigDecimal totalResolvedLossAmt) {
			this.totalResolvedLossAmt = totalResolvedLossAmt;
		}

		public BigDecimal getTotalClosedPaidLossAmt() {
			return totalClosedPaidLossAmt;
		}

		public void setTotalClosedPaidLossAmt(BigDecimal totalClosedPaidLossAmt) {
			this.totalClosedPaidLossAmt = totalClosedPaidLossAmt;
		}

		public BigDecimal getTotalClosedWithinExcessLossAmt() {
			return totalClosedWithinExcessLossAmt;
		}

		public void setTotalClosedWithinExcessLossAmt(BigDecimal totalClosedWithinExcessLossAmt) {
			this.totalClosedWithinExcessLossAmt = totalClosedWithinExcessLossAmt;
		}

		public BigDecimal getTotalClosedDeclinedLossAmt() {
			return totalClosedDeclinedLossAmt;
		}

		public void setTotalClosedDeclinedLossAmt(BigDecimal totalClosedDeclinedLossAmt) {
			this.totalClosedDeclinedLossAmt = totalClosedDeclinedLossAmt;
		}

		public BigDecimal getTotalClosedWithdrawnLossAmt() {
			return totalClosedWithdrawnLossAmt;
		}

		public void setTotalClosedWithdrawnLossAmt(BigDecimal totalClosedWithdrawnLossAmt) {
			this.totalClosedWithdrawnLossAmt = totalClosedWithdrawnLossAmt;
		}

		public BigDecimal getTotalLossAmt() {
			return totalLossAmt;
		}

		public void setTotalLossAmt(BigDecimal totalLossAmt) {
			this.totalLossAmt = totalLossAmt;
		}

		public BigDecimal getTotalOpenPaidAmt() {
			return totalOpenPaidAmt;
		}

		public void setTotalOpenPaidAmt(BigDecimal totalOpenPaidAmt) {
			this.totalOpenPaidAmt = totalOpenPaidAmt;
		}

		public BigDecimal getTotalPendingDocumentPaidAmt() {
			return totalPendingDocumentPaidAmt;
		}

		public void setTotalPendingDocumentPaidAmt(BigDecimal totalPendingDocumentPaidAmt) {
			this.totalPendingDocumentPaidAmt = totalPendingDocumentPaidAmt;
		}

		public BigDecimal getTotalPendingAdjusterPaidAmt() {
			return totalPendingAdjusterPaidAmt;
		}

		public void setTotalPendingAdjusterPaidAmt(BigDecimal totalPendingAdjusterPaidAmt) {
			this.totalPendingAdjusterPaidAmt = totalPendingAdjusterPaidAmt;
		}

		public BigDecimal getTotalPendingAcceptancePaidAmt() {
			return totalPendingAcceptancePaidAmt;
		}

		public void setTotalPendingAcceptancePaidAmt(BigDecimal totalPendingAcceptancePaidAmt) {
			this.totalPendingAcceptancePaidAmt = totalPendingAcceptancePaidAmt;
		}

		public BigDecimal getTotalPendingOfferPaidAmt() {
			return totalPendingOfferPaidAmt;
		}

		public void setTotalPendingOfferPaidAmt(BigDecimal totalPendingOfferPaidAmt) {
			this.totalPendingOfferPaidAmt = totalPendingOfferPaidAmt;
		}

		public BigDecimal getTotalPendingPaymentPaidAmt() {
			return totalPendingPaymentPaidAmt;
		}

		public void setTotalPendingPaymentPaidAmt(BigDecimal totalPendingPaymentPaidAmt) {
			this.totalPendingPaymentPaidAmt = totalPendingPaymentPaidAmt;
		}

		public BigDecimal getTotalOngoingClaimPaidAmt() {
			return totalOngoingClaimPaidAmt;
		}

		public void setTotalOngoingClaimPaidAmt(BigDecimal totalOngoingClaimPaidAmt) {
			this.totalOngoingClaimPaidAmt = totalOngoingClaimPaidAmt;
		}

		public BigDecimal getTotalResolvedPaidAmt() {
			return totalResolvedPaidAmt;
		}

		public void setTotalResolvedPaidAmt(BigDecimal totalResolvedPaidAmt) {
			this.totalResolvedPaidAmt = totalResolvedPaidAmt;
		}

		public BigDecimal getTotalClosedPaidPaidAmt() {
			return totalClosedPaidPaidAmt;
		}

		public void setTotalClosedPaidPaidAmt(BigDecimal totalClosedPaidPaidAmt) {
			this.totalClosedPaidPaidAmt = totalClosedPaidPaidAmt;
		}

		public BigDecimal getTotalClosedWithinExcessPaidAmt() {
			return totalClosedWithinExcessPaidAmt;
		}

		public void setTotalClosedWithinExcessPaidAmt(BigDecimal totalClosedWithinExcessPaidAmt) {
			this.totalClosedWithinExcessPaidAmt = totalClosedWithinExcessPaidAmt;
		}

		public BigDecimal getTotalClosedDeclinedPaidAmt() {
			return totalClosedDeclinedPaidAmt;
		}

		public void setTotalClosedDeclinedPaidAmt(BigDecimal totalClosedDeclinedPaidAmt) {
			this.totalClosedDeclinedPaidAmt = totalClosedDeclinedPaidAmt;
		}

		public BigDecimal getTotalClosedWithdrawnPaidAmt() {
			return totalClosedWithdrawnPaidAmt;
		}

		public void setTotalClosedWithdrawnPaidAmt(BigDecimal totalClosedWithdrawnPaidAmt) {
			this.totalClosedWithdrawnPaidAmt = totalClosedWithdrawnPaidAmt;
		}

		public BigDecimal getTotalPaidAmt() {
			return totalPaidAmt;
		}

		public void setTotalPaidAmt(BigDecimal totalPaidAmt) {
			this.totalPaidAmt = totalPaidAmt;
		}
		
	}

	class ClaimDetailDTO {
		private String statusGroup;
		private ClaimStatusEnum status;
		private BigDecimal claimedAmount;
		private BigDecimal offeredAmount;
		private BigDecimal paidAmount;
		private int count;

		public String getStatusGroup() {
			return statusGroup;
		}

		public void setStatusGroup(String statusGroup) {
			this.statusGroup = statusGroup;
		}
		
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
	

//	OPN
//	PDOC 
//	PADJ
//	PACC
//	POFR
//	PPYMT
//	CPAID
//	CUEX
//	CDCL
//	CWDTH
}
