package com.example.models;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class models one row of the test_results_yyyy-MM-dd-HH-mm-ss.xlsx
 * @author Armando
 *
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResult {
	
	/**
	 * URL of the login page of the application
	 */
	private String url;
	
	/**
	 * Description of the test scenario
	 */
	private String testScenario;
	
	/**
	 * Name of the application
	 */
	private String applicationName;
	
	/**
	 * Name of the browser where the test case has been executed
	 */
	private String browser;
	
	/**
	 * If set to true the test case has passed
	 */
	private boolean testResult;
	
	/**
	 * Screenshot of the application after the execution of all test case steps
	 */
	private File screenshot;
}
