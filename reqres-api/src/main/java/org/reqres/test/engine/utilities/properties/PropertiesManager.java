package org.reqres.test.engine.utilities.properties;

import org.reqres.test.engine.utilities.logs.logsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

    private static Properties config;
    private static Properties reportConfig;

    private PropertiesManager() {}

    public static void initializeProperties() {
        try (InputStream configStream = PropertiesManager.class.getClassLoader().getResourceAsStream("config.properties");
             InputStream reportStream = PropertiesManager.class.getClassLoader().getResourceAsStream("Reporting.properties")) {
            config = new Properties();
            config.load(configStream);
            logsUtils.info("config loaded from config.properties");
            reportConfig = new Properties();
            if (reportStream != null) {
                reportConfig.load(reportStream);
                logsUtils.info("Reporting.properties loaded");
            }
        } catch (IOException e) {
            logsUtils.error("could not load config: ", e.getMessage());
        }
    }

    public static String getConfig(String key) {
        if (config == null) {
            initializeProperties();
        }
        String envOverride = getEnvOverride(key);
        if (envOverride != null) {
            return envOverride;
        }
        return config.getProperty(key, "").trim();
    }

    private static String getEnvOverride(String key) {
        String env = switch (key) {
            case "baseUrl" -> System.getenv("BASE_URL");
            case "wireMockPort" -> System.getenv("WIREMOCK_PORT");
            case "useWireMock" -> System.getenv("USE_WIREMOCK");
            default -> null;
        };
        if (env != null) return env;
        String sysProp = switch (key) {
            case "baseUrl" -> System.getProperty("baseUrl");
            case "wireMockPort" -> System.getProperty("wireMockPort");
            case "useWireMock" -> System.getProperty("useWireMock");
            default -> null;
        };
        return sysProp;
    }

    public static String getReportConfig(String key) {
        if (reportConfig == null) initializeProperties();
        return reportConfig != null ? reportConfig.getProperty(key, "").trim() : "";
    }
}
