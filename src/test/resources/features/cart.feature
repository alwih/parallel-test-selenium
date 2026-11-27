@cart
Feature: Cart

  Background:
    Given I open the login page
    And I log in as "standard_user"

  @smoke @positive
  Scenario: Products added from the list are in the cart
    When I add the first 2 products to the cart
    Then the cart badge should show 2
    When I open the cart
    Then I should see the cart page
    And the cart should list the products I added with their prices

  @positive
  Scenario Outline: A product added from <note> is in the cart
    When I add the first product to the cart from the <page> page
    Then the cart badge should show 1
    When I open the cart
    Then the cart should list the products I added with their prices

    Examples:
      | page   | note                    |
      | list   | the product list        |
      | detail | the product detail page |

  @positive
  Scenario Outline: A product removed from <note> leaves the cart
    When I add the first 2 products to the cart
    And I remove the first added product from the <page> page
    Then the cart badge should show 1
    When I open the cart
    Then the cart should list the products I added with their prices

    Examples:
      | page   | note                    |
      | list   | the product list        |
      | detail | the product detail page |

  @positive
  Scenario: A product removed from the cart page leaves the cart
    When I add the first 2 products to the cart
    And I open the cart
    And I remove the first added product from the cart page
    Then the cart badge should show 1
    And the cart should list the products I added with their prices

  @positive
  Scenario: The cart keeps its products after opening one and going back
    When I add the first 2 products to the cart
    And I open the first product
    And I go back to the products
    Then I should see the products page
    And the cart badge should show 2
    When I open the cart
    Then the cart should list the products I added with their prices

  @positive
  Scenario: A product in the cart offers Remove on its product page
    When I add the first 2 products to the cart
    And I open the first product
    Then the product page should offer to remove it from the cart

  @positive
  Scenario: The cart keeps its products when I continue shopping
    When I add the first 2 products to the cart
    And I open the cart
    And I continue shopping
    Then I should see the products page
    And the cart badge should show 2

  @edge
  Scenario: A new cart is empty
    When I open the cart
    Then I should see the cart page
    And the cart badge should show 0
    And the cart should list the products I added with their prices

  @edge
  Scenario: Removing the last product empties the cart
    When I add the first 1 product to the cart
    And I open the cart
    And I remove the first added product from the cart page
    Then the cart badge should show 0
    And the cart should list the products I added with their prices

  @edge
  Scenario: The products page keeps the cart after a reload
    When I add the first 2 products to the cart
    And I reload the page
    Then I should see the products page
    And the cart badge should show 2
    When I open the cart
    Then the cart should list the products I added with their prices

  @edge
  Scenario: The cart page keeps the cart after a reload
    When I add the first 2 products to the cart
    And I open the cart
    And I reload the page
    Then I should see the cart page
    And the cart badge should show 2
    And the cart should list the products I added with their prices
