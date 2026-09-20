package com.orangeHRM.test;



import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;

public class HeaderTest extends BaseClass  {

	@Test (priority = 1, groups = {"smoke", "regression"})
	public void verifyHeader() {
		String title = getDriver().getTitle();
		assert title.equals("OrangeHRM"): "Test Faild - Title is Not Matching";
		System.out.println("Test Passed : - Title is Matching");
	}
}


