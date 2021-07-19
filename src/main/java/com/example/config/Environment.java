package com.example.config;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Properties;

@Log4j2
public class Environment {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public static final String dateTimestamp;

	public static final String CONFIG_FILE_NAME = "config.properties";

	private static final String CONFIG_KEY_DATA_FILE = "dataFile";

	private static final String CONFIG_KEY_RESULTS_PATH = "resultsPath";

	private static final String CONFIG_KEY_ENV_NAME = "env";

	private static final String CONFIG_KEY_HEADLESS = "headless";

	private static final String CONFIG_KEY_SEND_EMAIL = "sendEmail";

	private static final String CONFIG_KEY_WAIT = "wait";

	private static final String CONFIG_TEST_SUITE_FILE = "testSuiteFile";

	public static boolean headless = true;

	public static boolean sendEmail = false;

	public static int wait = 40;

	public static String envName = "staging";

	public static String dataFilePath = "./test_data.example.xlsx";

	public static String resultsFilePath = "%s/test_results_%s.xlsx";
	
	public static String screenshotBasePath = "%s/Screenshots";
	
	public static final String screenshotFilePath = "%s/%s-%s-%s.png";

	public static String logsBasePath = "%s/logs";

	public static final String log4jLogs = "./logs";

	public static String resultsBasePath = "Results/";

	public static String screenshotsZipFilePath;

	public static String testSuiteFile = "testng.xml";

	public static LocalDateTime startTime;

	public static class Browser {
		public static final String CHROME = "Chrome";
		public static final String EDGE = "Edge";
		public static final String IE = "IE";
	}

	static {
		InputStream configFileInputStream = Environment.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
		Properties configProps = new Properties();
		try {
			configProps.load(configFileInputStream);
			envName = configProps.getProperty(CONFIG_KEY_ENV_NAME, "staging");
			headless = configProps.getProperty(CONFIG_KEY_HEADLESS, "yes").equals("yes");
			dataFilePath = configProps.getProperty(CONFIG_KEY_DATA_FILE, "./test_data.example.xlsx");
			resultsBasePath = configProps.getProperty(CONFIG_KEY_RESULTS_PATH, "Results/");
			if (configFileInputStream != null) {
				configFileInputStream.close();
			}
			sendEmail = configProps.getProperty(CONFIG_KEY_SEND_EMAIL, "no").equals("yes");
			wait = Integer.parseInt(configProps.getProperty(CONFIG_KEY_WAIT, "40"));
			testSuiteFile = configProps.getProperty(CONFIG_TEST_SUITE_FILE, "testng.xml");
		} catch (NullPointerException e) {
			log.info("config.properties file does not exists, using default values", e);
		} catch (IOException e) {
			log.info("error while reading config.properties, using default values ", e);
		}

		log.info("Set up for " + envName + " environment");
		log.info("Headless mode: " + (headless ? "ON" : "OFF"));
		log.info("Path to data file: " + dataFilePath);
		log.info("Path to results folder: " + resultsBasePath);
		log.info("Send email: " + (sendEmail ? "ON" : "OFF"));
		log.info("Wait time: " + wait + " seconds");
		log.info("Test suite file: " + testSuiteFile);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		dateTimestamp = SDF.format(timestamp);

		char lastChar = resultsBasePath.charAt(resultsBasePath.length() - 1);
		resultsBasePath = String.format(lastChar == '/' ? "%s%s" : "%s/%s", resultsBasePath, dateTimestamp);
		resultsFilePath = String.format(resultsFilePath, resultsBasePath, dateTimestamp);
		File file = new File(resultsBasePath);
		if (file.mkdirs()) {
			log.info(resultsBasePath + " created");
		}

		screenshotBasePath = String.format(screenshotBasePath, resultsBasePath);
		file = new File(screenshotBasePath);
		if (file.mkdirs()) {
			log.info(screenshotBasePath + " created");
		}

		logsBasePath = String.format(logsBasePath, resultsBasePath);
		file = new File(logsBasePath);
		if (file.mkdirs()) {
			log.info(logsBasePath + " created");
		}

		String screenshotsZipFileName = "Screenshots.zip";
		screenshotsZipFilePath =
				String.format("%s/%s", resultsBasePath, screenshotsZipFileName);
	}
}
