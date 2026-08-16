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
        // Ensure the Wikipedia app is in foreground (some devices may switch to launcher/search overlay)
        launchActivity("org.wikipedia", "org.wikipedia.main.MainActivity");
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
            // Prefer waiting for the search input directly (more stable across app versions)
            WebElement input = visible(searchInput);
            input.clear();
            input.sendKeys(article);

            // Wait for the search result matching the article to appear and click it
            try {
                By result = By.xpath("(//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")])[1]");
                visible(result);
                click(result);
            } catch (Exception ignored) {
                // ignore: caller may click explicitly
            }
            return;
        } catch (TimeoutException ignored) {
            // Fallback for the current Wikipedia app: use the Search tab coordinates directly.
        }

        tapByAdb(540, 2230);
        // wait for the search input opened by the tap
        try {
            WebElement input = visible(searchInput);
            input.clear();
            input.sendKeys(article);
        } catch (Exception e) {
            test.core.Screenshot.attach("SearchFor-Failure: " + article);
            throw e;
        }
    }
}
