package org.amazon.test.engine.common.reporting;

import org.apache.commons.io.FileUtils;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.common.properties.PropertiesManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class AllureReportHelper {

    private static final String ALLURE_RESULTS_DIR = "target/allure-results";

    private AllureReportHelper() {}

    public static void cleanAllureReport() {
        try {
            FileUtils.deleteDirectory(new File(ALLURE_RESULTS_DIR));
            logsUtils.info("Allure results directory cleaned");
        } catch (IOException e) {
            logsUtils.info("Allure results already clean or not found");
        }
    }

    /**
     * Writes environment.properties and executor.json into target/allure-results/.
     * Allure 2 picks these up automatically and displays them in the report's
     * Environment and Executor panels — showing Amazon branding context.
     */
    public static void setupReportBranding() {
        createResultsDir();
        writeEnvironmentProperties();
        writeExecutorJson();
        logsUtils.info("Allure report branding files written to: ", ALLURE_RESULTS_DIR);
    }

    private static void createResultsDir() {
        File dir = new File(ALLURE_RESULTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void writeEnvironmentProperties() {
        Properties env = new Properties();
        env.setProperty("Application",  "Amazon Egypt");
        env.setProperty("Base.URL",     PropertiesManager.getConfig("BaseURL"));
        env.setProperty("Browser",      PropertiesManager.getConfig("BrowserType"));
        env.setProperty("Headless",     PropertiesManager.getConfig("HeadlessMode"));
        env.setProperty("Environment",  "Production");
        env.setProperty("Framework",    "Amazon UI Task");

        File file = new File(ALLURE_RESULTS_DIR + "/environment.properties");
        try (FileWriter writer = new FileWriter(file)) {
            env.store(writer, "Amazon UI Test Environment");
            logsUtils.info("environment.properties written");
        } catch (IOException e) {
            logsUtils.error("Failed to write environment.properties: ", e.getMessage());
        }
    }

    private static void writeExecutorJson() {
        String json = "{\n" +
                "  \"name\": \"Amazon UI — Shopping Flow\",\n" +
                "  \"type\": \"local\",\n" +
                "  \"reportName\": \"Amazon Egypt — Automated Test Report\"\n" +
                "}";

        File file = new File(ALLURE_RESULTS_DIR + "/executor.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            logsUtils.info("executor.json written");
        } catch (IOException e) {
            logsUtils.error("Failed to write executor.json: ", e.getMessage());
        }
    }
}
