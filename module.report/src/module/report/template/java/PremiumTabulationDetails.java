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

public class PremiumTabulationDetails extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(PremiumTabulationDetails.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	
	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;
	private ReportGeneratorJavaStyle style;

	public PremiumTabulationDetails(ApplicationContext context, String logFile) {
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

		doPrintExcel(getPolicyDetails());
		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);

		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	private void doPrintExcel(List<PolicyDTO> policyDetails) {
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		boolean exception = false;
		try {
			wb = new HSSFWorkbook();			 
			if (style==null) {
				this.style = new ReportGeneratorJavaStyle();
			}
			HSSFSheet sheet = columnWidth(wb);
			
			// Header
			HSSFCellStyle headerStyle = style.headerStyleBlock(wb);
			HSSFCellStyle columnAreaTotalStyle = style.columnAreaTotalStyleBlock(wb);
			HSSFCellStyle tableBodyStyle = style.tableBodyStyleBlock(wb);
			HSSFCellStyle tableBodyStyleDecimalBlock = style.tableBodyStyleDecimalBlock(wb);
			HSSFCellStyle tableEmptyBodyStyle = style.tableEmptyBodyBlock(wb);
			HSSFCellStyle tableSubTotalStyle = style.tableSubTotalStyleBlock(wb);
			HSSFCellStyle tableTotalStyle = style.tableTotalStyleBlock(wb);
			
			HSSFRow row = sheet.createRow(0); // A
			HSSFCell cell = row.createCell(0); // 1
			
			//print header : report by and report at
			printReportDetails(headerStyle, sheet, cell, row);

			int startIndex = 5;
			
			// From-To Loss Date

			Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("dispFromDate");
			Timestamp toLossDate = (Timestamp) handler.getParamMap().get("dispToDate");
			
			if (fromLossDate != null) {
				row = sheet.createRow(startIndex);
				cell = row.createCell(0);
				cell.setCellValue("Start Date : ");
				cell = row.createCell(1);
				cell.setCellValue(sdf.format(fromLossDate));
				startIndex++;
			}
			
			if (toLossDate != null) {
				row = sheet.createRow(startIndex);
				cell = row.createCell(0);
				cell.setCellValue("End Date : ");
				cell = row.createCell(1);
				cell.setCellValue(sdf.format(toLossDate));
				startIndex++;
			}
			
			String companies = (String) handler.getParamMap().get("dispCompanyId");
			if (companies != null) {
				row = sheet.createRow(startIndex);
				cell = row.createCell(0);
				cell.setCellValue("Companies: ");
				cell = row.createCell(1);
				cell.setCellValue(companies);
				startIndex++;
			}

			String groupOfInsurance = (String) handler.getParamMap().get("dispGroupOfInsuranceCode");
			if (groupOfInsurance != null) {
				row = sheet.createRow(startIndex);
				cell = row.createCell(0);
				cell.setCellValue("Group of Insurance: ");
				cell = row.createCell(1);
				cell.setCellValue(groupOfInsurance);
				startIndex++;
			}

			String classOfInsurance = (String) handler.getParamMap().get("dispInsuranceClassCode");
			if (classOfInsurance != null) {
				row = sheet.createRow(startIndex);
				cell = row.createCell(0);
				cell.setCellValue("Class of of Insurance: ");
				cell = row.createCell(1);
				cell.setCellValue(classOfInsurance);
				startIndex++;
			}
			
			startIndex++;
			
			// Table Header
			row = sheet.createRow(startIndex); 
			cell = row.createCell(3);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell.setCellValue("RENEWAL POLICIES");
			cell = row.createCell(4);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(5);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(6);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(7);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 3, 7 ));
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			   
			cell = row.createCell(8);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell.setCellValue("ENDORSEMENT POLICIES");
			cell = row.createCell(9);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb)); 
			cell = row.createCell(10);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(11);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(12);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(13);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 8, 13 ));
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			
			cell = row.createCell(14);
			cell.setCellStyle(columnAreaTotalStyle);
			cell.setCellValue("GRAND TOTAL");
			cell = row.createCell(15);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(16);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(17);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			cell = row.createCell(18);
			cell.setCellStyle(style.tableHeaderStyleBlock(wb));
			sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 14, 18 ));
			cell.setCellStyle(columnAreaTotalStyle);
			startIndex++;
			
			// print table header
			printTableHeader(startIndex, sheet, wb);
			startIndex++;
			
			// Creating detail
			String incClassCat = ""; String incClassName = ""; String policyNo = ""; 

			BigDecimal subTotalPolicyGross = BigDecimal.ZERO; BigDecimal subTotalPolicyRebate = BigDecimal.ZERO;
			BigDecimal subTotalPolicyTax = BigDecimal.ZERO; BigDecimal subTotalPolicyNet = BigDecimal.ZERO;
			BigDecimal subTotalPolicyStampDuty = BigDecimal.ZERO;
			
			BigDecimal subTotalEndorsementGross = BigDecimal.ZERO; BigDecimal subTotalEndorsementRebate = BigDecimal.ZERO;
			BigDecimal subTotalEndorsementTax = BigDecimal.ZERO; BigDecimal subTotalEndorsementNet = BigDecimal.ZERO; 
			BigDecimal subTotalEndorsementStampDuty = BigDecimal.ZERO; 
			
			BigDecimal subTotalGross = BigDecimal.ZERO; BigDecimal subTotalRebate = BigDecimal.ZERO;
			BigDecimal subTotalTax = BigDecimal.ZERO; BigDecimal subTotalNet = BigDecimal.ZERO; 
			BigDecimal subTotalStampDuty = BigDecimal.ZERO; 
			
			BigDecimal totalPolicyGross = BigDecimal.ZERO; BigDecimal totalPolicyRebate = BigDecimal.ZERO;
			BigDecimal totalPolicyTax = BigDecimal.ZERO; BigDecimal totalPolicyNet = BigDecimal.ZERO;
			BigDecimal totalPolicyStampDuty = BigDecimal.ZERO;
			
			BigDecimal totalEndorsementGross = BigDecimal.ZERO; BigDecimal totalEndorsementRebate = BigDecimal.ZERO;
			BigDecimal totalEndorsementTax = BigDecimal.ZERO; BigDecimal totalEndorsementNet = BigDecimal.ZERO;
			BigDecimal totalEndorsementStampDuty = BigDecimal.ZERO;
			
			BigDecimal totalGross = BigDecimal.ZERO; BigDecimal totalRebate = BigDecimal.ZERO;
			BigDecimal totalTax = BigDecimal.ZERO; BigDecimal totalNet = BigDecimal.ZERO;
			BigDecimal totalStampDuty = BigDecimal.ZERO;
			
			for (int i = 0; i < policyDetails.size(); i++) {
				row = sheet.createRow(startIndex);
				cell = row.createCell(0);
				if (policyDetails.get(i).getInsuranceClassCategory() != null
						&& !incClassCat.equalsIgnoreCase(policyDetails.get(i).getInsuranceClassCategory())) {
					if (i != 0) {
						cell.setCellValue("SUB TOTAL");
						cell.setCellStyle(tableSubTotalStyle);
						sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 0, 2));
						
						cell = row.createCell(3);
						cell.setCellType(CellType.NUMERIC);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalPolicyGross.doubleValue());
						
						cell = row.createCell(4);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalPolicyRebate.doubleValue());

						cell = row.createCell(5);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalPolicyTax.doubleValue());

						cell = row.createCell(6);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalPolicyStampDuty.doubleValue());

						cell = row.createCell(7);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalPolicyNet.doubleValue());
						
						cell = row.createCell(8);
						cell.setCellStyle(tableSubTotalStyle);
						
						cell = row.createCell(9);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalEndorsementGross.doubleValue());
						
						cell = row.createCell(10);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalEndorsementRebate.doubleValue());

						cell = row.createCell(11);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalEndorsementTax.doubleValue());

						cell = row.createCell(12);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalEndorsementStampDuty.doubleValue());

						cell = row.createCell(13);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalEndorsementNet.doubleValue());
						
						subTotalGross = subTotalPolicyGross.add(subTotalEndorsementGross); 
						cell = row.createCell(14);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalGross.doubleValue());
						
						subTotalRebate = subTotalPolicyRebate.add(subTotalEndorsementRebate);
						cell = row.createCell(15);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalRebate.doubleValue());

						subTotalTax = subTotalPolicyTax.add(subTotalEndorsementTax);
						cell = row.createCell(16);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalTax.doubleValue());

						subTotalStampDuty = subTotalPolicyStampDuty.add(subTotalEndorsementStampDuty);
						cell = row.createCell(17);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalStampDuty.doubleValue());
						
						subTotalNet = subTotalPolicyNet.add(subTotalEndorsementNet);
						cell = row.createCell(18);
						cell.setCellStyle(tableSubTotalStyle);
						cell.setCellValue(subTotalNet.doubleValue());
						
						subTotalPolicyGross = BigDecimal.ZERO; subTotalPolicyRebate = BigDecimal.ZERO;
						subTotalPolicyTax = BigDecimal.ZERO; subTotalPolicyNet = BigDecimal.ZERO;
						subTotalPolicyStampDuty = BigDecimal.ZERO;
						
						subTotalEndorsementGross = BigDecimal.ZERO; subTotalEndorsementRebate = BigDecimal.ZERO;
						subTotalEndorsementTax = BigDecimal.ZERO; subTotalEndorsementNet = BigDecimal.ZERO;
						subTotalEndorsementStampDuty = BigDecimal.ZERO;
						
						subTotalGross = BigDecimal.ZERO; subTotalRebate = BigDecimal.ZERO;
						subTotalTax = BigDecimal.ZERO; subTotalNet = BigDecimal.ZERO; 
						subTotalStampDuty = BigDecimal.ZERO;
						
						startIndex++;
						row = sheet.createRow(startIndex);
						cell = row.createCell(0);
					}
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policyDetails.get(i).getInsuranceClassCategory());
				}
				
				cell = row.createCell(1);
				if (policyDetails.get(i).getInsuranceClassName() != null
						&& !incClassName.equalsIgnoreCase(policyDetails.get(i).getInsuranceClassName())) {
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policyDetails.get(i).getInsuranceClassName());
				} else {
					cell.setCellStyle(tableEmptyBodyStyle);
				}

				int idxDetails = 2;
				if (policyDetails.get(i).getPolicyNo() != null
						&& !policyNo.equalsIgnoreCase(policyDetails.get(i).getPolicyNo())) {
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policyDetails.get(i).getPolicyNo());

					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getPremiumGross()!=null) {
						cell.setCellValue(policyDetails.get(i).getPremiumGross().doubleValue());
						subTotalPolicyGross = subTotalPolicyGross.add(policyDetails.get(i).getPremiumGross());
						totalPolicyGross = totalPolicyGross.add(policyDetails.get(i).getPremiumGross());
						totalGross = totalGross.add(totalPolicyGross);
					}
					
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getPremiumRebate()!=null) {
						cell.setCellValue(policyDetails.get(i).getPremiumRebate().doubleValue());
						subTotalPolicyRebate = subTotalPolicyRebate.add(policyDetails.get(i).getPremiumRebate());
						totalPolicyRebate = totalPolicyRebate.add(policyDetails.get(i).getPremiumRebate());
						totalRebate = totalRebate.add(totalPolicyRebate);
					}

					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getPremiumTax()!=null) {
						cell.setCellValue(policyDetails.get(i).getPremiumTax().doubleValue());
						subTotalPolicyTax = subTotalPolicyTax.add(policyDetails.get(i).getPremiumTax());
						totalPolicyTax = totalPolicyTax.add(policyDetails.get(i).getPremiumTax());
						totalTax = totalTax.add(totalPolicyTax);
					}

					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getPremiumStampDuty()!=null) {
						cell.setCellValue(policyDetails.get(i).getPremiumStampDuty().doubleValue());
						subTotalPolicyStampDuty = subTotalPolicyStampDuty.add(policyDetails.get(i).getPremiumStampDuty());
						totalPolicyStampDuty = totalPolicyStampDuty.add(policyDetails.get(i).getPremiumStampDuty());
						totalStampDuty = totalStampDuty.add(totalPolicyStampDuty);
					}

					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getPremiumNet()!=null) {
						cell.setCellValue(policyDetails.get(i).getPremiumNet().doubleValue());
						subTotalPolicyNet = subTotalPolicyNet.add(policyDetails.get(i).getPremiumNet());
						totalPolicyNet = totalPolicyNet.add(policyDetails.get(i).getPremiumNet());
						totalNet = totalNet.add(totalPolicyNet);
					}
				} else {
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableEmptyBodyStyle);

					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableEmptyBodyStyle);
					
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableEmptyBodyStyle);
					
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableEmptyBodyStyle);
					
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableEmptyBodyStyle);
					
					cell = row.createCell(idxDetails++);
					cell.setCellStyle(tableEmptyBodyStyle);
				}

				int detailIdx = 8;
				cell = row.createCell(detailIdx);
				cell.setCellStyle(tableBodyStyle);
				cell.setCellValue(policyDetails.get(i).getEndorsmentNo());
				detailIdx++;

				cell = row.createCell(detailIdx);
				cell.setCellStyle(tableBodyStyleDecimalBlock);
				if (policyDetails.get(i).getGross()!=null) {
					cell.setCellValue(policyDetails.get(i).getGross().doubleValue());
					subTotalEndorsementGross = subTotalEndorsementGross.add(policyDetails.get(i).getGross());
					totalEndorsementGross = totalEndorsementGross.add(policyDetails.get(i).getGross());
				}
				detailIdx++;

				cell = row.createCell(detailIdx);
				cell.setCellStyle(tableBodyStyleDecimalBlock);
				if (policyDetails.get(i).getRebate()!=null) {
					cell.setCellValue(policyDetails.get(i).getRebate().doubleValue());
					subTotalEndorsementRebate = subTotalEndorsementRebate.add(policyDetails.get(i).getRebate());
					totalEndorsementRebate = totalEndorsementRebate.add(policyDetails.get(i).getRebate());
				}
				detailIdx++;

				cell = row.createCell(detailIdx);
				cell.setCellStyle(tableBodyStyleDecimalBlock);
				if (policyDetails.get(i).getTax()!=null) {
					cell.setCellValue(policyDetails.get(i).getTax().doubleValue());
					subTotalEndorsementTax = subTotalEndorsementTax.add(policyDetails.get(i).getTax());
					totalEndorsementTax = totalEndorsementTax.add(policyDetails.get(i).getTax());
				}
				detailIdx++;

				cell = row.createCell(detailIdx);
				cell.setCellStyle(tableBodyStyleDecimalBlock);
				if (policyDetails.get(i).getStampDuty()!=null) {
					cell.setCellValue(policyDetails.get(i).getStampDuty().doubleValue());
					subTotalEndorsementStampDuty = subTotalEndorsementStampDuty.add(policyDetails.get(i).getStampDuty());
					totalEndorsementStampDuty = totalEndorsementStampDuty.add(policyDetails.get(i).getStampDuty());
				}
				detailIdx++;

				cell = row.createCell(detailIdx);
				cell.setCellStyle(tableBodyStyleDecimalBlock);
				if (policyDetails.get(i).getNet()!=null) {
					cell.setCellValue(policyDetails.get(i).getNet().doubleValue());
					subTotalEndorsementNet = subTotalEndorsementNet.add(policyDetails.get(i).getNet());
					totalEndorsementNet = totalEndorsementNet.add(policyDetails.get(i).getNet());
				}
				detailIdx++;

				if (policyDetails.get(i).getPolicyNo() != null
						&& !policyNo.equalsIgnoreCase(policyDetails.get(i).getPolicyNo())) {
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getTotalGross()!=null) {
						cell.setCellValue(policyDetails.get(i).getTotalGross().doubleValue());
					}
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getTotalRebate()!=null) {
						cell.setCellValue(policyDetails.get(i).getTotalRebate().doubleValue());
					}
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getTotalTax()!=null) {
						cell.setCellValue(policyDetails.get(i).getTotalTax().doubleValue());
					}
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getTotalStampDuty()!=null) {
						cell.setCellValue(policyDetails.get(i).getTotalStampDuty().doubleValue());
					}
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableBodyStyleDecimalBlock);
					if (policyDetails.get(i).getTotalNet()!=null) {
						cell.setCellValue(policyDetails.get(i).getTotalNet().doubleValue());
					}
					detailIdx++;
				} else {
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableEmptyBodyStyle);
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableEmptyBodyStyle);
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableEmptyBodyStyle);
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableEmptyBodyStyle);
					detailIdx++;
					
					cell = row.createCell(detailIdx);
					cell.setCellStyle(tableEmptyBodyStyle);
					detailIdx++;
				}
				
				incClassCat = policyDetails.get(i).getInsuranceClassCategory();
				incClassName = policyDetails.get(i).getInsuranceClassName();
				policyNo = policyDetails.get(i).getPolicyNo();
								
				startIndex++;
			}

			row = sheet.createRow(startIndex);
			cell = row.createCell(0);
			cell.setCellValue("SUB TOTAL");
			cell.setCellStyle(tableSubTotalStyle);
			sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 0, 2)); 
			
			cell = row.createCell(3);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalPolicyGross.doubleValue());
			
			cell = row.createCell(4);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalPolicyRebate.doubleValue());

			cell = row.createCell(5);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalPolicyTax.doubleValue());

			cell = row.createCell(6);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalPolicyStampDuty.doubleValue());
			
			cell = row.createCell(7);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalPolicyNet.doubleValue());
			
			cell = row.createCell(8);
			cell.setCellStyle(tableSubTotalStyle);
			
			cell = row.createCell(9);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalEndorsementGross.doubleValue());
			
			cell = row.createCell(10);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalEndorsementRebate.doubleValue());

			cell = row.createCell(11);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalEndorsementTax.doubleValue());

			cell = row.createCell(12);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalEndorsementStampDuty.doubleValue());

			cell = row.createCell(13);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalEndorsementNet.doubleValue());
			
			subTotalGross = subTotalPolicyGross.add(subTotalEndorsementGross);
			cell = row.createCell(14);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalGross.doubleValue());
			
			subTotalRebate = subTotalPolicyRebate.add(subTotalEndorsementRebate);
			cell = row.createCell(15);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalRebate.doubleValue());

			subTotalTax = subTotalPolicyTax.add(subTotalEndorsementTax);
			cell = row.createCell(16);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalTax.doubleValue());

			subTotalTax = subTotalPolicyTax.add(subTotalEndorsementTax);
			cell = row.createCell(17);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalStampDuty.doubleValue());

			subTotalNet = subTotalPolicyNet.add(subTotalEndorsementNet);
			cell = row.createCell(18);
			cell.setCellStyle(tableSubTotalStyle);
			cell.setCellValue(subTotalNet.doubleValue());
			
			startIndex++;
			row = sheet.createRow(startIndex);
			cell = row.createCell(0);
			cell.setCellValue("GRAND TOTAL");
			cell.setCellStyle(tableTotalStyle);
			sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 0, 2)); 

			cell = row.createCell(3);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalPolicyGross.doubleValue());
			
			cell = row.createCell(4);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalPolicyRebate.doubleValue());

			cell = row.createCell(5);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalPolicyTax.doubleValue());

			cell = row.createCell(6);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalPolicyStampDuty.doubleValue());

			cell = row.createCell(7);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalPolicyNet.doubleValue());

			cell = row.createCell(8);
			cell.setCellStyle(tableTotalStyle);
			
			cell = row.createCell(9);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalEndorsementGross.doubleValue());
			
			cell = row.createCell(10);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalEndorsementRebate.doubleValue());

			cell = row.createCell(11);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalEndorsementTax.doubleValue());

			cell = row.createCell(12);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalEndorsementStampDuty.doubleValue());

			cell = row.createCell(13);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalEndorsementNet.doubleValue());
			
			totalGross = totalPolicyGross.add(totalEndorsementGross);
			cell = row.createCell(14);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalGross.doubleValue());
			
			totalRebate = totalPolicyRebate.add(totalEndorsementRebate);
			cell = row.createCell(15);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalRebate.doubleValue());
			
			totalTax = totalPolicyTax.add(totalEndorsementTax); 
			cell = row.createCell(16);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalTax.doubleValue());
			
			totalStampDuty = totalPolicyStampDuty.add(totalStampDuty); 
			cell = row.createCell(17);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalStampDuty.doubleValue());
			
			totalNet = totalPolicyNet.add(totalEndorsementNet);
			cell = row.createCell(18);
			cell.setCellStyle(tableTotalStyle);
			cell.setCellValue(totalNet.doubleValue());
			
			fileOut = new FileOutputStream(submission.getFullOutputFile());
			wb.write(fileOut);
			fileOut.close();
			wb.close();
			if (policyDetails.size() == 0) {
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

	private HSSFSheet columnWidth(HSSFWorkbook wb) {
		HSSFSheet sheet = wb.createSheet();
		int idx = 0;
		sheet.setColumnWidth(idx, 6000); idx++;
		sheet.setColumnWidth(idx, 8000); idx++;
		sheet.setColumnWidth(idx, 8000); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 4000); idx++;
		sheet.setColumnWidth(idx, 3800); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 8000); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 4000); idx++;
		sheet.setColumnWidth(idx, 3800); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		sheet.setColumnWidth(idx, 4000); idx++;
		sheet.setColumnWidth(idx, 3800); idx++;
		sheet.setColumnWidth(idx, 6500); idx++;
		return sheet;
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
		cell.setCellValue("Premium Tabulation Details");
		cell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 0, 6 ));
	}

	private void printTableHeader(int rowIdx, HSSFSheet sheet, HSSFWorkbook wb) {
		HSSFCellStyle tableHeaderStyle = style.tableHeaderStyleBlock(wb);
		HSSFCellStyle columnAreaTotalStyle = style.columnAreaTotalStyleBlock(wb);
		
		int tableHeaderIdx = 0;
		HSSFRow row = sheet.createRow(rowIdx);
		HSSFCell cell =  row.createCell(tableHeaderIdx);
		
		row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Class of Insurance");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Type of Policy");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Policy Number");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Premium (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Premium Rebate (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Tax Amount (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Stamp Duty (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Net Premium (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Endorsement No.");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Premium (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Premium Rebate (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Tax Amount (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Stamp Duty (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("Net Premium (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(columnAreaTotalStyle);
		cell.setCellValue("Premium (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(columnAreaTotalStyle);
		cell.setCellValue("Premium Rebate (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(columnAreaTotalStyle);
		cell.setCellValue("Tax Amount (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(columnAreaTotalStyle);
		cell.setCellValue("Stamp Duty (RM)");
		tableHeaderIdx++;

		cell = row.createCell(tableHeaderIdx);
		cell.setCellStyle(columnAreaTotalStyle);
		cell.setCellValue("Net Premium (RM)");
	}
	
	private List<PolicyDTO> getPolicyDetails() {
		LOGGER.info("Getting All Policy and their respective Endorsement");
		
		// PARAMETER
		Object[] userCompany = (Object[]) handler.getParamMap().get("userCompany");
		Long companyIds = (Long) handler.getParamMap().get("companies");
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
				Company dbCompany = companyService.getCompany((Long) companyIds);
				if (dbCompany != null) {
					CompanyDTO dto = new CompanyDTO();
					dto.setName(dbCompany.getName());
					companies.put(dbCompany.getId(), dto);
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
				Company dbCompany = companyService.getCompany((Long) companyIds);
				if (dbCompany != null) {
					CompanyDTO dto = new CompanyDTO();
					dto.setName(dbCompany.getName());
					companies.put(dbCompany.getId(), dto);
				}
			}
		}

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
			sql.append(" UPPER(icc.name) as insClass,");
			sql.append(" INITCAP(ic.name) as typeOfPolicy,");
			sql.append(" UPPER(p.policy_no) as policy_no,");
			sql.append(" COALESCE(p.premium_gross,null) as policy_gross,");
			sql.append(" COALESCE(p.premium_rebate,null) as policy_rebate,");
			sql.append(" COALESCE(p.premium_tax,null) as policy_gst,");
			sql.append(" COALESCE(p.stamp_duty,null) as policy_stamp_duty,");
			sql.append(" COALESCE(p.premium_net,null) as policy_net,");
			sql.append(" UPPER(e.endorsement_no) as endorsement_no,");
			sql.append(" COALESCE(e.gross_premium,null) as endorsement_gross,");
			sql.append(" COALESCE(e.rebate_premium,null) as endorsement_rebate,");
			sql.append(" COALESCE(e.tax_amount,null) as endorsement_gst,");
			sql.append(" COALESCE(e.stamp_duty,null) as endorsement_stamp_duty,");
			sql.append(" COALESCE(e.net_premium,null) as endorsement_net,");
			sql.append(" case when pe.total_gross is not null then pe.total_gross else p.premium_gross end as total_gross,");
			sql.append(" case when pe.total_rebate is not null then pe.total_rebate else p.premium_rebate end as total_rebate,");
			sql.append(" case when pe.total_tax is not null then pe.total_tax else p.premium_tax end as total_tax,");
			sql.append(" case when pe.total_stamp_duty is not null then pe.total_stamp_duty else p.stamp_duty end as total_stamp_duty,");
			sql.append(" case when pe.total_net is not null then pe.total_net else p.premium_net end as total_net,");
			sql.append(" UPPER(c.name) as companyName");
		sql.append(" FROM policy p");
			sql.append(" LEFT JOIN policy_endorsement e ON e.policy_id=p.id");
			sql.append(" INNER JOIN company c ON c.id = p.company_id");
			sql.append(" INNER JOIN insurance_class ic ON ic.code = p.insurance_class_code");
			sql.append(" INNER JOIN insurance_class_category icc ON icc.code = ic.category");
			sql.append(" LEFT JOIN (SELECT  PE.POLICY_ID, ");
					sql.append(" POLICY.PREMIUM_GROSS + SUM(COALESCE(PE.GROSS_PREMIUM,0)) AS TOTAL_GROSS, " );
					sql.append(" POLICY.PREMIUM_REBATE + SUM(COALESCE(PE.REBATE_PREMIUM,0)) AS TOTAL_REBATE, ");
					sql.append(" POLICY.PREMIUM_TAX + SUM(COALESCE(PE.TAX_AMOUNT,0)) AS TOTAL_TAX,");
					sql.append(" POLICY.STAMP_DUTY + SUM(COALESCE(PE.STAMP_DUTY,0)) AS TOTAL_STAMP_DUTY,");
					sql.append(" POLICY.PREMIUM_NET + SUM(COALESCE(PE.NET_PREMIUM,0)) AS TOTAL_NET");
					sql.append(" FROM POLICY ");
					sql.append(" LEFT JOIN POLICY_ENDORSEMENT PE ON POLICY.ID = PE.POLICY_ID");
					sql.append(" GROUP BY PE.POLICY_ID, POLICY.PREMIUM_GROSS, POLICY.PREMIUM_REBATE, POLICY.PREMIUM_TAX, POLICY.PREMIUM_NET, POLICY.STAMP_DUTY)");
			sql.append(" pe ON pe.policy_id = p.id");
		sql.append(" WHERE 1 = 1 ");
		
		if (companyIds != null ) {
			sql.append(" and c.id in (");
				sql.append("?");
			sql.append(") ");
		}

		if (userCompany != null && userCompany.length > 0) {
			sql.append(" and c.code in (");
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
		
		Timestamp fromLossDate = (Timestamp) handler.getParamMap().get("fromDate");
		Timestamp toLossDate = (Timestamp) handler.getParamMap().get("toDate");
		if (fromLossDate != null && toLossDate != null) {
			sql.append(" and p.start_date between ? and ? ");
		} else if (fromLossDate != null && toLossDate == null) {
			sql.append(" and p.start_date >= ? ");
		} else if (fromLossDate == null && toLossDate != null) {
			sql.append(" and p.end_date <= ? ");
		}
		
		sql.append(" GROUP BY icc.name, ic.name, p.policy_no, p.premium_gross, p.premium_rebate, p.premium_tax, p.stamp_duty,");
		sql.append(" p.premium_net, e.endorsement_no, e.gross_premium, e.rebate_premium, e.tax_amount, e.stamp_duty, ");
        sql.append(" e.net_premium, c.name, pe.total_gross, pe.total_rebate, pe.total_tax, pe.total_stamp_duty, pe.total_net, icc.sort_order, e.sort_order ");
		sql.append(" ORDER BY icc.sort_order, ic.name, p.policy_no desc");	
		LOGGER.info("SQL : [" + sql.toString() + "]");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<PolicyDTO> policesDTO = new ArrayList<PolicyDTO>();
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql.toString());

			int parameterIdx = 1;
			
			if (companyIds != null) {
				ps.setLong(parameterIdx++, (Long) companyIds);
			}

			if (userCompany != null && userCompany.length > 0) {
				for (Object uc : userCompany) {
					ps.setString(parameterIdx++, (String) uc);
				}
			}
			
			if (groupOfInsuranceCode!=null && !groupOfInsuranceCode.isEmpty()) {
				ps.setString(parameterIdx++, (String) groupOfInsuranceCode);
			}
			
			if (insuranceClassCode != null && insuranceClassCode.length > 0) {
				for (Object ic : insuranceClassCode) {
					ps.setString(parameterIdx++, (String) ic);
				}
			}
			
			if (fromLossDate != null && toLossDate != null) {
				ps.setTimestamp(parameterIdx++, fromLossDate);
				ps.setTimestamp(parameterIdx++, toLossDate);
			} else if (fromLossDate != null && toLossDate == null) {
				ps.setTimestamp(parameterIdx++, fromLossDate);
			} else if (fromLossDate == null && toLossDate != null) {
				ps.setTimestamp(parameterIdx++, toLossDate);
			}
			
			rs = ps.executeQuery();
			while(rs.next()) {
				int idx = 1; 
				PolicyDTO dto = new PolicyDTO();
				dto.setInsuranceClassCategory(rs.getString(idx));idx++;
				dto.setInsuranceClassName(rs.getString(idx));idx++;
				dto.setPolicyNo(rs.getString(idx));idx++;
				dto.setPremiumGross(rs.getBigDecimal(idx));idx++;
				dto.setPremiumRebate(rs.getBigDecimal(idx));idx++;
				dto.setPremiumTax(rs.getBigDecimal(idx));idx++;
				dto.setPremiumStampDuty(rs.getBigDecimal(idx));idx++;
				dto.setPremiumNet(rs.getBigDecimal(idx));idx++;
				dto.setEndorsmentNo(rs.getString(idx));idx++;
				dto.setGross(rs.getBigDecimal(idx));idx++;
				dto.setRebate(rs.getBigDecimal(idx));idx++;
				dto.setTax(rs.getBigDecimal(idx));idx++;
				dto.setStampDuty(rs.getBigDecimal(idx));idx++;
				dto.setNet(rs.getBigDecimal(idx));idx++;
				dto.setTotalGross(rs.getBigDecimal(idx));idx++;
				dto.setTotalRebate(rs.getBigDecimal(idx));idx++;
				dto.setTotalTax(rs.getBigDecimal(idx));idx++;
				dto.setTotalStampDuty(rs.getBigDecimal(idx));idx++;
				dto.setTotalNet(rs.getBigDecimal(idx));idx++;
				policesDTO.add(dto);
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
		
		return policesDTO;
	}
	
	// IDA : NEW CLASS IF NOT CLASS DO NOT GO BEYOND THIS
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
	
	class PolicyDTO {
		private String insuranceClassCategory;
		private String insuranceClassName;
		
		private String policyNo;
		private BigDecimal premiumGross;
		private BigDecimal premiumRebate;
		private BigDecimal premiumTax;
		private BigDecimal premiumStampDuty;
		private BigDecimal premiumNet;
		
		private String endorsmentNo;
		private BigDecimal gross;
		private BigDecimal rebate;
		private BigDecimal tax;
		private BigDecimal stampDuty;
		private BigDecimal net;

		private BigDecimal totalGross;
		private BigDecimal totalRebate;
		private BigDecimal totalTax;
		private BigDecimal totalStampDuty;
		private BigDecimal totalNet;
		
		private int totalCount;

		public String getInsuranceClassCategory() {
			return insuranceClassCategory;
		}

		public void setInsuranceClassCategory(String insuranceClassCategory) {
			this.insuranceClassCategory = insuranceClassCategory;
		}

		public String getInsuranceClassName() {
			return insuranceClassName;
		}

		public void setInsuranceClassName(String insuranceClassName) {
			this.insuranceClassName = insuranceClassName;
		}

		public String getPolicyNo() {
			return policyNo;
		}

		public void setPolicyNo(String policyNo) {
			this.policyNo = policyNo;
		}

		public BigDecimal getPremiumGross() {
			return premiumGross;
		}

		public void setPremiumGross(BigDecimal premiumGross) {
			this.premiumGross = premiumGross;
		}

		public BigDecimal getPremiumRebate() {
			return premiumRebate;
		}

		public void setPremiumRebate(BigDecimal premiumRebate) {
			this.premiumRebate = premiumRebate;
		}

		public BigDecimal getPremiumTax() {
			return premiumTax;
		}

		public void setPremiumTax(BigDecimal premiumTax) {
			this.premiumTax = premiumTax;
		}
		
		public BigDecimal getPremiumNet() {
			return premiumNet;
		}

		public void setPremiumNet(BigDecimal premiumNet) {
			this.premiumNet = premiumNet;
		}

		public String getEndorsmentNo() {
			return endorsmentNo;
		}

		public void setEndorsmentNo(String endorsmentNo) {
			this.endorsmentNo = endorsmentNo;
		}

		public BigDecimal getGross() {
			return gross;
		}

		public void setGross(BigDecimal gross) {
			this.gross = gross;
		}

		public BigDecimal getRebate() {
			return rebate;
		}

		public void setRebate(BigDecimal rebate) {
			this.rebate = rebate;
		}

		public BigDecimal getTax() {
			return tax;
		}

		public void setTax(BigDecimal tax) {
			this.tax = tax;
		}

		public BigDecimal getNet() {
			return net;
		}

		public void setNet(BigDecimal net) {
			this.net = net;
		}

		public int getTotalCount() {
			return totalCount;
		}

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}
		

		public BigDecimal getTotalGross() {
			return totalGross;
		}

		public void setTotalGross(BigDecimal totalGross) {
			this.totalGross = totalGross;
		}

		public BigDecimal getTotalRebate() {
			return totalRebate;
		}

		public void setTotalRebate(BigDecimal totalRebate) {
			this.totalRebate = totalRebate;
		}

		public BigDecimal getTotalTax() {
			return totalTax;
		}

		public void setTotalTax(BigDecimal totalTax) {
			this.totalTax = totalTax;
		}

		public BigDecimal getTotalNet() {
			return totalNet;
		}

		public void setTotalNet(BigDecimal totalNet) {
			this.totalNet = totalNet;
		}

		public BigDecimal getPremiumStampDuty() {
			return premiumStampDuty;
		}

		public void setPremiumStampDuty(BigDecimal premiumStampDuty) {
			this.premiumStampDuty = premiumStampDuty;
		}

		public BigDecimal getStampDuty() {
			return stampDuty;
		}

		public void setStampDuty(BigDecimal stampDuty) {
			this.stampDuty = stampDuty;
		}

		public BigDecimal getTotalStampDuty() {
			return totalStampDuty;
		}

		public void setTotalStampDuty(BigDecimal totalStampDuty) {
			this.totalStampDuty = totalStampDuty;
		}
	}

}
