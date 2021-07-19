package com.example.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.example.config.Environment;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory {

	public WebDriverFactory() {
		WebDriverManager.chromedriver().setup();
		WebDriverManager.edgedriver().setup();
		WebDriverManager.iedriver().driverVersion("3.150.1").arch32().setup();
	}
	
	public WebDriver lauchChrome() {
		ChromeOptions options = new ChromeOptions();
		if (Environment.headless)
			options.addArguments("--headless", "--window-size=1920,1200");
		return new ChromeDriver(options);
	}
	
	public WebDriver lauchEdge() {
		EdgeOptions edgeOptions = new EdgeOptions();
		return new EdgeDriver(edgeOptions);
	}
	
	public WebDriver lauchIE() {
		return new InternetExplorerDriver();
	}
}
