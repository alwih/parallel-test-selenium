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

  @positive
  Scenario: A product opens with the name and price shown in the list
    Then opening the first product should show its name and price from the list
    When I go back to the products
    Then I should see the products page
    And I should see a product list with a price for each product

  @edge
  Scenario: The same products are listed after opening one and going back
    The order is not asserted: SauceDemo resets the sort when the list is shown again.

    Then opening a product after sorting by "Price (high to low)" and going back should list the same products
