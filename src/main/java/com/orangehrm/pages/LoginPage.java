package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LoginPage {
	private WebDriver driver;

	// Define locators using By Class
	private By userNameField = By.name("username");
	private By passwordField = By.name("password");
	private By loginButton = By.xpath("//button[text()=' Login ']");
	private By errorMessage = By.xpath("//p[text()='Invalid credentials']");

	/* ==================== OLD CODE COMMENTED OUT ====================
	// Constructor left empty to avoid saving state across parallel execution threads
	// public LoginPage(WebDriver driver) {
	// }
	================================================================= */

	// NEW FIX: Initialize local driver instance or retrieve thread-safe driver from BaseClass to avoid NullPointerException in WebDriverWait
	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}

	// Helper method to fetch the active thread's dynamic ActionDriver instance
	private ActionDriver getAction() {
		return BaseClass.getActionDriver();
	}

	// Method to perform login actions
	public void login(String userName, String password) {
		getAction().enterText(userNameField, userName);
		getAction().enterText(passwordField, password);
		getAction().click(loginButton);
		

		// NEW FIX: Use BaseClass.getDriver() dynamically to guarantee non-null driver instance across ThreadLocal parallel threads
		try {
			new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
					.until(ExpectedConditions.urlContains("/dashboard"));
		} catch (Exception e) {
			// Catch timeout for invalid login data-driven scenarios where dashboard redirection is not expected
			System.out.println("LoginPage: Dashboard URL wait timed out (expected for invalid login attempts).");
		}
	}

	// Method to check if error message element is displayed on the screen
	public boolean errorMessageDisplayed() {
		return getAction().isDisplayed(errorMessage);
	}

	// Method to capture the inner text of the error message element
	public String getErrorMessageText() {
		return getAction().getText(errorMessage);
	}

	// Method to validate if the actual error message text matches expectation
	public boolean verifyErrorMessage(String expectedError) {
		return getAction().compareText(errorMessage, expectedError);
	}
}