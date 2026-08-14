package test.mobile.pages;

import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By skip = By.xpath("//*[@text='Skip' or @content-desc='Skip']");
    private final By next = By.xpath("//*[@text='Next' or @content-desc='Next' or @text='Continue' or @content-desc='Continue']");
    private final By searchButton = By.xpath(
            "//*[@text='Search' or @content-desc='Search' or contains(@content-desc,'Search')]");
    private final By searchBar = By.xpath(
            "//*[@text='Search Wikipedia' or @content-desc='Search Wikipedia' or " +
            "@text='Search for an article' or @content-desc='Search for an article']");
    private final By searchInput = By.xpath(
            "//android.widget.EditText[@resource-id='org.wikipedia:id/search_src_text' or " +
            "@resource-id='org.wikipedia:id/search_input' or @hint='Search Wikipedia']");

    /** Handles the first-run onboarding: click Skip when it is available. */
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

    /** Exact requested flow: Skip -> Search button -> Search bar -> type article. */
    public void searchFor(String article) {
        skipFirstRun();

        click(searchButton);
        if (exists(searchBar)) {
            click(searchBar);
        }

        visible(searchInput).clear();
        visible(searchInput).sendKeys(article);
    }
}
