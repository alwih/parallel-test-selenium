@inventory
Feature: Inventory for the problem user

  Background:
    Given I open the login page
    And I log in as "problem_user"

  @known_issue @negative
  Scenario: Sorting by price reorders the products
    Actual: no sort changes the list, and the sort dropdown goes back to "Name (A to Z)".

    Then sorting the products by "Price (low to high)" should list them in that order

  @known_issue @negative
  Scenario: Every product shows its own image
    Actual: all products show the same placeholder image (sl-404).

    Then every product should have its own image
