package io.github.alwih.selenium.pages;

import io.github.alwih.selenium.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK = By.cssSelector("[data-test='logout-sidebar-link']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(Config.get("base.url") + "/inventory.html");
    }

    public String title() {
        return visible(TITLE).getText();
    }

    // the menu slides in, so the link is not clickable right after the menu button
    public void logOut() {
        clickable(MENU_BUTTON).click();
        clickable(LOGOUT_LINK).click();
    }
}
