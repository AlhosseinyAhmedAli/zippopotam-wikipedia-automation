package test.mobile.hooks;

import test.core.DriverFactory;
import test.core.Screenshot;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;

import java.net.MalformedURLException;

public class MobileHooks {

    @Before("@mobile")
    public void beforeScenario() throws MalformedURLException {
        DriverFactory.initDriver();
    }

    @After("@mobile")
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed() || Status.SKIPPED == scenario.getStatus()) {
            Screenshot.attach("Failure/Skip - " + scenario.getName());
        }
        DriverFactory.quitDriver();
    }
}
