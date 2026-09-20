package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.orangehrm.actiondriver.ActionDriver;

public class BaseClass {

	protected static Properties prop;

	// ThreadLocal instances to manage drivers safely across parallel threads
	public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	public static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();

	// Load the configuration file
	@BeforeSuite(alwaysRun = true)
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream file = new FileInputStream("src/main/resources/config.properties");
		prop.load(file);
	}

	// Configuration hook stabilized for sequential groups execution blocks
	@BeforeMethod (alwaysRun = true)
	public synchronized void setup() throws IOException {
		System.out.println("Setting up WebDriver for : " + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);

		// Initialize the Action Driver only for The Current Thread
		actionDriver.set(new ActionDriver(driver.get()));
	}

	// Initialize the WebDriver based on browser define in config.properties file
	private synchronized void launchBrowser() {
		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			driver.set(new ChromeDriver()); // New instance of ChromeDriver for the current thread
		} else if (browser.equalsIgnoreCase("firefox")) {
			driver.set(new FirefoxDriver()); // New instance of FirefoxDriver for the current thread
		} else if (browser.equalsIgnoreCase("edge")) {
			driver.set(new EdgeDriver()); // New instance of EdgeDriver for the current thread
		} else {
			throw new IllegalArgumentException("Browser Not Supported " + browser);
		}
	}

	private void configureBrowser() {
		// Implicit wait
		int imlicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(imlicitWait));

		// Maximize The Browser
		getDriver().manage().window().maximize();

		// Navigate to url
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to Navigate to URL" + e.getMessage());
		}
	}

	// Capture screenshot using Allure programmatic API before cleaning thread context wrappers
	@AfterMethod (alwaysRun = true)
	public synchronized void tearDown(ITestResult result) {

		// 1. Capture screenshot using Allure's direct programmatic API if the test fails
		if (result.getStatus() == ITestResult.FAILURE) {
			try {
				if (driver.get() != null) {
					System.out.println("BaseClass: Direct injection of failure screenshot for: " + result.getName());

					// Capture bytes from browser
					byte[] screenshotBytes = ((org.openqa.selenium.TakesScreenshot) getDriver()).getScreenshotAs(org.openqa.selenium.OutputType.BYTES);

					// Directly push into the active Allure report stream
					io.qameta.allure.Allure.addAttachment("Failure Screenshot", new java.io.ByteArrayInputStream(screenshotBytes));
				}
			} catch (Exception e) {
				System.out.println("BaseClass: Programmatic attachment failed: " + e.getMessage());
			}
		}

		// 2. Clear browser contexts normally safely
		try {
			if (driver.get() != null) {
				driver.get().quit();
				System.out.println("WebDriver instance is close.");
			}
		} catch (Exception e) {
			System.out.println("unable to quit the driver" + e.getMessage());
		} finally {
			// Thread cleanups to prevent memory leaks or context overlaps
			driver.remove();
			actionDriver.remove();
			System.out.println("ActionDriver instance is close.");
		}
	}

	// Getter method for prop
	public static Properties getProp() {
		return prop;
	}

	// Getter Method for WebDriver with safe null return configuration
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			return null;
		}
		return driver.get();
	}

	// Getter Method for ActionDriver
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			return null;
		}
		return actionDriver.get();
	}

	// Driver setter Method
//	public void setDriver(ThreadLocal<WebDriver> driver) {
//		this.driver = driver;
//	}

	// static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
