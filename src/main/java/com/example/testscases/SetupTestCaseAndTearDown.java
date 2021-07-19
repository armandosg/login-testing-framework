package com.example.testscases;

import com.example.common.*;
import com.example.config.Environment;
import com.example.models.TestData;
import com.example.models.TestDataLoginPage;
import com.example.models.TestResult;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * This class adds basic functionality to all test cases
 * @author Armando
 *
 */
@Log4j2
public abstract class SetupTestCaseAndTearDown {
	
	/**
	 * Selenium web driver to control the browser
	 */
	protected WebDriver driver;

	protected TestDataLoginPage testData;

	/**
	 * Name of the class that contains the Test
	 */
	private String testCaseClass;

	/**
	 * Setup environment instance, read data file and init write results file
	 */
	@BeforeSuite
	public void beforeSuite() {
		Environment.startTime = LocalDateTime.now();
		log.info("Start time: " + Environment.startTime);
		try {
			log.info("Executing beforeSuite() method");

			DataFileReader fileReader = DataFileReader.getInstance();
			fileReader.init(Environment.dataFilePath, Environment.envName);
			fileReader.populateListTestData();
			fileReader.close();

			ResultsFileWriter resultsFileWriter = ResultsFileWriter.getInstance();
			resultsFileWriter.init(Environment.resultsFilePath, Environment.envName);
		} catch (IOException e) {
			log.error(e);
		}
	}

	@BeforeClass
	public void getTestCaseClass() {
		testCaseClass = this.getClass().getSimpleName();
		log.info("Executing getTestCaseClass() method for " + testCaseClass);
	}

	/**
	 * Get testData and check if runFlag is set to true. If it is not, then throw an SkipException
	 */
	@BeforeMethod
	public void beforeMethod(Method method) {
		String testCaseId = String.format("%s.%s", testCaseClass, method.getName());
		log.info("Executing beforeMethod() method for " + testCaseId);
		DataFileReader fileReader = DataFileReader.getInstance();
		testData = fileReader.getListTestData()
				.stream()
				.filter(t -> t.getTestCaseId().equals(testCaseId))
				.filter(TestData::isRunFlag)
				.findFirst().orElse(null);
		if (testData == null) {
			log.info(testCaseId + " does not exist on this data sheet or is not marked with Y on the run flag");
			throw new SkipException(
					testCaseId + " does not exist on this data sheet or is not marked with Y on the run flag");
		}
		setupWebDriver(testData);
		driver.manage().window().maximize();
	}

	/**
	 * Get testData and check if runFlag is set to true. If it is not, then throw an SkipException
	 */
	@AfterMethod
	public void afterMethod(ITestResult result) {
		log.info("Executing afterMethod() for:" + testData.getApplicationName());

		log.info("Result: " + (result.isSuccess() ? "PASS" : "FAIL"));

		log.info("Take screenshot");
		File screenshot = takeScreenshotAndSaveIt(testData);

		TestResult testResult = setupTestResult(testData, result.isSuccess(), screenshot);

		ResultsFileWriter fileWriter = ResultsFileWriter.getInstance();
		fileWriter.createResultRow(testResult);

		log.info("Close driver");
		driver.close();
	}

	/**
	 * Close results file and copy logs to Results folder
	 */
	@AfterSuite
	public void afterSuite() {
		log.info("Executing afterSuite() method");
		ResultsFileWriter resultsFileWriter = ResultsFileWriter.getInstance();
		try {
			resultsFileWriter.writeAndClose();
			File log4jLogs = new File(Environment.log4jLogs);
			FileUtils.copyDirectory(log4jLogs, new File(Environment.logsBasePath));
			FileUtils.deleteDirectory(log4jLogs);
		} catch (IOException e) {
			log.error(e);
		}
		if (Environment.sendEmail) {
			EmailHelper emailHelper =
					new EmailHelper(
							resultsFileWriter.getTestResults(), Environment.startTime.toString(), LocalDateTime.now().toString());
			emailHelper.sendEmail();
		}
		log.info("End time : " + LocalDateTime.now());
	}

	/**
	 * Takes the screenshot of the current page and saves it to the proper path
	 * @param testData The data that will be used for test
	 * @return Screenshot taken
	 */
	private File takeScreenshotAndSaveIt(TestData testData) {
		ScreenshotHelper helper = new ScreenshotHelper(driver);
		File screenshot = helper.takeScreenshot();
		log.info("Save screenshot to screenshots folder");
		screenshot = helper.saveScreenshot(screenshot, helper.getScreenshotFilePath(testData));
		return screenshot;
	}
	
	/**
	 * Setup and launch the required driver specified by the TestData object
	 * @param testData The data that will be used for test
	 */
	private void setupWebDriver(TestData testData) {
		log.info("Setting up driver");
		WebDriverFactory factory = new WebDriverFactory();
		switch (testData.getBrowser()) {
			case Environment.Browser.CHROME -> driver = factory.lauchChrome();
			case Environment.Browser.EDGE -> driver = factory.lauchEdge();
			case Environment.Browser.IE -> driver = factory.lauchIE();
			default -> throw new IllegalArgumentException("Unexpected value: " + testData.getBrowser());
		}
	}

	/**
	 * Creates and returns a TestResult object with the provided information 
	 * @param testData The data that will be used for test
	 * @param passed Tells weather the test passed or not
	 * @param screenshot The file containing the screenshot of the result
	 * @return TestResult object
	 */
	private TestResult setupTestResult(TestData testData, boolean passed, File screenshot) {
		return TestResult.builder()
				.browser(testData.getBrowser())
				.testResult(passed)
				.testScenario(testData.getTestScenario())
				.url(testData.getURL())
				.screenshot(screenshot)
				.applicationName(testData.getApplicationName())
				.build();
	}
}
