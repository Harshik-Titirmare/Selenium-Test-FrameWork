package com.orangeHRM.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.PIMPage;

public class PIMTest extends BaseClass {

	private LoginPage loginPage;
	private PIMPage pimPage;

	@BeforeMethod(alwaysRun = true)
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		pimPage = new PIMPage(getDriver());
	}

	@Test(priority = 5, groups = {"regression"})
	public void verifyAddEmployeeWithLoginDetailsFlow() {
		// Step 1: Login
		loginPage.login("Admin", "admin123");

		// Step 2: Navigate to Add Employee
		pimPage.navigateToPIM();
		pimPage.navigateToAddEmployee();

		// Step 3: Enter Details & Toggle Login Credentials
		String uniqueId = String.valueOf(System.currentTimeMillis()).substring(7);
		pimPage.enterEmployeeDetails("Rahul", "Kumar", "Sharma", uniqueId);
		pimPage.enableCreateLoginDetails();
		pimPage.enterLoginDetails("rahul" + uniqueId, "Password@123");

		// Step 4: Save
		pimPage.clickSave();
		staticWait(3);

		// Dynamic String Assertions (No true/false output)
		boolean isSaved = pimPage.isSaveSuccessful();
		String actualResult = isSaved ? "Employee created successfully with toast message" : "Employee creation failed";
		String expectedResult = "Employee created successfully with toast message";

		Assert.assertEquals(actualResult, expectedResult, "Add Employee Flow Validation Failed:");
	}

	@Test(priority = 6, groups = {"regression"})
	public void verifyAddEmployeeMandatoryFieldsValidationFailure() {
		// Step 1: Login & Navigate
		loginPage.login("Admin", "admin123");
		pimPage.navigateToPIM();
		pimPage.navigateToAddEmployee();

		// Step 2: Submit form without required fields
		pimPage.clickSave();
		staticWait(1);

		// Step 3: Explicit Assertion for Failure Scenario
		boolean isSaved = pimPage.isSaveSuccessful();
		String actualResult = isSaved
				? "Form saved successfully without required fields"
				: "Form submission blocked by required field validation errors";

		String expectedResult = "Form saved successfully without required fields"; // Intentionally mismatching to trigger failure

		Assert.assertEquals(actualResult, expectedResult, "Mandatory Fields Validation Test Failed:");
	}
}