package com.example.common;

import com.example.models.TestDataLoginPage;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class helps on the reading of the test_data file
 * @author Armando
 *
 */
@Log4j2
public class DataFileReader {

	private List<TestDataLoginPage> listTestData;
	
	private XSSFWorkbook wb;
	
	private XSSFSheet envSettingsSheet;
	
	private static DataFileReader INSTANCE;
	
	private DataFileReader() {
		
	}
	
	/**
	 * Get the instance of ResultsFileWriter. It creates a new one if it is set to null.
	 * @return The instance of ResultsFileWriter
	 */
	public static DataFileReader getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DataFileReader();
		return INSTANCE;
	}
	
	/**
	 * Create workbook and new sheet as well as open an input stream.
	 * @param filePath Path of the file where the workbook will be stored
	 * @param sheetName Name of the sheet where all result are written
	 * @throws IOException Throws this if the file is corrupted
	 */
	public void init(String filePath, String sheetName) throws IOException {
		listTestData = new ArrayList<>();
		FileInputStream fileInputStream = new FileInputStream(filePath);
		wb = new XSSFWorkbook(fileInputStream);
		envSettingsSheet = wb.getSheet(sheetName);
	}
	
	/**
	 * Read each row of the test_data file and return each one as a
	 * TestDataLoginPage object on a List
	 */
	public void populateListTestData() {
		log.info("Read test data");
		for (Row row : envSettingsSheet) {
			if (isHeadersRow(row))
				continue;
			TestDataLoginPage testData = decodeRow(row);
			if (testData.isRunFlag())
				listTestData.add(testData);
		}
	}

	/**
	 * Get the data list for testing
	 * @return Returns the data list
	 */
	public synchronized List<TestDataLoginPage> getListTestData() {
		return listTestData;
	}
	
	/**
	 * Close the workbook
	 * @throws IOException Throws this if the workbook is corrupted
	 */
	public void close() throws IOException {
		wb.close();
	}

	/**
	 * Check if the first row of the sheet contains the headers
	 * 
	 * @param row Row to check if it is the headers row
	 * @return Returns true is is the headers row and false if it isn't
	 */
	private boolean isHeadersRow(Row row) throws NullPointerException {
		return getStringCellValue(row, 0).equals("Test Case Id");
	}

	/**
	 * Converts a row object into a TestDataLoginPage object
	 * 
	 * @param row This is the row to be converted to TestDataLoginPage object
	 * @return Returns the converted row in form of a TestDataLoginPage object
	 */
	private TestDataLoginPage decodeRow(Row row) throws NullPointerException {
		String testCaseId = getStringCellValue(row, 0);
		String testScenario = getStringCellValue(row, 1);
		String applicationName = getStringCellValue(row, 2);
		String url = getStringCellValue(row, 3);
		String browser = getStringCellValue(row, 4);
		boolean runFlag = getStringCellValue(row, 5).equals("Y");
		String userId = getStringCellValue(row, 6);
		String password = getStringCellValue(row, 7);
		
		TestDataLoginPage testData = new TestDataLoginPage();
		testData.setTestCaseId(testCaseId);
		testData.setTestScenario(testScenario);
		testData.setApplicationName(applicationName);
		testData.setURL(url);
		testData.setBrowser(browser);
		testData.setRunFlag(runFlag);
		testData.setUserId(userId);
		testData.setPassword(password);
		log.debug(testData);
		return testData;
	}

	/**
	 * It returns the value of the cell. If the value is of type int it will convert it to String
	 * @param row Row that contains the value
	 * @param cellNum Cell number of the row
	 * @return Returns the cell value in String type
	 * @throws NullPointerException Throws this if the row is null
	 */
	private String getStringCellValue(Row row, int cellNum) {
		String value;
		try {
			value = row.getCell(cellNum).getStringCellValue();
		} catch (IllegalStateException e) {
			value = String.valueOf((int) row.getCell(cellNum).getNumericCellValue());
		} catch (NullPointerException e) {
			value = "";
		}
		return value;
	}
}
