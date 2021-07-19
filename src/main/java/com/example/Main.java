package com.example;

import com.example.config.Environment;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

import java.util.List;

/**
 * This is the main class
 * @author Armando
 *
 */
public class Main {
	
	/**
	 * This is the main method
	 * @param args Arguments of execution
	 */
	public static void main(String[] args) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		suites.add(Environment.testSuiteFile);
		testng.setTestSuites(suites);
		testng.run();
	}

}
