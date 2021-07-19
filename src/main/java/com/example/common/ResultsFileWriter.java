package com.example.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.models.TestDataLoginPage;
import lombok.Getter;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.models.TestResult;
import lombok.extern.log4j.Log4j2;

/**
 * This class helps to write the results objects in the test_results_yyyy-MM-dd-HH-mm-ss.xlsx
 * @author Armando
 *
 */
@Log4j2
public class ResultsFileWriter {
	
	/**
	 * FileOutputStream to write to spreadsheet
	 */
	private FileOutputStream outputStream;
	
	private XSSFWorkbook wb;
	
	private XSSFSheet resultsSheet;
	
	private static ResultsFileWriter resultsFileWriter;

	private @Getter List<TestResult> testResults;
	
	/**
	 * Get the instance of ResultsFileWriter. It creates a new one if it is set to null.
	 * @return The instance of ResultsFileWriter
	 */
	public static ResultsFileWriter getInstance() {
		if (resultsFileWriter == null)
			resultsFileWriter = new ResultsFileWriter();
		return resultsFileWriter;
	}
	
	/**
	 * Create workbook and new sheet as well as open an output stream.
	 * @param filePath
	 * @param sheetName
	 * @throws FileNotFoundException
	 */
	public void init(String filePath, String sheetName) throws FileNotFoundException {
		wb = new XSSFWorkbook();
		resultsSheet = wb.createSheet(sheetName);
		outputStream = new FileOutputStream(filePath);
		testResults = new ArrayList<>();
	}
	
	/**
	 * Create one result row into the specified sheet
	 */
	public synchronized void createResultRow(TestResult result) {
		try {
			int lastRow = resultsSheet.getLastRowNum();
			
			if (lastRow == -1)
				createHeadersRow(resultsSheet);
			
			lastRow = resultsSheet.getLastRowNum();
			createTestResultRow(lastRow + 1, result, resultsSheet);
			testResults.add(result);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	/**
	 * Write all changes into workbook and close the workbook
	 * @throws IOException 
	 */
	public void writeAndClose() throws IOException {
		wb.write(outputStream);
		wb.close();
	}
	
	/**
	 * Write each item of the testResults list into the specified sheet
	 * 
	 * @param testResults
	 */
	public synchronized void createResultRow(List<TestResult> testResults) {
		try {
			int lastRow = resultsSheet.getLastRowNum();
			
			if (lastRow == -1)
				createHeadersRow(resultsSheet);
			
			lastRow = resultsSheet.getLastRowNum();
			
			int i = lastRow + 1;
			for (TestResult testResult : testResults) {
				log.info("Write result of " + testResult);
				createTestResultRow(i, testResult, resultsSheet);				
				i ++;
				testResults.add(testResult);
			}
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	/**
	 * Create the headers row in the first row of the specified sheet
	 * @param resultsSheet
	 */
	private void createHeadersRow(XSSFSheet resultsSheet) {
		XSSFRow newRow = resultsSheet.createRow(0);
		newRow.createCell(0).setCellValue("Application Name");
		newRow.createCell(1).setCellValue("URL");
		newRow.createCell(2).setCellValue("Result");
	}
	
	/**
	 * Creates a new row on the specified sheet and row number.
	 * The new row will have all data provided by the TestResult object
	 * 
	 * @param rowNum Row number where the row is going to be created
	 * @param testResult Contains the information that is going to be written in the new row
	 * @param resultsSheet Sheet name where the row is going to be created
	 * @throws IOException 
	 */
	private void createTestResultRow(int rowNum, TestResult testResult, XSSFSheet resultsSheet) throws IOException {
		XSSFRow newRow = resultsSheet.createRow(rowNum);
		newRow.createCell(0).setCellValue(testResult.getApplicationName());
		newRow.createCell(1).setCellValue(testResult.getUrl());
		XSSFCell cell = newRow.createCell(2);
		cell.setCellValue(testResult.isTestResult() ? "PASS" : "FAIL");
		insertHyperlinkToScreenshot(testResult, cell);
	}

	private void insertHyperlinkToScreenshot(TestResult testResult, XSSFCell cell) {
		XSSFWorkbook workbook = cell.getSheet().getWorkbook();
		
		// Create Helpers
		CreationHelper helper = workbook.getCreationHelper();
	    XSSFCellStyle linkStyle = workbook.createCellStyle();
	    XSSFFont linkFont = workbook.createFont();
		XSSFHyperlink link = (XSSFHyperlink)helper.createHyperlink(HyperlinkType.URL);
		
		// Setting the Link Style
		linkFont.setUnderline(XSSFFont.U_SINGLE);
        linkFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        linkStyle.setFont(linkFont);
        
        // Adding a Link
        String path = testResult.getScreenshot().toURI().toString();
        path = path.replace('\\', '/');
        link.setAddress(path);
        cell.setHyperlink((XSSFHyperlink)link);
        cell.setCellStyle(linkStyle);
	}
}
