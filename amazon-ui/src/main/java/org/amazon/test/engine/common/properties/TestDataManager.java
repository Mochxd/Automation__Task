package org.amazon.test.engine.common.properties;

import org.amazon.test.engine.common.logs.logsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads website test data from TestData.properties:
 *   - Amazon credentials  (amazon.email, amazon.password)
 *   - Checkout details    (checkout.fullName, checkout.mobileNumber, etc.)
 */
public class TestDataManager {

    private static Properties testData;

    private TestDataManager() {}

    private static void load() {
        try (InputStream stream = TestDataManager.class.getClassLoader().getResourceAsStream("TestData.properties")) {
            testData = new Properties();
            testData.load(stream);
            logsUtils.info("Test data loaded");
        } catch (IOException e) {
            logsUtils.error("Failed to load TestData.properties: ", e.getMessage());
        }
    }

    public static String getData(String key) {
        if (testData == null) load();
        return testData.getProperty(key, "").trim();
    }
}
