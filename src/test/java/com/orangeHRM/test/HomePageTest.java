package com.orangeHRM.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;

public class HomePageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}


	@Test(priority = 3, groups = {"smoke"})
	public void orangeHRMLogo() {
		loginPage.login("Admin", "admin123");
		Assert.assertTrue(homePage.verifyOrangeHRMLogo(), "Logo is not Visible");
	}


	@Test(priority = 4, groups = {"regression"})
	public void verifyAdminTabVisibility() {
		loginPage.login("Admin", "admin123");
		Assert.assertTrue(homePage.isAdminTabvisible(), "Secure Admin navigation panel widget was not accessible.");
	}
}
