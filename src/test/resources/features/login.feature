@login
Feature: Login

  @smoke @positive
  Scenario: The standard user logs in and sees the products
    Given I open the login page
    When I log in as "standard_user"
    Then I should see the products page
