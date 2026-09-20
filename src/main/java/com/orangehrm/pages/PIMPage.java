package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class PIMPage {

	private WebDriver driver;

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

	/* ==================== OLD CODE COMMENTED OUT ====================
	// public PIMPage(WebDriver driver) {
	// }
	================================================================= */

	// NEW FIX: Store local driver reference to use inside WebDriverWait instances
	public PIMPage(WebDriver driver) {
		this.driver = driver;
	}

	private ActionDriver getAction() {
		return BaseClass.getActionDriver();
	}

	/* ==================== OLD CODE COMMENTED OUT ====================
	// public void navigateToPIM() {
	// 	getAction().click(pimTab);
	// }
	//
	// public void navigateToAddEmployee() {
	// 	getAction().click(addEmployeeTab);
	// }
	================================================================= */

	// NEW FIX: Added explicit waits for menu navigation tabs to render properly before clicking
	public void navigateToPIM() {
		new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.elementToBeClickable(pimTab));
		getAction().click(pimTab);
	}

	public void navigateToAddEmployee() {
		new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.elementToBeClickable(addEmployeeTab));
		getAction().click(addEmployeeTab);
	}

	// NEW FIX: Added wait for input fields visibility before entering employee personal details
	public void enterEmployeeDetails(String firstName, String middleName, String lastName, String empId) {
		new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
		
		getAction().enterText(firstNameField, firstName);
		getAction().enterText(middleNameField, middleName);
		getAction().enterText(lastNameField, lastName);
		if (empId != null && !empId.isEmpty()) {
			getAction().enterText(employeeIdField, empId);
		}
	}

	// NEW FIX: Added explicit wait for toggle switch stability
	public void enableCreateLoginDetails() {
		new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(createLoginDetailsToggle));
		getAction().click(createLoginDetailsToggle);
	}

	// NEW FIX: Added explicit wait for login detail input fields to become visible after expanding toggle
	public void enterLoginDetails(String username, String password) {
		new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
		
		getAction().enterText(usernameField, username);
		getAction().click(enabledStatusRadio);
		getAction().enterText(passwordField, password);
		getAction().enterText(confirmPasswordField, password);
	}

	public void clickSave() {
		getAction().click(saveButton);
	}

	/* ==================== OLD CODE COMMENTED OUT ====================
	// public boolean isSaveSuccessful() {
	// 	return getAction().isDisplayed(successToast);
	// }
	================================================================= */

	// NEW FIX: Added explicit wait for toast popup message visibility after clicking save button
	public boolean isSaveSuccessful() {
		try {
			new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(15))
					.until(ExpectedConditions.visibilityOfElementLocated(successToast));
			return getAction().isDisplayed(successToast);
		} catch (Exception e) {
			System.out.println("PIMPage ERROR: Success toast notification not displayed -> " + e.getMessage());
			return false;
		}
	}
}