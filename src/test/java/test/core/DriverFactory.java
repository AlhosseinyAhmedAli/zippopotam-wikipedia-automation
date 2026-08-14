package test.core;

import test.utilities.TestData;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public final class DriverFactory {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver initDriver() throws MalformedURLException {
        if (DRIVER.get() != null) {
            return DRIVER.get();
        }

        String appiumUrl = System.getProperty("appiumUrl");
        if (appiumUrl == null || appiumUrl.isBlank()) {
            appiumUrl = TestData.get("mobile", "appiumUrl", "http://127.0.0.1:4723");
        }

        String androidHome = System.getProperty("androidHome");
        if (androidHome != null && !androidHome.isBlank()) {
            System.setProperty("ANDROID_HOME", androidHome);
        }

        String deviceName = System.getProperty("deviceName");
        if (deviceName == null || deviceName.isBlank()) {
            deviceName = TestData.get("mobile", "device.name", "emulator-5554");
        }

        String platformVersion = System.getProperty("platformVersion");
        if (platformVersion == null || platformVersion.isBlank()) {
            platformVersion = TestData.get("mobile", "platform.version", "17");
        }

        String appPackage = System.getProperty("appPackage");
        if (appPackage == null || appPackage.isBlank()) {
            appPackage = TestData.get("mobile", "app.package", "org.wikipedia");
        }

        String appActivity = System.getProperty("appActivity");
        if (appActivity == null || appActivity.isBlank()) {
            appActivity = TestData.get("mobile", "app.activity", "org.wikipedia.main.MainActivity");
        }

        String appPath = System.getProperty("appPath");

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(deviceName)
                .setPlatformVersion(platformVersion)
                .setAutomationName("UiAutomator2")
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setNoReset(true);

        if (appPath != null && !appPath.isBlank()) {
            options.setApp(appPath);
            options.setNoReset(false);
        } else {
            options.setAppPackage(appPackage);
            options.setAppActivity(appActivity);
        }

        AndroidDriver driver = new AndroidDriver(new URL(appiumUrl), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        DRIVER.set(driver);
        return driver;
    }

    public static boolean isDriverInitialized() {
        return DRIVER.get() != null;
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("AndroidDriver is not initialized. Call initDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
