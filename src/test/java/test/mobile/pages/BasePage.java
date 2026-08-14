package test.mobile.pages;

import test.core.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
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
}
