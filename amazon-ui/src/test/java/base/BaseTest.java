package base;

import org.amazon.test.engine.ui.driver.Driver;
import org.testng.annotations.AfterMethod;

public class BaseTest {

    protected Driver driver;

    @AfterMethod(alwaysRun = true)
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
