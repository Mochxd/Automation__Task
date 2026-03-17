package org.amazon.test.engine.ui.listeners.testng;

import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.common.reporting.AllureReportHelper;
import org.amazon.test.engine.common.reporting.ScreenShotManager;
import org.amazon.test.engine.ui.driver.Driver;
import org.testng.IAlterSuiteListener;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.amazon.test.engine.common.properties.PropertiesManager.getReportConfig;
import static org.amazon.test.engine.common.properties.PropertiesManager.initializeProperties;

public class TestNGListener implements ITestListener, IExecutionListener, IAlterSuiteListener {

    @Override
    public void onExecutionStart() {
        logsUtils.info("============ Amazon UI Test Execution Started ============");
        initializeProperties();

        if (getReportConfig("CleanAllureReport").equalsIgnoreCase("true")) {
            AllureReportHelper.cleanAllureReport();
        }

        AllureReportHelper.setupReportBranding();
    }

    @Override
    public void onExecutionFinish() {
        logsUtils.info("Generating Allure report...");
        if (getReportConfig("OpenAllureReportAfterExecution").equalsIgnoreCase("true")) {
            try {
                new ProcessBuilder("cmd", "/c", "reportGeneration.bat")
                        .directory(new File(System.getProperty("user.dir")))
                        .inheritIO()
                        .start();
                logsUtils.info("Allure report launched");
            } catch (IOException e) {
                logsUtils.error("Failed to open Allure report: ", e.getMessage());
            }
        }
        logsUtils.info("============ Amazon UI Test Execution Finished ============");
    }

    @Override
    public void onTestStart(ITestResult result) {
        logsUtils.info("--- Starting test: ", result.getName(), " ---");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logsUtils.info("--- PASSED: ", result.getName(), " ---");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logsUtils.error("--- FAILED: ", result.getName(), " ---");

        Driver driver = resolveDriver(result);
        if (driver == null) {
            logsUtils.warn("No Driver found for test — skipping screenshot");
            return;
        }

        logsUtils.info("Capturing failure screenshot...");
        ScreenShotManager.captureScreenShot(driver.get(), result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logsUtils.warn("--- SKIPPED: ", result.getName(), " ---");
    }

    private Driver resolveDriver(ITestResult result) {
        Object testInstance = result.getInstance();
        for (Field field : result.getTestClass().getRealClass().getDeclaredFields()) {
            try {
                if (field.getType() == Driver.class) {
                    field.setAccessible(true);
                    return (Driver) field.get(testInstance);
                }
                if (field.getType() == ThreadLocal.class) {
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    ThreadLocal<Driver> tl = (ThreadLocal<Driver>) field.get(testInstance);
                    return tl.get();
                }
            } catch (IllegalAccessException e) {
                logsUtils.error("Cannot access field: ", field.getName());
            }
        }
        return null;
    }
}
