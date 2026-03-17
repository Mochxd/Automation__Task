package org.reqres.test.engine.listeners.testng;

import org.reqres.test.engine.utilities.logs.logsUtils;
import org.reqres.test.engine.utilities.reporting.AllureReportHelper;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** TestNG listener: Allure setup/teardown and test lifecycle logging (like amazon-ui). */
public class TestNGListener implements ITestListener, IExecutionListener {

    @Override
    public void onExecutionStart() {
        logsUtils.info("============ ReqRes API test execution started ============");
        org.reqres.test.engine.utilities.properties.PropertiesManager.initializeProperties();
        if ("true".equalsIgnoreCase(org.reqres.test.engine.utilities.properties.PropertiesManager.getReportConfig("CleanAllureReport"))) {
            AllureReportHelper.cleanAllureReport();
        }
        AllureReportHelper.setupReportBranding();
    }

    @Override
    public void onExecutionFinish() {
        logsUtils.info("generating Allure report...");
        if ("true".equalsIgnoreCase(org.reqres.test.engine.utilities.properties.PropertiesManager.getReportConfig("OpenAllureReportAfterExecution"))) {
            try {
                new ProcessBuilder("cmd", "/c", "reportGeneration.bat")
                        .directory(new java.io.File(System.getProperty("user.dir")))
                        .inheritIO()
                        .start();
                logsUtils.info("allure report launched");
            } catch (Exception e) {
                logsUtils.error("failed to open Allure report: ", e.getMessage());
            }
        }
        logsUtils.info("============ ReqRes API test execution finished ============");
    }

    @Override
    public void onTestStart(ITestResult result) {
        logsUtils.info("--- starting test: ", result.getMethod().getMethodName(), " ---");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logsUtils.info("--- passed: ", result.getMethod().getMethodName(), " ---");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logsUtils.error("--- failed: ", result.getMethod().getMethodName(), " ---");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logsUtils.warn("--- skipped: ", result.getMethod().getMethodName(), " ---");
    }
}
