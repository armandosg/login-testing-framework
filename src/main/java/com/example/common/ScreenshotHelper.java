package com.example.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.example.config.Environment;
import com.example.models.TestData;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * This class helps in handling screenshot actions
 * @author Armando
 *
 */
@AllArgsConstructor
@Log4j2
public class ScreenshotHelper {
	
	/**
	 * Helps to control the browser
	 */
	private WebDriver driver;

	/**
	 * Take screenshot of the current page and return the screenshot object
	 * @return screenshot file
	 */
	public File takeScreenshot() {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	}
	
	/**
	 * Save the screenshot to the specified file path
	 * @param screenshot screenshot file
	 * @param filePath path in which the screenshot file will be saved to
	 */
	public File saveScreenshot(File screenshot, String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			FileUtils.copyFile(screenshot, file);
        } catch (IOException e) {
            log.error(e);
        }
		return file;
	}
	
	/**
	 * Return the appropriate file path for an specific environment and TestData
	 *
	 * @param testData provides the information to generate the path to the screenshot file
	 * @return the path correspondent to the information provided by the testData parameter
	 */
	public String getScreenshotFilePath(TestData testData) {
		return String.format(Environment.screenshotFilePath,
				Environment.screenshotBasePath,
				testData.getTestScenario().replace('/', '_'), 
				testData.getApplicationName().replace('/', '_'), 
				testData.getBrowser().replace('/', '_'));
	}
	
}
