package org.amazon.test.engine.ui.driver;

import org.amazon.test.engine.common.properties.PropertiesManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeDriverFactory extends DriverAbstract {

    @Override
    public WebDriver startDriver() {
        EdgeOptions options = new EdgeOptions();
        if (PropertiesManager.getConfig("HeadlessMode").equalsIgnoreCase("true")) {
            options.addArguments("--headless");
        }
        driver = new EdgeDriver(options);
        return driver;
    }
}
