package test.mobile.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ReadingListsPage extends BasePage {
    private final By savedTab = By.xpath(
            "//*[@text='Saved' or @content-desc='Saved' or contains(@content-desc,'Reading list')]");
    private final By createList = By.xpath(
            "//*[@text='Create a new list' or @text='Create new list' or @content-desc='Create a new list' or @content-desc='Create new list']");
    private final By listNameInput = By.xpath("//android.widget.EditText");
    private final By confirm = By.xpath("//*[@text='Create' or @text='OK' or @content-desc='Create']");

    public void openSaved() {
        click(savedTab);
    }

    public void createList(String name) {
        if (!exists(createList)) {
            By add = By.xpath("//*[@content-desc='Add' or @text='Add']");
            if (exists(add)) click(add);
        }
        click(createList);
        visible(listNameInput).sendKeys(name);
        click(confirm);
    }

    public void searchList(String name) {
        By search = By.xpath("//*[@content-desc='Search' or @text='Search']");
        if (exists(search)) click(search);
        By input = By.xpath("//android.widget.EditText");
        if (exists(input)) visible(input).sendKeys(name);
        click(By.xpath("//*[@text=\"" + name + "\" or contains(@text,\"" + name + "\")][1]"));
    }

    public boolean containsArticle(String article) {
        return exists(By.xpath("//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")]"));
    }

    public int countArticle(String article) {
        List<WebElement> elements = (List<WebElement>) By.xpath("//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")]");
        return elements.size();
    }

    public void removeArticle(String article) {
        By item = By.xpath("//*[@text=\"" + article + "\" or contains(@text,\"" + article + "\")][1]");
        click(item);
        By remove = By.xpath(
                "//*[@text='Remove' or @text='Remove from list' or @content-desc='Remove' or contains(@content-desc,'Remove')]");
        if (exists(remove)) {
            click(remove);
        } else {
            By menu = By.xpath("//*[@content-desc='More options' or @content-desc='More']");
            if (exists(menu)) {
                click(menu);
                click(remove);
            }
        }
    }
}
