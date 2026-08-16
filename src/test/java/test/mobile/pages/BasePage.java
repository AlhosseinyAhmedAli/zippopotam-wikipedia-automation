package test.mobile.pages;

import test.core.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.openqa.selenium.Dimension;

public abstract class BasePage {
    protected AndroidDriver driver() {
        return DriverFactory.getDriver();
    }

    protected WebDriverWait waitt() {
        return new WebDriverWait(driver(), Duration.ofSeconds(20));
    }

    protected WebElement visible(By locator) {
        return waitt().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitt().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected boolean exists(By locator) {
        try {
            List<WebElement> elements = driver().findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void scrollDown() {
        Dimension size = driver().manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        driver().executeScript("mobile: scrollGesture", Map.of(
                "left", 10,
                "top", Math.max(100, height / 5),
                "width", Math.max(100, width - 20),
                "height", Math.max(300, height * 3 / 5),
                "direction", "down",
                "percent", 0.75));
    }

    protected void scrollToTop() {
        Dimension size = driver().manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        driver().executeScript("mobile: scrollGesture", Map.of(
                "left", 10,
                "top", Math.max(100, height / 5),
                "width", Math.max(100, width - 20),
                "height", Math.max(300, height * 3 / 5),
                "direction", "up",
                "percent", 1.0));
    }

    protected void pressBack() {
        driver().navigate().back();
    }

    protected void tap(int x, int y) {
        driver().executeScript("mobile: clickGesture", Map.of(
                "x", x,
                "y", y,
                "duration", 100));
    }

    protected void tapByAdb(int x, int y) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "adb",
                    "-s",
                    "emulator-5554",
                    "shell",
                    "input",
                    "tap",
                    String.valueOf(x),
                    String.valueOf(y));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Try without -s as a fallback (let adb pick the device)
                ProcessBuilder pb2 = new ProcessBuilder("adb", "shell", "input", "tap", String.valueOf(x), String.valueOf(y));
                pb2.redirectErrorStream(true);
                Process process2 = pb2.start();
                try (BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
                    String line;
                    while ((line = reader2.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                }
                int exit2 = process2.waitFor();
                if (exit2 != 0) {
                    // Final fallback: try Appium gesture tap
                    try {
                        tap(x, y);
                        return;
                    } catch (Exception e) {
                        throw new IllegalStateException("ADB tap failed at " + x + "," + y + "; exit=" + exitCode + "; output=" + output.toString(), e);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            // Fallback to Appium gesture tap
            try {
                tap(x, y);
                return;
            } catch (Exception inner) {
                throw new IllegalStateException("Unable to tap via ADB at " + x + "," + y, e);
            }
        }
    }

    protected void launchActivity(String packageName, String activityName) {
        try {
            Process process = new ProcessBuilder(
                    "adb",
                    "shell",
                    "am",
                    "start",
                    "-n",
                    packageName + "/" + activityName)
                    .start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("Unable to launch activity " + packageName + "/" + activityName + "; adb exit code=" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to launch activity " + packageName + "/" + activityName, e);
        }
    }
}
