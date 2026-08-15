package test.mobile.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    private final By skip = By.xpath("//*[@text='Skip' or @content-desc='Skip']");
    private final By next = By.xpath("//*[@text='Next' or @content-desc='Next' or @text='Continue' or @content-desc='Continue']");
    private final By searchButton = By.xpath(
            "//*[@resource-id='org.wikipedia:id/nav_tab_search' or @content-desc='Search' or @text='Search' or " +
            "contains(@content-desc,'Search')]");
    private final By searchBar = By.id("org.wikipedia:id/search_container");
    private final By searchInput = By.id("org.wikipedia:id/search_src_text");

    public void skipFirstRun() {
        for (int i = 0; i < 5; i++) {
            if (exists(skip)) {
                click(skip);
                return;
            }
            if (exists(next)) {
                click(next);
            } else {
                return;
            }
        }
    }

    public void searchFor(String article) {
        skipFirstRun();

        try {
            if (exists(searchButton)) {
                click(searchButton);
            } else {
                tapByAdb(540, 2230);
            }
            if (exists(searchBar)) {
                click(searchBar);
            }
            // ensure the search container is visible before locating the input
            visible(searchBar);
            WebElement input = visible(searchInput);
            input.clear();
            input.sendKeys(article);
            return;
        } catch (TimeoutException ignored) {
            // Fallback for the current Wikipedia app: use the Search tab coordinates directly.
        }

        tapByAdb(540, 2230);
        // wait for the search container opened by the tap
        visible(searchBar);
        WebElement input = visible(searchInput);
        input.clear();
        input.sendKeys(article);
    }
}
