package io.github.alwih.selenium.pages;

import io.github.alwih.selenium.config.Config;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    // scoped to the list: the product page reuses these data-test names and shows briefly while the list is still absent
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-list'] [data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-list'] [data-test='inventory-item-price']");
    private static final By ITEM_IMAGE = By.cssSelector("[data-test='inventory-list'] [data-test$='-img']");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
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

    public List<String> productNames() {
        return all(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public List<BigDecimal> productPrices() {
        return all(ITEM_PRICE).stream().map(price -> new BigDecimal(price.getText().substring(1))).toList();
    }

    public List<String> productImages() {
        return all(ITEM_IMAGE).stream().map(image -> image.getAttribute("src")).toList();
    }

    public void sortBy(String optionText) {
        new Select(visible(SORT)).selectByVisibleText(optionText);
    }

    public ProductPage openFirstProduct() {
        all(ITEM_NAME).get(0).click();
        return new ProductPage(driver);
    }

    // the menu slides in, so the link is not clickable right after the menu button
    public void logOut() {
        clickable(MENU_BUTTON).click();
        clickable(LOGOUT_LINK).click();
    }
}
