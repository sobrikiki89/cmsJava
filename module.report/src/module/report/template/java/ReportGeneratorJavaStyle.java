package module.report.template.java;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class ReportGeneratorJavaStyle {

	public HSSFCellStyle tableEmptyBodyBlock(HSSFWorkbook wb) {
		HSSFCellStyle tableBodyStyle = wb.createCellStyle();
		HSSFFont tableBodyFont = wb.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		tableBodyFont.setFontName("SansSerif");
		tableBodyStyle.setFont(tableBodyFont);
		tableBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tableBodyStyle.setWrapText(true);
		tableBodyStyle.setBorderLeft(BorderStyle.THIN);
		tableBodyStyle.setBorderRight(BorderStyle.THIN);
		return tableBodyStyle;
	}
	
	public HSSFCellStyle tableBodyDetailStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle tableBodyStyle = wb.createCellStyle();
		HSSFFont tableBodyFont = wb.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		tableBodyFont.setFontName("SansSerif");
		tableBodyStyle.setFont(tableBodyFont);
		tableBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tableBodyStyle.setWrapText(true);
		tableBodyStyle.setBorderTop(BorderStyle.THIN);
		tableBodyStyle.setBorderLeft(BorderStyle.THIN);
		tableBodyStyle.setBorderRight(BorderStyle.THIN);
		tableBodyStyle.setBorderBottom(BorderStyle.THIN);
		return tableBodyStyle;
	}
	
	public HSSFCellStyle tableBodyStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle tableBodyStyle = wb.createCellStyle();
		HSSFFont tableBodyFont = wb.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		tableBodyFont.setFontName("SansSerif");
		tableBodyStyle.setFont(tableBodyFont);
		tableBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tableBodyStyle.setWrapText(true);
		tableBodyStyle.setBorderTop(BorderStyle.THIN);
		tableBodyStyle.setBorderLeft(BorderStyle.THIN);
		tableBodyStyle.setBorderRight(BorderStyle.THIN);
		return tableBodyStyle;
	}
	
	public HSSFCellStyle tableBodyStyleDecimalBlock(HSSFWorkbook wb) {
		HSSFCellStyle tableBodyStyle = wb.createCellStyle();
		HSSFFont tableBodyFont = wb.createFont();
		tableBodyFont.setFontHeightInPoints((short) 10);
		tableBodyFont.setFontName("SansSerif");
		tableBodyStyle.setFont(tableBodyFont);
		tableBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tableBodyStyle.setWrapText(true);
		tableBodyStyle.setBorderTop(BorderStyle.THIN);
		tableBodyStyle.setBorderLeft(BorderStyle.THIN);
		tableBodyStyle.setBorderRight(BorderStyle.THIN);
		tableBodyStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
		return tableBodyStyle;
	}

	public HSSFCellStyle columnAreaTotalStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBold(true);
		headerFont.setFontName("SansSerif");
		headerFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(headerFont);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}
		
	public HSSFCellStyle headerStyleBlock(HSSFWorkbook wb) {
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
	
	public HSSFCellStyle tableHeaderStyleBlock(HSSFWorkbook wb) {
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
	
	// DOUBLE
	public HSSFCellStyle tableSubTotalStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont subTotalFontFont = wb.createFont();
		subTotalFontFont.setFontHeightInPoints((short) 10);
		subTotalFontFont.setFontName("SansSerif");
		subTotalFontFont.setBold(false);
		subTotalFontFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(subTotalFontFont);
//		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
		return style;
	}
	
	public HSSFCellStyle tableTotalStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont subTotalFontFont = wb.createFont();
		subTotalFontFont.setFontHeightInPoints((short) 10);
		subTotalFontFont.setFontName("SansSerif");
		subTotalFontFont.setBold(true);
		subTotalFontFont.setColor(HSSFColorPredefined.BLACK.getIndex());
		style.setFont(subTotalFontFont);
//		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillForegroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
		return style;
	}
	
	public HSSFCellStyle tableFooterStyleBlock(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont tableFooterFont = wb.createFont();
		tableFooterFont.setFontHeightInPoints((short) 10);
		tableFooterFont.setFontName("SansSerif");
		tableFooterFont.setBold(true);
		tableFooterFont.setColor(IndexedColors.BLACK.getIndex());
		style.setFont(tableFooterFont);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}
}
