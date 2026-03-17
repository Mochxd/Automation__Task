package org.reqres.test.engine.utilities.reporting;

import org.reqres.test.engine.utilities.logs.logsUtils;
import org.reqres.test.engine.utilities.properties.PropertiesManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/** Allure report: clean results dir, write environment and executor for report branding. */
public final class AllureReportHelper {

    private static final String ALLURE_RESULTS_DIR = "target/allure-results";

    private AllureReportHelper() {}

    public static void cleanAllureReport() {
        File dir = new File(ALLURE_RESULTS_DIR);
        if (!dir.exists()) return;
        try {
            deleteDir(dir);
            logsUtils.info("allure results dir cleaned");
        } catch (IOException e) {
            logsUtils.warn("allure clean: ", e.getMessage());
        }
    }

    public static void setupReportBranding() {
        createResultsDir();
        writeEnvironmentProperties();
        writeExecutorJson();
        logsUtils.info("allure branding written to ", ALLURE_RESULTS_DIR);
    }

    private static void createResultsDir() {
        File dir = new File(ALLURE_RESULTS_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private static void writeEnvironmentProperties() {
        Properties env = new Properties();
        env.setProperty("Base.URL", PropertiesManager.getConfig("baseUrl"));
        env.setProperty("UseWireMock", PropertiesManager.getConfig("useWireMock"));
        env.setProperty("Framework", "ReqRes API");

        File file = new File(ALLURE_RESULTS_DIR + "/environment.properties");
        try (FileWriter w = new FileWriter(file)) {
            env.store(w, "ReqRes API test environment");
        } catch (IOException e) {
            logsUtils.error("failed to write environment.properties: ", e.getMessage());
        }
    }

    private static void writeExecutorJson() {
        String json = "{\n" +
                "  \"name\": \"ReqRes API Tests\",\n" +
                "  \"type\": \"local\",\n" +
                "  \"reportName\": \"ReqRes API Test Report\"\n" +
                "}";
        File file = new File(ALLURE_RESULTS_DIR + "/executor.json");
        try (FileWriter w = new FileWriter(file)) {
            w.write(json);
        } catch (IOException e) {
            logsUtils.error("failed to write executor.json: ", e.getMessage());
        }
    }

    private static void deleteDir(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File f : children) deleteDir(f);
            }
        }
        Files.delete(dir.toPath());
    }
}
