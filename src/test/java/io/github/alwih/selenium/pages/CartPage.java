package io.github.alwih.selenium.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By CONTINUE_SHOPPING = By.cssSelector("[data-test='continue-shopping']");
    private static final By ITEM = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item-price']");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");
    private static final By REMOVE_BUTTON = By.cssSelector("[data-test^='remove']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        loaded();
        return visible(TITLE).getText();
    }

    public List<String> itemNames() {
        loaded();
        return driver.findElements(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public List<BigDecimal> itemPrices() {
        loaded();
        return driver.findElements(ITEM_PRICE).stream().map(price -> parsePrice(price.getText())).toList();
    }

    public void remove(String name) {
        loaded();
        itemNamed(ITEM, name).findElement(REMOVE_BUTTON).click();
    }

    public void continueShopping() {
        clickable(CONTINUE_SHOPPING).click();
    }

    public CheckoutInfoPage checkout() {
        clickable(CHECKOUT).click();
        return new CheckoutInfoPage(driver);
    }

    // the URL changes to /cart.html before the cart renders and the products page lingers in the DOM, so wait for the cart's own button (an empty cart has no items to wait for)
    private void loaded() {
        visible(CONTINUE_SHOPPING);
    }
}
