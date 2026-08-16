package test.mobile.pages;

import org.openqa.selenium.By;

public class SearchResultsPage extends BasePage {

    public void openFirstResult(String article) {
        By firstResult = By.xpath(
                "(//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")])[1]");
        try {
            // Wait for the specific article result to appear and be visible
            visible(firstResult);
            click(firstResult);
            return;
        } catch (Exception ignored) {
            // continue to fallback
        }

        // Fallback: wait for a generic first result and click
        By genericFirst = By.xpath("(//*[@text and @resource-id])[1]");
        try {
            visible(genericFirst);
            click(genericFirst);
            return;
        } catch (Exception e) {
            // Debug: attach screenshot if nothing could be clicked
            test.core.Screenshot.attach("OpenFirstResult-Failed: " + article);
            throw new AssertionError("Could not open search result for: " + article);
        }
    }
}
