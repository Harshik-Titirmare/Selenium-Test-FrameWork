package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LoginPage {

	// Define locators using By Class
	private By userNameField = By.name("username");
	private By passwordField = By.name("password");
	private By loginButton = By.xpath("//button[text()=' Login ']");
	private By errorMessage = By.xpath("//p[text()='Invalid credentials']");

	// Constructor left empty to avoid saving state across parallel execution threads
	public LoginPage(WebDriver driver) {
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
