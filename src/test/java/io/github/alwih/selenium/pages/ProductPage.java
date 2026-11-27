package io.github.alwih.selenium.pages;

import java.math.BigDecimal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage extends BasePage {

    // the detail page reuses the list's data-test names, so scope them to the detail container
    private static final By NAME = By.cssSelector(".inventory_details_container [data-test='inventory-item-name']");
    private static final By PRICE = By.cssSelector(".inventory_details_container [data-test='inventory-item-price']");
    private static final By CART_BUTTON = By.cssSelector("[data-test='add-to-cart'], [data-test='remove']");
    private static final By ADD_BUTTON = By.cssSelector("[data-test='add-to-cart']");
    private static final By REMOVE_BUTTON = By.cssSelector("[data-test='remove']");
    private static final By BACK_BUTTON = By.cssSelector("[data-test='back-to-products']");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String name() {
        return visible(NAME).getText();
    }

    public BigDecimal price() {
        return new BigDecimal(visible(PRICE).getText().substring(1));
    }

    public void addToCart() {
        clickable(ADD_BUTTON).click();
    }

    public void removeFromCart() {
        clickable(REMOVE_BUTTON).click();
    }

    public boolean inCart() {
        return visible(CART_BUTTON).getAttribute("data-test").equals("remove");
    }

    public InventoryPage backToProducts() {
        clickable(BACK_BUTTON).click();
        return new InventoryPage(driver);
    }
}
