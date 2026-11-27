@checkout
Feature: Checkout

  Background:
    Given I open the login page
    And I log in as "standard_user"

  @smoke @positive
  Scenario: An order is completed and totals add up
    When I add the first 2 products to the cart
    And I open the cart
    And I start the checkout
    Then I should see the checkout information page
    When I continue with first name "Ada", last name "Lovelace" and postal code "12345"
    Then I should see the order overview
    And the overview should list the products I added with their prices
    And the item total should be the sum of the prices of the products I added
    And the total should be the item total plus tax
    When I finish the order
    Then I should see the order confirmation
    And the cart badge should show 0
    When I open the cart
    Then the cart should be empty

  @positive
  Scenario: Going back home after an order shows the products page with an empty cart
    When I add the first 2 products to the cart
    And I open the cart
    And I start the checkout
    And I continue with first name "Ada", last name "Lovelace" and postal code "12345"
    And I finish the order
    And I go back home
    Then I should see the products page
    And the cart badge should show 0
    When I open the cart
    Then the cart should be empty
