package io.github.alwih.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return visible(TITLE).getText();
    }
}
