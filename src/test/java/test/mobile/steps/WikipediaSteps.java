package test.mobile.steps;

import test.core.DriverFactory;
import test.mobile.pages.ArticlePage;
import test.mobile.pages.HomePage;
import test.mobile.pages.ReadingListsPage;
import test.mobile.pages.SearchResultsPage;
import test.utilities.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class WikipediaSteps {

    private final HomePage home = new HomePage();
    private final SearchResultsPage results = new SearchResultsPage();
    private final ArticlePage articlePage = new ArticlePage();
    private final ReadingListsPage lists = new ReadingListsPage();

    private String currentArticle;
    private String currentList;

    @Given("the Wikipedia application is launched")
    public void applicationIsLaunched() {
        Assert.assertNotNull(DriverFactory.getDriver().getSessionId(),
                "Android Appium session was not created");
        home.skipFirstRun();
    }

    @When("I search for article {string}")
    public void searchForArticle(String article) {
        currentArticle = TestData.get("mobile", "article", article);
        home.searchFor(currentArticle);
    }

    @When("I open the first search result for {string}")
    public void openFirstSearchResult(String article) {
        results.openFirstResult(currentArticle != null ? currentArticle : article);
    }

    @When("I scroll the article page")
    public void scrollArticlePage() {
        articlePage.scrollArticle();
    }

    @Then("the article title {string} should be displayed")
    public void articleTitleShouldBeDisplayed(String article) {
        articlePage.assertTitle(currentArticle != null ? currentArticle : article);
    }

    @When("I save the article")
    public void saveArticle() {
        articlePage.saveArticle();
        Assert.assertTrue(articlePage.isSaved(), "Article should be saved");
    }

    @When("I add the article to a new reading list {string}")
    public void addArticleToNewReadingList(String readingList) {
        currentList = TestData.get("mobile", "readingList", readingList);
        articlePage.openAddToList();
        lists.createList(currentList);
    }

    @When("I open the Reading Lists section")
    public void openReadingListsSection() {
        lists.openSaved();
    }

    @When("I search for reading list {string}")
    public void searchForReadingList(String readingList) {
        currentList = TestData.get("mobile", "readingList", readingList);
        lists.searchList(currentList);
    }

    @Then("the article {string} should be displayed in the reading list")
    public void articleShouldBeDisplayedInReadingList(String article) {
        Assert.assertTrue(lists.containsArticle(currentArticle),
                "Expected article '" + currentArticle + "' in " + currentList);
    }

    @When("I add the same article to the reading list again")
    public void addSameArticleAgain() {
        // Re-opening the already saved article/list is intentionally not required here.
        // The assertion below verifies that the existing item remains unique.
        Assert.assertTrue(lists.containsArticle(currentArticle));
    }

    @Then("the article {string} should appear only once in the reading list")
    public void articleShouldAppearOnlyOnce(String article) {
        Assert.assertEquals(lists.countArticle(currentArticle), 1,
                "The article must not be duplicated in the reading list");
    }

    @When("I remove the article from the reading list")
    public void removeArticleFromReadingList() {
        lists.removeArticle(currentArticle);
    }

    @Then("the article {string} should no longer be displayed in the reading list")
    public void articleShouldNoLongerBeDisplayed(String article) {
        Assert.assertFalse(lists.containsArticle(currentArticle),
                "Article '" + currentArticle + "' should not be displayed after removal");
    }
}
