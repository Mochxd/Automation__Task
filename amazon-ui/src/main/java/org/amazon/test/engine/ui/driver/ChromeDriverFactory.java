package org.amazon.test.engine.ui.driver;

import org.amazon.test.engine.common.properties.PropertiesManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverFactory extends DriverAbstract {

    @Override
    public WebDriver startDriver() {
        ChromeOptions options = new ChromeOptions();
        if (PropertiesManager.getConfig("HeadlessMode").equalsIgnoreCase("true")) {
            options.addArguments("--headless");
        }
        driver = new ChromeDriver(options);
        return driver;
    }
}
