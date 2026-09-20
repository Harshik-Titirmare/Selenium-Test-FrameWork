package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class HomePage {

	// Define locators using By Class
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By userIdButton = By.className("oxd-userdropdown-tab");
	private By logoutButton = By.xpath("//a[text()='Logout']");
	private By orangeHRMLogo = By.xpath("//div[@class='oxd-brand-banner']//img");

	// Constructor left empty to prevent cross-thread driver collision in parallel tests
	public HomePage(WebDriver driver) {
	}

	// Helper method to resolve the current thread's localized ActionDriver instance
	private ActionDriver getAction() {
		return BaseClass.getActionDriver();
	}

	// Method to verify if the Admin Dashboard tab navigation is visible
	public boolean isAdminTabvisible() {
		return getAction().isDisplayed(adminTab);
	}

	// Method to verify if the official corporate brand logo is displayed
	public boolean verifyOrangeHRMLogo() {
		return getAction().isDisplayed(orangeHRMLogo);
	}

	// Method to execute user logout flow from the application header dropdown
	public void logout() {
		getAction().click(userIdButton);
		getAction().click(logoutButton);
	}
}
