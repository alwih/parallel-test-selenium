package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.config.Config;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.LoginPage;
import org.testng.Assert;

public class LoginSteps {

    @Given("I open the login page")
    public void openLoginPage() {
        new LoginPage(DriverManager.get()).open();
    }

    @When("I log in as {string}")
    public void logInAs(String username) {
        new LoginPage(DriverManager.get()).logIn(username, Config.get("sauce.password"));
    }

    @When("I log in as {string} with password {string}")
    public void logInWithPassword(String username, String password) {
        new LoginPage(DriverManager.get()).logIn(username, password);
    }

    @Then("I should stay on the login page")
    public void shouldStayOnLoginPage() {
        LoginPage loginPage = new LoginPage(DriverManager.get());
        Assert.assertEquals(loginPage.currentPath(), "/", "Login page path");
        loginPage.waitForForm();
    }

    @Then("the login error should be {string}")
    public void loginErrorShouldBe(String error) {
        Assert.assertEquals(new LoginPage(DriverManager.get()).error(), error, "Login error");
    }
}
