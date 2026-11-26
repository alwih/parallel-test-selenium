@inventory
Feature: Inventory

  Background:
    Given I open the login page
    And I log in as "standard_user"

  @smoke @positive
  Scenario: The product list shows every product with a name and a price
    Then I should see the products page
    And I should see a product list with a price for each product
    And every product should have a name and a price above zero
