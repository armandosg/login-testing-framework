package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class models one row of the test_data.xlsx file
 * The test data provides information on what is going to be tested
 * @author Armando
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestData {

	/**
	 * Description of the test scenario
	 */
	private String testCaseId;

	/**
	 * Description of the test scenario
	 */
	private String testScenario;
	
	/**
	 * Name of the application
	 */
	protected String applicationName;
	
	/**
	 * URL of the login page of the application
	 */
	private String URL;
	
	/**
	 * Name of the browser where the test case will get executed
	 */
	private String browser;
	
	/**
	 * If set to true the test case will be run
	 */
	private boolean runFlag;
	
	/**
	 * User credentials
	 */
	private String userId;
	
	/**
	 * User credentials
	 */
	private String password;
}
