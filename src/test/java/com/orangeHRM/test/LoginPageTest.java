package com.orangeHRM.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.dataprovider.JSONDataReader;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;

public class LoginPageTest extends BaseClass {

	@Test(priority = 2, groups = {"smoke", "regression"}, dataProvider = "getLoginData", dataProviderClass = JSONDataReader.class)
	public void loginDataDrivenTest(String scenario, String username, String password, String expectedError) {

		LoginPage loginPage = new LoginPage(getDriver());
		HomePage homePage = new HomePage(getDriver());

		System.out.println("Executing Scenario: " + scenario);
		loginPage.login(username, password);

		if (expectedError == null || expectedError.isEmpty()) {
			boolean isAdminVisible = homePage.isAdminTabvisible();

			String actualResult = isAdminVisible
					? "Admin Tab is displayed successfully"
					: "Admin Tab is NOT displayed (UI Error: '" + (loginPage.errorMessageDisplayed() ? loginPage.getErrorMessageText() : "None") + "')";

			String expectedResult = "Admin Tab is displayed successfully";

			Assert.assertEquals(actualResult, expectedResult, "Login Scenario Validation Failed:");

			homePage.logout();
			staticWait(2);
		} else {
			// Direct comparison without unused variables (Zero Warning)
			String actualErrorText = loginPage.errorMessageDisplayed() ? loginPage.getErrorMessageText() : "No Error Message Displayed";
			Assert.assertEquals(actualErrorText, expectedError, "Error Message Validation Failed:");
		}
	}
}