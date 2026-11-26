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

  @positive
  Scenario Outline: Products can be sorted by <note>
    Then sorting the products by "<option>" should list them in that order

    Examples:
      | option              | note                   |
      | Name (A to Z)       | name from A to Z       |
      | Name (Z to A)       | name from Z to A       |
      | Price (low to high) | price from low to high |
      | Price (high to low) | price from high to low |
