Feature: Wikipedia Android mobile automation

  @mobile @smoke
  Scenario: Search, open, scroll and validate an article
    Given the Wikipedia application is launched
    When I search for article "Artificial Intelligence"
    And I open the first search result for "Artificial Intelligence"
    And I scroll the article page
    Then the article title "Artificial Intelligence" should be displayed
    And I save the article
    And I close the application

  @mobile @reading-list
  Scenario: Save an article to a new reading list and remove it
    Given the Wikipedia application is launched
    When I search for article "Artificial Intelligence"
    And I open the first search result for "Artificial Intelligence"
    And I save the article
    And I add the article to a new reading list "QA Automation Assessment"
    And I open the Reading Lists section
    And I search for reading list "QA Automation Assessment"
    Then the article "Artificial Intelligence" should be displayed in the reading list
    When I add the same article to the reading list again
    Then the article "Artificial Intelligence" should appear only once in the reading list
    When I remove the article from the reading list
    Then the article "Artificial Intelligence" should no longer be displayed in the reading list
    And I close the application
