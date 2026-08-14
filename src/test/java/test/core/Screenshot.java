package test.core;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public final class Screenshot {
    private Screenshot() {
    }

    public static void attach(String name) {
        try {
            if (!DriverFactory.isDriverInitialized()) {
                return;
            }

            AndroidDriver driver = DriverFactory.getDriver();
            byte[] image = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(image), ".png");
        } catch (Exception ignored) {
        }
    }
}
