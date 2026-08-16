# Zippopotam.us API + Wikipedia Android Automation Assessment

Java automation framework for the assessment, focused on:

* Zippopotam.us REST API automation with Rest Assured + TestNG
* API data-driven testing from CSV
* Wikipedia Android automation with Appium
* Cucumber / BDD
* Page Object Model
* ThreadLocal AndroidDriver through `DriverFactory`
* Failure screenshots attached to Allure
* Cucumber HTML/JSON + Allure reporting
* Android Studio Emulator + local Appium
* Maven / Java 25 project configuration

```

git init
git add .
git commit -m "Add API and Wikipedia Android automation assessment"
git branch -M main
git remote add origin <YOUR\\\_GITHUB\\\_REPOSITORY\\\_URL>
git push -u origin main
```

## Android SDK setup for Appium

Appium requires the Android SDK to be visible via `ANDROID_HOME` and/or `ANDROID_SDK_ROOT`.

On Windows, set the environment variables before launching Appium or the test suite:

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
$env:Path = "$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:Path"
```

If the SDK is installed in a different location, replace the path above with that install directory.

For IntelliJ IDEA, add these as Environment variables in the Run/Debug configuration, or start Appium from the project scripts:

```bat
start-appium.bat
run-mobile-tests.bat
```

The project also auto-detects the Android SDK in common installation paths and sets both variables at runtime before the driver starts.

