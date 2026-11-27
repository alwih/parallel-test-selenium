package io.github.alwih.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutCompletePage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By HEADER = By.cssSelector("[data-test='complete-header']");
    private static final By BACK_HOME = By.cssSelector("[data-test='back-to-products']");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        visible(HEADER);
        return visible(TITLE).getText();
    }

    // the URL changes before the page renders and the overview's title lingers, so reading the header doubles as the wait
    public String header() {
        return visible(HEADER).getText();
    }

    public InventoryPage backHome() {
        clickable(BACK_HOME).click();
        return new InventoryPage(driver);
    }
}
