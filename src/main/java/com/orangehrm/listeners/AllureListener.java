package com.orangehrm.listeners;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.orangehrm.base.BaseClass;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllureListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("=================================================");
        System.out.println("SUITE EXECUTION STARTED: " + context.getName());
        System.out.println("=================================================");

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
        Allure.addAttachment("Test Status Log", "Test completed successfully without errors.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("TEST FAILED: " + result.getName());

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
            System.out.println("Driver found successfully! Capturing screenshot...");
            
            // 1. Allure Report me attach karega
            saveScreenshotToAllure(activeDriver);

            // 2. Email attachments ke liye target/screenshots folder me save karega
            saveScreenshotToDisk(activeDriver, result.getName());
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

    // Allure attachment method
    @Attachment(value = "Failure Screenshot", type = "image/png")
    public byte[] saveScreenshotToAllure(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    // File disk par save karne ka method (Jenkins Email Attachment ke liye)
    public void saveScreenshotToDisk(WebDriver driver, String testName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            
            // Screenshots 'target/screenshots/' folder me save honge
            String destPath = System.getProperty("user.dir") + "/target/screenshots/" + testName + "_" + timestamp + ".png";
            File destFile = new File(destPath);
            
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("Screenshot saved locally for email attachment at: " + destPath);
        } catch (IOException e) {
            System.out.println("Failed to save screenshot locally: " + e.getMessage());
        }
    }
}