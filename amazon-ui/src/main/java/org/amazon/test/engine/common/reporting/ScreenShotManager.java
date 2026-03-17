package org.amazon.test.engine.common.reporting;

import org.amazon.test.engine.common.logs.logsUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ScreenShotManager {

    private static final String SCREENSHOT_DIR = "./screenshots";

    private ScreenShotManager() {}

    public static void captureScreenShot(WebDriver driver, String screenshotName) {
        try {
            Path dir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path destination = dir.resolve(screenshotName + ".jpg");
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(destination, bytes, StandardOpenOption.CREATE);
            logsUtils.info("Screenshot saved: ", destination.toString());
        } catch (IOException e) {
            logsUtils.error("Unable to save screenshot: ", e.getMessage());
        }
    }
}
