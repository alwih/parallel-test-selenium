@login
Feature: Login

  @smoke @positive
  Scenario: The login page shows the login form
    Given I open the login page
    Then I should see the login form
