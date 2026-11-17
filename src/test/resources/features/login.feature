@login
Feature: Login

  Background:
    Given I open the login page

  @smoke @positive
  Scenario: The standard user logs in and sees the products
    When I log in as "standard_user"
    Then I should see the products page

  @negative
  Scenario Outline: Login is rejected for <note>
    When I log in as "<username>" with password "<password>"
    Then I should stay on the login page
    And the login error should be "<error>"

    Examples:
      | username        | password     | error                                                                     | note              |
      | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.                       | a locked-out user |
      | standard_user   | wrong        | Epic sadface: Username and password do not match any user in this service | a wrong password  |
      | nobody          | secret_sauce | Epic sadface: Username and password do not match any user in this service | an unknown user   |
      |                 | secret_sauce | Epic sadface: Username is required                                        | an empty username |
      | standard_user   |              | Epic sadface: Password is required                                        | an empty password |

  @edge
  Scenario: Login is rejected for a username with surrounding spaces
    The site does not trim the username.

    When I log in as " standard_user "
    Then I should stay on the login page
    And the login error should be "Epic sadface: Username and password do not match any user in this service"
