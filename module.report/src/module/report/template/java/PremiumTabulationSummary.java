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

public class PremiumTabulationSummary extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(PremiumTabulationSummary.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;
	private ReportGeneratorJavaStyle style;
	
	public PremiumTabulationSummary(ApplicationContext context, String logFile) {
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
	
	@Override
	public void run() {
		LogUtils.addSiftAppender(((ReportModule) context.getBean("ReportModule")).getModuleName());
		LogUtils.startLogging(submission.getFullLogFile());
		LocalDateTime startDate = LocalDateTime.now();
		LOGGER.info("Start date : " + startDate.format(DATETIME_FORMAT));

		doPrintExcel(getPolicySummary());
		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);

		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	private void doPrintExcel(List<PolicyDTO> policySummary) {
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		if (style==null) {
			this.style = new ReportGeneratorJavaStyle();
		}
		boolean exception = false;
		try {
			wb = new HSSFWorkbook();
			HSSFSheet sheet = columnWidth(wb);
			HSSFCellStyle headerStyle = style.headerStyleBlock(wb);
			HSSFCellStyle tableBodyStyle = style.tableBodyDetailStyleBlock(wb);
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
						
			String category = "";
			for (int i = 0; i < policySummary.size(); i++) {
				if (!category.equalsIgnoreCase(policySummary.get(i).getInsuranceClassCategory())) {
					rowIndex++;
					
					printTableHeader(sheet, row,  cell, rowIndex, policySummary.get(i).getInsuranceClassCategory(), wb);
					rowIndex = rowIndex + 2;
					row = sheet.createRow(rowIndex);
					int cellIdx = 0;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getCompanyId());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getCompanyCode());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumGross().doubleValue());
					cellIdx++;

					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumRebate().doubleValue());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumTax().doubleValue());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumStampDuty().doubleValue());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumNet().doubleValue());
					cellIdx++;
					
					// ENDORSEMENT
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementGross() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementGross().doubleValue());
					}
					cellIdx++;

					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementRebate()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementRebate().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementTax()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementTax().doubleValue());						
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementTax()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementStampDuty().doubleValue());						
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementNet()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementNet().doubleValue());
					}
					cellIdx++;
					
					// TOTAL
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumGross() != null 
							&& policySummary.get(i).getEndorsementGross() != null) {
						BigDecimal totalGross = policySummary.get(i).getEndorsementGross().add(policySummary.get(i).getPremiumGross());
						cell.setCellValue(totalGross.doubleValue());
					} else if (policySummary.get(i).getPremiumGross() != null && policySummary.get(i).getEndorsementGross() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumGross().doubleValue());
					} else if (policySummary.get(i).getPremiumGross() == null && policySummary.get(i).getEndorsementGross() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementGross().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumRebate() != null && policySummary.get(i).getEndorsementRebate() != null) {
						BigDecimal totalRebate = policySummary.get(i).getEndorsementRebate().add(policySummary.get(i).getPremiumRebate());
						cell.setCellValue(totalRebate.doubleValue());
					} else if (policySummary.get(i).getPremiumRebate() != null && policySummary.get(i).getEndorsementRebate() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumRebate().doubleValue());
					} else if (policySummary.get(i).getPremiumRebate() == null && policySummary.get(i).getEndorsementRebate() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementRebate().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumTax() != null && policySummary.get(i).getEndorsementTax() != null) {
						BigDecimal totalTax = policySummary.get(i).getEndorsementTax().add(policySummary.get(i).getPremiumTax());
						cell.setCellValue(totalTax.doubleValue());
					} else if (policySummary.get(i).getPremiumTax() != null && policySummary.get(i).getEndorsementTax() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumTax().doubleValue());
					} else if (policySummary.get(i).getPremiumTax() == null && policySummary.get(i).getEndorsementTax() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementTax().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumStampDuty() != null && policySummary.get(i).getEndorsementStampDuty() != null) {
						BigDecimal totalStampDuty = policySummary.get(i).getEndorsementStampDuty().add(policySummary.get(i).getPremiumStampDuty());
						cell.setCellValue(totalStampDuty.doubleValue());
					} else if (policySummary.get(i).getPremiumStampDuty() != null && policySummary.get(i).getEndorsementStampDuty() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumTax().doubleValue());
					} else if (policySummary.get(i).getPremiumStampDuty() == null && policySummary.get(i).getEndorsementStampDuty() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementStampDuty().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumNet() != null && policySummary.get(i).getEndorsementNet() != null) {
						BigDecimal totalNet = policySummary.get(i).getEndorsementNet().add(policySummary.get(i).getPremiumNet());
						cell.setCellValue(totalNet.doubleValue());
					} else if (policySummary.get(i).getPremiumNet() != null && policySummary.get(i).getEndorsementNet() != null) {
						cell.setCellValue(policySummary.get(i).getPremiumNet().doubleValue());
					} else if (policySummary.get(i).getPremiumNet() != null && policySummary.get(i).getEndorsementNet() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementNet().doubleValue());
					}
					cellIdx++;
					
				} else {
					
					row = sheet.createRow(rowIndex);
					int cellIdx = 0;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getCompanyId());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getCompanyCode());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumGross().doubleValue());
					cellIdx++;

					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumRebate().doubleValue());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumTax().doubleValue());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumStampDuty().doubleValue());
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					cell.setCellValue(policySummary.get(i).getPremiumNet().doubleValue());
					cellIdx++;
					
					// ENDORSEMENT
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementGross() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementGross().doubleValue());
					}
					cellIdx++;

					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementRebate()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementRebate().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementTax()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementTax().doubleValue());						
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementTax()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementStampDuty().doubleValue());						
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableBodyStyle);
					if (policySummary.get(i).getEndorsementNet()!=null) {
						cell.setCellValue(policySummary.get(i).getEndorsementNet().doubleValue());
					}
					cellIdx++;
					
					// TOTAL
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumGross() != null && policySummary.get(i).getEndorsementGross() != null) {
						BigDecimal totalGross = policySummary.get(i).getEndorsementGross().add(policySummary.get(i).getPremiumGross());
						cell.setCellValue(totalGross.doubleValue());
					} else if (policySummary.get(i).getPremiumGross() != null && policySummary.get(i).getEndorsementGross() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumGross().doubleValue());
					} else if (policySummary.get(i).getPremiumGross() == null && policySummary.get(i).getEndorsementGross() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementGross().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumRebate() != null && policySummary.get(i).getEndorsementRebate() != null) {
						BigDecimal totalRebate = policySummary.get(i).getEndorsementRebate().add(policySummary.get(i).getPremiumRebate());
						cell.setCellValue(totalRebate.doubleValue());
					} else if (policySummary.get(i).getPremiumRebate() != null && policySummary.get(i).getEndorsementRebate() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumRebate().doubleValue());
					} else if (policySummary.get(i).getPremiumRebate() == null && policySummary.get(i).getEndorsementRebate() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementRebate().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumTax() != null && policySummary.get(i).getEndorsementTax() != null) {
						BigDecimal totalTax = policySummary.get(i).getEndorsementTax().add(policySummary.get(i).getPremiumTax());
						cell.setCellValue(totalTax.doubleValue());
					} else if (policySummary.get(i).getPremiumTax() != null && policySummary.get(i).getEndorsementTax() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumTax().doubleValue());
					} else if (policySummary.get(i).getPremiumTax() == null && policySummary.get(i).getEndorsementTax() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementTax().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumStampDuty() != null && policySummary.get(i).getEndorsementStampDuty() != null) {
						BigDecimal totalTax = policySummary.get(i).getEndorsementStampDuty().add(policySummary.get(i).getPremiumStampDuty());
						cell.setCellValue(totalTax.doubleValue());
					} else if (policySummary.get(i).getPremiumStampDuty() != null && policySummary.get(i).getEndorsementStampDuty() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumStampDuty().doubleValue());
					} else if (policySummary.get(i).getPremiumStampDuty() == null && policySummary.get(i).getEndorsementStampDuty() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementStampDuty().doubleValue());
					}
					cellIdx++;
					
					cell = row.createCell(cellIdx);
					cell.setCellStyle(tableTotalStyle);
					if (policySummary.get(i).getPremiumNet() != null && policySummary.get(i).getEndorsementNet() != null) {
						BigDecimal totalNet = policySummary.get(i).getEndorsementNet().add(policySummary.get(i).getPremiumNet());
						cell.setCellValue(totalNet.doubleValue());
					} else if (policySummary.get(i).getPremiumNet() != null && policySummary.get(i).getEndorsementNet() == null) {
						cell.setCellValue(policySummary.get(i).getPremiumNet().doubleValue());
					} else if (policySummary.get(i).getPremiumNet() == null && policySummary.get(i).getEndorsementNet() != null) {
						cell.setCellValue(policySummary.get(i).getEndorsementNet().doubleValue());
					}
					cellIdx++;
				}
				
				category = policySummary.get(i).getInsuranceClassCategory();				
				rowIndex++;			
			}
			
			fileOut = new FileOutputStream(submission.getFullOutputFile());
			wb.write(fileOut);
			fileOut.close();
			wb.close();
			if (policySummary.size() == 0) {
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

	private void printTableHeader(HSSFSheet sheet, HSSFRow row, HSSFCell cell, int rowIndex, String insuranceClassCategory, HSSFWorkbook wb) {
		
		HSSFCellStyle tableHeaderStyle = style.tableHeaderStyleBlock(wb);
		HSSFCellStyle columnAreaTotalStyle = style.columnAreaTotalStyleBlock(wb);
		
		row = sheet.createRow(rowIndex);
		int rowNum = row.getRowNum() + 1;
		cell = row.createCell(0);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), rowNum, 0, 1 ));
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue(insuranceClassCategory);

		cell = row.createCell(2);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("RENEWAL POLICIES");
		cell = row.createCell(3);
		cell.setCellStyle(tableHeaderStyle);
		cell = row.createCell(4);
		cell.setCellStyle(tableHeaderStyle);
		cell = row.createCell(5);
		cell.setCellStyle(tableHeaderStyle);
		cell = row.createCell(6);
		cell.setCellStyle(tableHeaderStyle);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 2, 6 ));
		cell.setCellStyle(tableHeaderStyle);
		
		cell = row.createCell(7);
		cell.setCellStyle(tableHeaderStyle);
		cell.setCellValue("ENDORSEMENT POLICIES");
		cell = row.createCell(8);
		cell.setCellStyle(tableHeaderStyle);
		cell = row.createCell(9);
		cell.setCellStyle(tableHeaderStyle);
		cell = row.createCell(10);
		cell.setCellStyle(tableHeaderStyle);
		cell = row.createCell(11);
		cell.setCellStyle(tableHeaderStyle);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 7, 11 ));
		cell.setCellStyle(tableHeaderStyle);
		
		cell = row.createCell(12);
		cell.setCellStyle(columnAreaTotalStyle);
		cell.setCellValue("TOTAL");
		cell = row.createCell(13);
		cell.setCellStyle(columnAreaTotalStyle);
		cell = row.createCell(14);
		cell.setCellStyle(columnAreaTotalStyle);
		cell = row.createCell(15);
		cell.setCellStyle(columnAreaTotalStyle);
		cell = row.createCell(16);
		cell.setCellStyle(columnAreaTotalStyle);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 12, 16 ));
		cell.setCellStyle(columnAreaTotalStyle);
		
		rowIndex = rowIndex + 1;
		row = sheet.createRow(rowIndex);
		row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
		
		int tableHeaderIdx = 2;		
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
		
		//ENDORSEMENT
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
		
		//TOTAL
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
		tableHeaderIdx++;		
		rowIndex++;
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
		cell.setCellValue("Premium Tabulation Summary");
		cell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress( row.getRowNum(), row.getRowNum(), 0, 6 ));
	}
	
	/** DATA  ***********************************************************************************/
	private List<PolicyDTO> getPolicySummary() {
		LOGGER.info("Getting All Policy and Endorsement");
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
			sql.append(" UPPER(INSURANCE_CLASS_CATEGORY.NAME) AS CATEGORY, ");
			sql.append(" ROW_NUMBER() OVER (PARTITION BY INSURANCE_CLASS_CATEGORY.NAME) as NUMBER, ");
			sql.append(" COMPANY.CODE AS COMPANY_CODE, ");
			sql.append(" SUM(COALESCE(POLICY.PREMIUM_GROSS,0)) AS POLICY_GROSS, ");
			sql.append(" SUM(COALESCE(POLICY.PREMIUM_REBATE,0)) AS POLICY_REBATE, ");
			sql.append(" SUM(COALESCE(POLICY.PREMIUM_TAX,0)) AS POLICY_TAX, ");
			sql.append(" SUM(COALESCE(POLICY.STAMP_DUTY,0)) AS POLICY_STAMP_DUTY, ");
			sql.append(" SUM(COALESCE(POLICY.PREMIUM_NET,0)) AS POLICY_NET, ");
			sql.append(" SUM(COALESCE(TABLE_2.ENDORSEMENT_GROSS,0)) AS ENDORSEMENT_GROSS, ");
			sql.append(" SUM(COALESCE(TABLE_2.ENDORSEMENT_REBATE,0)) AS ENDORSEMENT_REBATE, ");
			sql.append(" SUM(COALESCE(TABLE_2.ENDORSEMENT_TAX,0)) AS ENDORSEMENT_TAX, ");
			sql.append(" SUM(COALESCE(TABLE_2.ENDORSEMENT_STAMP_DUTY,0)) AS ENDORSEMENT_STAMP_DUTY, ");
			sql.append(" SUM(COALESCE(TABLE_2.ENDORSEMENT_NET,0)) AS ENDORSEMENT_NET ");
		sql.append(" FROM POLICY ");
			sql.append(" LEFT JOIN ( SELECT POLICY_ID, ");
				sql.append(" SUM(COALESCE(ENDORSEMENT.GROSS_PREMIUM,0)) AS ENDORSEMENT_GROSS, ");
				sql.append(" SUM(COALESCE(ENDORSEMENT.REBATE_PREMIUM,0)) AS ENDORSEMENT_REBATE, ");
				sql.append(" SUM(COALESCE(ENDORSEMENT.TAX_AMOUNT,0)) AS ENDORSEMENT_TAX, ");
				sql.append(" SUM(COALESCE(ENDORSEMENT.STAMP_DUTY,0)) AS ENDORSEMENT_STAMP_DUTY, ");
				sql.append(" SUM(COALESCE(ENDORSEMENT.NET_PREMIUM,0)) AS ENDORSEMENT_NET ");
				sql.append(" FROM POLICY ");
				sql.append(" INNER JOIN POLICY_ENDORSEMENT ENDORSEMENT ON ENDORSEMENT.POLICY_ID = POLICY.ID ");
				sql.append(" GROUP BY POLICY_ID ) TABLE_2 ON POLICY.ID = TABLE_2.POLICY_ID ");
			sql.append(" INNER JOIN COMPANY ON COMPANY.ID = POLICY.COMPANY_ID ");
			sql.append(" INNER JOIN INSURANCE_CLASS ON POLICY.INSURANCE_CLASS_CODE = INSURANCE_CLASS.CODE ");
			sql.append(" INNER JOIN INSURANCE_CLASS_CATEGORY ON INSURANCE_CLASS.CATEGORY = INSURANCE_CLASS_CATEGORY.CODE ");
		sql.append(" WHERE 1 = 1 ");
		
		if (companyIds != null ) {
			sql.append(" and company.id in (");
				sql.append("?");
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
		
		if (groupOfInsuranceCode!=null && !groupOfInsuranceCode.isEmpty()) {
			sql.append(" and INSURANCE_CLASS_CATEGORY.code = ? ");
		}
		
		if (insuranceClassCode != null && insuranceClassCode.length > 0) {
			sql.append(" and INSURANCE_CLASS.code in (");
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
			sql.append(" and policy.start_date between ? and ? ");
		} else if (fromLossDate != null && toLossDate == null) {
			sql.append(" and policy.start_date = ? ");
		} else if (fromLossDate == null && toLossDate != null) {
			sql.append(" and policy.end_date = ? ");
		}
			
		sql.append(" GROUP BY INSURANCE_CLASS_CATEGORY.SORT_ORDER, INSURANCE_CLASS_CATEGORY.NAME, COMPANY.ID, COMPANY.CODE ");
		sql.append(" ORDER BY INSURANCE_CLASS_CATEGORY.SORT_ORDER, NUMBER, COMPANY.ID; ");
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
				dto.setCompanyId(rs.getLong(idx));idx++;
				dto.setCompanyCode(rs.getString(idx));idx++;
				dto.setPremiumGross(rs.getBigDecimal(idx));idx++;
				dto.setPremiumRebate(rs.getBigDecimal(idx));idx++;
				dto.setPremiumTax(rs.getBigDecimal(idx));idx++;
				dto.setPremiumStampDuty(rs.getBigDecimal(idx));idx++;
				dto.setPremiumNet(rs.getBigDecimal(idx));idx++;
				dto.setEndorsementGross(rs.getBigDecimal(idx));idx++;
				dto.setEndorsementRebate(rs.getBigDecimal(idx));idx++;
				dto.setEndorsementTax(rs.getBigDecimal(idx));idx++;
				dto.setEndorsementStampDuty(rs.getBigDecimal(idx));idx++;
				dto.setEndorsementNet(rs.getBigDecimal(idx));idx++;
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
	
	/** STYLE ***********************************************************************************/
	private HSSFSheet columnWidth(HSSFWorkbook wb) {
		HSSFSheet sheet = wb.createSheet();
		int cellIdx = 0;
		sheet.setColumnWidth(cellIdx, 2000);cellIdx++; // ID
		sheet.setColumnWidth(cellIdx, 4000);cellIdx++; // CODE
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // POLICY GROSS
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // POLICY REBATE
		sheet.setColumnWidth(cellIdx, 4000);cellIdx++; // POLICY TAX
		sheet.setColumnWidth(cellIdx, 3800);cellIdx++; // POLICY STAMP DUTY
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // POLICY NET
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // ENDROSEMENT GROSS
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // ENDROSEMENT REBATE
		sheet.setColumnWidth(cellIdx, 4000);cellIdx++; // ENDROSEMENT TAX
		sheet.setColumnWidth(cellIdx, 3800);cellIdx++; // ENDROSEMENT DUTY
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // ENDROSEMENT NET
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // TOTAL GROSS
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // TOTAL REBATE
		sheet.setColumnWidth(cellIdx, 4000);cellIdx++; // TOTAL TAX
		sheet.setColumnWidth(cellIdx, 3800);cellIdx++; // TOTAL DUTY
		sheet.setColumnWidth(cellIdx, 6500);cellIdx++; // TOTAL NET
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
	
	class PolicyDTO {
		private String companyCode;
		private Long companyId;
		private String insuranceClassCategory;
		
		private BigDecimal premiumGross;
		private BigDecimal premiumRebate;
		private BigDecimal premiumTax;
		private BigDecimal premiumStampDuty;
		private BigDecimal premiumNet;
		
		private BigDecimal endorsementGross;
		private BigDecimal endorsementRebate;
		private BigDecimal endorsementTax;
		private BigDecimal endorsementStampDuty;
		private BigDecimal endorsementNet;
		
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
		public String getInsuranceClassCategory() {
			return insuranceClassCategory;
		}
		public void setInsuranceClassCategory(String insuranceClassCategory) {
			this.insuranceClassCategory = insuranceClassCategory;
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
		public BigDecimal getPremiumStampDuty() {
			return premiumStampDuty;
		}
		public void setPremiumStampDuty(BigDecimal premiumStampDuty) {
			this.premiumStampDuty = premiumStampDuty;
		}
		public BigDecimal getPremiumNet() {
			return premiumNet;
		}
		public void setPremiumNet(BigDecimal premiumNet) {
			this.premiumNet = premiumNet;
		}
		public BigDecimal getEndorsementGross() {
			return endorsementGross;
		}
		public void setEndorsementGross(BigDecimal endorsementGross) {
			this.endorsementGross = endorsementGross;
		}
		public BigDecimal getEndorsementRebate() {
			return endorsementRebate;
		}
		public void setEndorsementRebate(BigDecimal endorsementRebate) {
			this.endorsementRebate = endorsementRebate;
		}
		public BigDecimal getEndorsementTax() {
			return endorsementTax;
		}
		public void setEndorsementTax(BigDecimal endorsementTax) {
			this.endorsementTax = endorsementTax;
		}
		public BigDecimal getEndorsementStampDuty() {
			return endorsementStampDuty;
		}
		public void setEndorsementStampDuty(BigDecimal endorsementStampDuty) {
			this.endorsementStampDuty = endorsementStampDuty;
		}
		public BigDecimal getEndorsementNet() {
			return endorsementNet;
		}
		public void setEndorsementNet(BigDecimal endorsementNet) {
			this.endorsementNet = endorsementNet;
		}
	}
}
