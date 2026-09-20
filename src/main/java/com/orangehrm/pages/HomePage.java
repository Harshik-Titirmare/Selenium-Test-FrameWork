package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class HomePage {

	private WebDriver driver;

	// Define locators using By Class
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By userIdButton = By.className("oxd-userdropdown-tab");
	private By logoutButton = By.xpath("//a[text()='Logout']");
	private By orangeHRMLogo = By.xpath("//div[@class='oxd-brand-banner']//img");

	/*
	 * ==================== OLD CODE COMMENTED OUT ==================== //
	 * Constructor left empty to prevent cross-thread driver collision in parallel
	 * tests // public HomePage(WebDriver driver) { // }
	 * =================================================================
	 */

	// NEW FIX: Store local driver instance safely for explicit waits while
	// maintaining ThreadLocal compatibility
	public HomePage(WebDriver driver) {
		this.driver = driver;
	}

	// Helper method to resolve the current thread's localized ActionDriver instance
	private ActionDriver getAction() {
		return BaseClass.getActionDriver();
	}

	/*
	 * ==================== OLD CODE COMMENTED OUT ==================== // Method to
	 * verify if the Admin Dashboard tab navigation is visible // public boolean
	 * isAdminTabvisible() { // return getAction().isDisplayed(adminTab); // }
	 * =================================================================
	 */

	// NEW FIX: Added explicit wait for adminTab visibility before checking display
	// status to prevent NoSuchElementException in headless mode
	public boolean isAdminTabvisible() {
		try {
			new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
					.until(ExpectedConditions.visibilityOfElementLocated(adminTab));
			return getAction().isDisplayed(adminTab);
		} catch (Exception e) {
			System.out.println("HomePage ERROR: Admin tab not visible after wait -> " + e.getMessage());
			return false;
		}
	}

	// Method to verify if the official corporate brand logo is displayed
	public boolean verifyOrangeHRMLogo() {
		try {
			new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
					.until(ExpectedConditions.visibilityOfElementLocated(orangeHRMLogo));
			return getAction().isDisplayed(orangeHRMLogo);
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * ==================== OLD CODE COMMENTED OUT ==================== // Method to
	 * execute user logout flow from the application header dropdown // public void
	 * logout() { // getAction().click(userIdButton); //
	 * getAction().click(logoutButton); // }
	 * =================================================================
	 */

	// NEW FIX: Added explicit wait for user dropdown menu animation before clicking
	// logout button
	public void logout() {
		try {
			getAction().click(userIdButton);
			new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(10))
					.until(ExpectedConditions.elementToBeClickable(logoutButton));
			getAction().click(logoutButton);
		} catch (Exception e) {
			System.out.println("HomePage ERROR: Unable to complete logout flow -> " + e.getMessage());
		}
	}
}