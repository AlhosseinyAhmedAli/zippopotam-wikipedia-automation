package test.core;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class AllureScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Screenshot.attach("Failed - " + result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Screenshot.attach("Skipped - " + result.getName());
    }
}
