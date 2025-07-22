@Login
Feature: Login functionality

  @ValidLogin
  Scenario Outline: Verify Valid Login Credentials
    Given User launches "<browser>" browser and opens the Login Page
    When User enters username "<username>" and password "<password>"
    And Clicks on Login button
    Then User should be navigated to Accounts Page

    Examples:
      | browser | username | password  |
      | Chrome  |gawedo3803@lxheir.com|Password|
      |Edge|gawedo3803@lxheir.com|Password|
      
      
      