package io.github.alwih.selenium.pages;

import io.github.alwih.selenium.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(Config.get("base.url"));
        return this;
    }

    public LoginPage waitForForm() {
        visible(USERNAME);
        visible(PASSWORD);
        visible(LOGIN_BUTTON);
        return this;
    }
}
