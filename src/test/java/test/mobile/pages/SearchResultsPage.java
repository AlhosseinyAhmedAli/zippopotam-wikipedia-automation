package test.mobile.pages;

import org.openqa.selenium.By;

public class SearchResultsPage extends BasePage {

    public void openFirstResult(String article) {
        By firstResult = By.xpath(
                "(//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")])[1]");
        try {
            visible(firstResult);
            click(firstResult);
            return;
        } catch (Exception ignored) {
        }

        By genericFirst = By.xpath("(//*[@text and @resource-id])[1]");
        try {
            visible(genericFirst);
            click(genericFirst);
            return;
        } catch (Exception e) {
            test.core.Screenshot.attach("OpenFirstResult-Failed: " + article);
            throw new AssertionError("Could not open search result for: " + article);
        }
    }
}
