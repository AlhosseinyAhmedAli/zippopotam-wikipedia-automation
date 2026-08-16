package test.mobile.pages;

import org.openqa.selenium.By;

public class ArticlePage extends BasePage {

    private final By save = By.xpath(
            "//*[@text='Save' or @content-desc='Save' or contains(@content-desc,'Save article')]");
    private final By saved = By.xpath(
            "//*[@text='Saved' or @content-desc='Saved' or contains(@content-desc,'Remove from saved')]");
    private final By addToList = By.xpath(
            "//*[@text='Add to list' or @content-desc='Add to list' or contains(@text,'Add to list')]");
    private final By more = By.xpath("//*[@content-desc='More options' or @content-desc='More']");

    public void dismissPopupIfPresent() {
        By gotIt = By.xpath("//*[@text='Got it' or @content-desc='Got it']");
        if (exists(gotIt)) {
            click(gotIt);
        }
    }

    public void scrollArticle() {
        dismissPopupIfPresent();
        scrollDown();
    }

    public void assertTitle(String expectedTitle) {
        if (isTitleDisplayed(expectedTitle)) {
            return;
        }

        test.core.Screenshot.attach("TitleNotFound: " + expectedTitle);
        throw new AssertionError("Article title is not displayed: " + expectedTitle);
    }

    public boolean isTitleDisplayed(String expectedTitle) {
        By title = By.xpath("(//*[@text=\"" + expectedTitle + "\" or contains(@text,\"" + expectedTitle + "\")])[1]");
        if (exists(title)) {
            return true;
        }

        // Try a case-insensitive search using xpath translate()
        String lower = expectedTitle.toLowerCase();
        By titleCi = By.xpath("//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"" + lower + "\")] ");
        if (exists(titleCi)) {
            return true;
        }

        // Try scrolling and re-check
        scrollToTop();
        return exists(title) || exists(titleCi);
    }

    public void saveArticle() {
        dismissPopupIfPresent();
        if (exists(save)) {
            click(save);
        }
    }

    public boolean isSaved() {
        return exists(saved) || !exists(save);
    }

    public void openAddToList() {
        if (exists(addToList)) {
            click(addToList);
            return;
        }

        if (exists(more)) {
            click(more);
            if (exists(addToList)) {
                click(addToList);
            }
        }
    }
}
