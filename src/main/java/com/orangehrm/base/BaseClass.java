package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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


	// NEW FIX: Load configuration file with correct path separator and try-catch for complete stacktrace visibility in Jenkins logs
	@BeforeSuite(alwaysRun = true)
	public void loadConfig() throws IOException {
		prop = new Properties();
		String filePath = System.getProperty("user.dir") + "/src/main/resources/config.properties";
		try {
			FileInputStream file = new FileInputStream(filePath);
			prop.load(file);
			System.out.println("BaseClass: Config properties file loaded successfully from path: " + filePath);
		} catch (Exception e) {
			System.out.println("BaseClass ERROR: Unable to load config.properties from path -> " + filePath);
			e.printStackTrace();
			throw e; // Rethrow to let TestNG report configuration failures accurately
		}
	}


	// NEW FIX: Wrapped setup execution inside explicit try-catch block to log detailed exceptions in Jenkins Console output
	@BeforeMethod(alwaysRun = true)
	public synchronized void setup() throws IOException {
		try {
			System.out.println("Setting up WebDriver for : " + this.getClass().getSimpleName());
			launchBrowser();
			configureBrowser();
			staticWait(2);

			// Initialize the Action Driver only for The Current Thread
			actionDriver.set(new ActionDriver(driver.get()));
		} catch (Exception e) {
			System.out.println("BaseClass ERROR: Exception occurred inside @BeforeMethod setup: " + e.getMessage());
			e.printStackTrace();
			throw e; // Rethrow exception so TestNG catches configuration failures
		}
	}

	// Initialize the WebDriver based on browser define in config.properties file
	private synchronized void launchBrowser() {
		String browser = prop.getProperty("browser");

		if (browser == null) {
			throw new IllegalArgumentException("Browser property is null! Please check key 'browser' in config.properties file.");
		}

		if (browser.equalsIgnoreCase("chrome")) {
			// Create ChromeOptions instance to pass browser configuration arguments
			ChromeOptions options = new ChromeOptions();

			// Configure Chrome for headless execution
//			options.addArguments("--headless=new"); // Run Chrome in headless mode (without GUI)
			options.addArguments("--no-sandbox"); // necessary when running Chrome as the 'jenkins' service user
			options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems in shared memory
																// environments (prevents Chrome crashes)
			options.addArguments("--disable-gpu"); // Disable GPU hardware acceleration to avoid driver compatibility
													// issues in headless mode
			options.addArguments("--window-size=1920,1080"); // Set explicit browser viewport size to ensure elements
																// load properly without UI scaling issues


			// NEW FIX: Create ChromeDriver instance with options for thread-safe assignment
			driver.set(new ChromeDriver(options));
		} else if (browser.equalsIgnoreCase("firefox")) {

			FirefoxOptions options = new FirefoxOptions();
			options.addArguments("-headless");
			options.addArguments("--width=1920");
			options.addArguments("--height=1080");
			driver.set(new FirefoxDriver(options)); // New instance of FirefoxDriver for the current thread
		} else if (browser.equalsIgnoreCase("edge")) {
			// Configure Edge for headless execution
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless=new");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-gpu");
			options.addArguments("--window-size=1920,1080");
			driver.set(new EdgeDriver(options)); // New instance of EdgeDriver for the current thread
		} else {
			throw new IllegalArgumentException("Browser Not Supported " + browser);
		}
	}

	private void configureBrowser() {
		// Implicit wait
		int imlicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(imlicitWait));

		/* ==================== OLD CODE COMMENTED OUT ====================
		// Maximize The Browser
		// getDriver().manage().window().maximize();
		================================================================= */

		// NEW COMMENT: Avoid maximize() in headless mode as viewport resolution is already set via window-size options argument

		// Navigate to url
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to Navigate to URL" + e.getMessage());
		}
	}

	// Capture screenshot using Allure programmatic API before cleaning thread
	// context wrappers
	@AfterMethod(alwaysRun = true)
	public synchronized void tearDown(ITestResult result) {

		// 1. Capture screenshot using Allure's direct programmatic API if the test
		// fails
		if (result.getStatus() == ITestResult.FAILURE) {
			try {
				if (driver.get() != null) {
					System.out.println("BaseClass: Direct injection of failure screenshot for: " + result.getName());

					// Capture bytes from browser
					byte[] screenshotBytes = ((org.openqa.selenium.TakesScreenshot) getDriver())
							.getScreenshotAs(org.openqa.selenium.OutputType.BYTES);

					// Directly push into the active Allure report stream
					io.qameta.allure.Allure.addAttachment("Failure Screenshot",
							new java.io.ByteArrayInputStream(screenshotBytes));
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

	/* ==================== OLD CODE COMMENTED OUT ====================
	// Driver setter Method
	// public void setDriver(ThreadLocal<WebDriver> driver) {
	// 	this.driver = driver;
	// }
	================================================================= */

	// static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}