package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.github.alwih.selenium.config.Config;
import io.github.alwih.selenium.driver.DriverManager;
import java.net.URI;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class LoginSteps {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");

    @Given("I open the login page")
    public void openLoginPage() {
        DriverManager.get().get(Config.get("base.url"));
    }

    @Then("I should see the login form")
    public void shouldSeeLoginForm() {
        WebDriver driver = DriverManager.get();
        Assert.assertEquals(URI.create(driver.getCurrentUrl()).getPath(), "/", "Login page path");
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(USERNAME),
                ExpectedConditions.visibilityOfElementLocated(PASSWORD),
                ExpectedConditions.visibilityOfElementLocated(LOGIN_BUTTON)));
    }
}
