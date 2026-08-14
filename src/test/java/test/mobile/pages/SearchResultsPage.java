package test.mobile.pages;

import org.openqa.selenium.By;

public class SearchResultsPage extends BasePage {

    public void openFirstResult(String article) {
        By firstResult = By.xpath(
                "(//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")])[1]");
        click(firstResult);
    }
}
