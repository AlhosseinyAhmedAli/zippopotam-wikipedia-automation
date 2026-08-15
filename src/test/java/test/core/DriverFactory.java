package test.core;

import test.utilities.TestData;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DriverFactory {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver initDriver() throws MalformedURLException {
        if (DRIVER.get() != null) {
            return DRIVER.get();
        }

        configureAndroidSdkEnvironment();

        String appiumUrl = System.getProperty("appiumUrl");
        if (appiumUrl == null || appiumUrl.isBlank()) {
            appiumUrl = TestData.get("mobile", "appiumUrl", "http://127.0.0.1:4723");
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

    private static void configureAndroidSdkEnvironment() {
        String sdkHome = resolveAndroidSdkPath();
        if (sdkHome == null || sdkHome.isBlank()) {
            return;
        }

        System.setProperty("ANDROID_HOME", sdkHome);
        System.setProperty("ANDROID_SDK_ROOT", sdkHome);
        trySetOsEnvironmentVariable("ANDROID_HOME", sdkHome);
        trySetOsEnvironmentVariable("ANDROID_SDK_ROOT", sdkHome);
    }

    private static String resolveAndroidSdkPath() {
        List<String> candidates = new ArrayList<>();
        candidates.add(System.getProperty("androidHome"));
        candidates.add(System.getProperty("ANDROID_HOME"));
        candidates.add(System.getProperty("ANDROID_SDK_ROOT"));
        candidates.add(System.getenv("ANDROID_HOME"));
        candidates.add(System.getenv("ANDROID_SDK_ROOT"));

        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            candidates.add(new File(localAppData, "Android\\Sdk").getAbsolutePath());
        }

        String userHome = System.getProperty("user.home");
        if (userHome != null && !userHome.isBlank()) {
            candidates.add(new File(userHome, "AppData\\Local\\Android\\Sdk").getAbsolutePath());
            candidates.add(new File(userHome, "Library\\Android\\sdk").getAbsolutePath());
            candidates.add(new File(userHome, "Android\\Sdk").getAbsolutePath());
        }

        for (String candidate : candidates) {
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            File sdkDir = new File(candidate);
            if (sdkDir.exists() && sdkDir.isDirectory() && new File(sdkDir, "platform-tools").exists()) {
                return sdkDir.getAbsolutePath();
            }
        }
        return null;
    }

    private static void trySetOsEnvironmentVariable(String name, String value) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Method theEnvironmentMethod = processEnvironmentClass.getDeclaredMethod("theEnvironment");
            theEnvironmentMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> env = (Map<String, String>) theEnvironmentMethod.invoke(null);
            env.put(name, value);

            try {
                Method theCaseInsensitiveEnvironmentMethod = processEnvironmentClass.getDeclaredMethod("theCaseInsensitiveEnvironment");
                theCaseInsensitiveEnvironmentMethod.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<String, String> caseInsensitiveEnv = (Map<String, String>) theCaseInsensitiveEnvironmentMethod.invoke(null);
                caseInsensitiveEnv.put(name, value);
            } catch (NoSuchMethodException ignored) {
                // The environment map is case sensitive on Linux/macOS.
            }
        } catch (Exception ignored) {
        }
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
