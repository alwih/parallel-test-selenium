package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.config.Config;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.LoginPage;

public class LoginSteps {

    @Given("I open the login page")
    public void openLoginPage() {
        new LoginPage(DriverManager.get()).open();
    }

    @When("I log in as {string}")
    public void logInAs(String username) {
        new LoginPage(DriverManager.get()).logIn(username, Config.get("sauce.password"));
    }
}
