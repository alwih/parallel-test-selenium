package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.LoginPage;
import org.testng.Assert;

public class LoginSteps {

    @Given("I open the login page")
    public void openLoginPage() {
        new LoginPage(DriverManager.get()).open();
    }

    @Then("I should see the login form")
    public void shouldSeeLoginForm() {
        LoginPage loginPage = new LoginPage(DriverManager.get());
        Assert.assertEquals(loginPage.currentPath(), "/", "Login page path");
        loginPage.waitForForm();
    }
}
