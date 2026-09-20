package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class PIMPage {

	// Menu & Sub-menu Locators
	private By pimTab = By.xpath("//span[text()='PIM']");
	private By addEmployeeTab = By.xpath("//a[text()='Add Employee']");

	// Employee Name & ID Locators
	private By firstNameField = By.name("firstName");
	private By middleNameField = By.name("middleName");
	private By lastNameField = By.name("lastName");
	private By employeeIdField = By.xpath("//label[text()='Employee Id']/parent::div/following-sibling::div/input");

	// Create Login Details Toggle & Additional Fields
	private By createLoginDetailsToggle = By.xpath("//span[contains(@class, 'oxd-switch-input')]");
	private By usernameField = By.xpath("//label[text()='Username']/parent::div/following-sibling::div/input");
	private By enabledStatusRadio = By.xpath("//label[contains(.,'Enabled')]/span");
	private By passwordField = By.xpath("//label[text()='Password']/parent::div/following-sibling::div/input");
	private By confirmPasswordField = By.xpath("//label[text()='Confirm Password']/parent::div/following-sibling::div/input");

	// Action Buttons
	private By saveButton = By.xpath("//button[@type='submit' and contains(.,'Save')]");
	private By successToast = By.xpath("//div[contains(@class,'oxd-toast-start')]");

	public PIMPage(WebDriver driver) {
	}

	private ActionDriver getAction() {
		return BaseClass.getActionDriver();
	}

	public void navigateToPIM() {
		getAction().click(pimTab);
	}

	public void navigateToAddEmployee() {
		getAction().click(addEmployeeTab);
	}

	public void enterEmployeeDetails(String firstName, String middleName, String lastName, String empId) {
		getAction().enterText(firstNameField, firstName);
		getAction().enterText(middleNameField, middleName);
		getAction().enterText(lastNameField, lastName);
		if (empId != null && !empId.isEmpty()) {
			getAction().enterText(employeeIdField, empId);
		}
	}

	public void enableCreateLoginDetails() {
		getAction().click(createLoginDetailsToggle);
	}

	public void enterLoginDetails(String username, String password) {
		getAction().enterText(usernameField, username);
		getAction().click(enabledStatusRadio);
		getAction().enterText(passwordField, password);
		getAction().enterText(confirmPasswordField, password);
	}

	public void clickSave() {
		getAction().click(saveButton);
	}

	public boolean isSaveSuccessful() {
		return getAction().isDisplayed(successToast);
	}
}