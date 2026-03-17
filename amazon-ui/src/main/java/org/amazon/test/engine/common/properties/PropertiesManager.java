package org.amazon.test.engine.common.properties;

import org.amazon.test.engine.common.logs.logsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads framework-level configuration:
 *   - WebConfigurations.properties  (browser type, headless mode, base URL)
 *   - Reporting.properties          (Allure report flags)
 */
public class PropertiesManager {

    private static Properties webConfig;
    private static Properties reportConfig;

    private PropertiesManager() {}

    public static void initializeProperties() {
        try (InputStream webStream    = PropertiesManager.class.getClassLoader().getResourceAsStream("WebConfigurations.properties");
             InputStream reportStream = PropertiesManager.class.getClassLoader().getResourceAsStream("Reporting.properties")) {

            webConfig    = new Properties();
            reportConfig = new Properties();
            webConfig.load(webStream);
            reportConfig.load(reportStream);
            logsUtils.info("Framework configuration loaded");

        } catch (IOException e) {
            logsUtils.error("Failed to load framework config: ", e.getMessage());
        }
    }

    public static String getConfig(String key) {
        if (webConfig == null) initializeProperties();
        return webConfig.getProperty(key, "").trim();
    }

    public static int getIntConfig(String key, int defaultValue) {
        if (webConfig == null) initializeProperties();
        String raw = webConfig.getProperty(key);
        if (raw == null || raw.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            logsUtils.warn("Invalid integer for key ", key, " = ", raw, " — using default ", String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    public static String getReportConfig(String key) {
        if (reportConfig == null) initializeProperties();
        return reportConfig.getProperty(key, "").trim();
    }
}
