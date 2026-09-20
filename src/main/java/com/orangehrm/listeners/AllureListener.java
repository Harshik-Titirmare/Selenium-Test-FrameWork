package com.orangehrm.listeners;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.orangehrm.base.BaseClass;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

public class AllureListener implements ITestListener {


    @Override
    public void onStart(ITestContext context) {
        System.out.println("=================================================");
        System.out.println("SUITE EXECUTION STARTED: " + context.getName());
        System.out.println("=================================================");

        // Allure report me OS aur Java version ke details attach karne ke liye
        Allure.parameter("OS", System.getProperty("os.name"));
        Allure.parameter("Java Version", System.getProperty("java.version"));
    }


    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("TEST STARTED: " + result.getName() + " on thread [" + Thread.currentThread().getId() + "]");
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("TEST PASSED: " + result.getName());
        // Allure report me ek chhota sa text log attach karne ke liye
        Allure.addAttachment("Test Status Log", "Test completed successfully without errors.");
    }


    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("TEST FAILED: " + result.getName());

        // Error message ko report me alag se highlight karne ke liye text attachment
//        Log the failure reason clearly in Allure Attachments
        if (result.getThrowable() != null) {
            Allure.addAttachment("Failure Reason Log", "Test execution failed due to:\n" + result.getThrowable().getMessage());
        }

        WebDriver activeDriver = null;
        Object testInstance = result.getInstance();

        try {
            if (testInstance instanceof BaseClass) {
                activeDriver = BaseClass.getDriver();
            }
        } catch (Exception e) {
            System.out.println("Could not resolve driver instance via test instance context: " + e.getMessage());
        }

        if (activeDriver == null) {
            try {
                if (BaseClass.driver != null && BaseClass.driver.get() != null) {
                    activeDriver = BaseClass.driver.get();
                }
            } catch (Exception e) {
                System.out.println("Fallback ThreadLocal query failed: " + e.getMessage());
            }
        }

        if (activeDriver != null) {
            System.out.println("Driver found successfully! Attaching screenshot to report stream...");
            saveScreenshot(activeDriver);
        } else {
            System.out.println("Screenshot skipped: WebDriver was null for this execution thread.");
        }
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("TEST SKIPPED: " + result.getName());
        if (result.getThrowable() != null) {
            Allure.addAttachment("Skip Reason Log", result.getThrowable().getMessage());
        }
    }


    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=================================================");
        System.out.println("SUITE EXECUTION FINISHED: " + context.getName());
        System.out.println("Total Passed: " + context.getPassedTests().size());
        System.out.println("Total Failed: " + context.getFailedTests().size());
        System.out.println("=================================================");
    }

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
