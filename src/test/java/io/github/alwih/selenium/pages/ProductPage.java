package io.github.alwih.selenium.pages;

import java.math.BigDecimal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage extends BasePage {

    // the detail page reuses the list's data-test names, so scope them to the detail container
    private static final By NAME = By.cssSelector(".inventory_details_container [data-test='inventory-item-name']");
    private static final By PRICE = By.cssSelector(".inventory_details_container [data-test='inventory-item-price']");
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

    public InventoryPage backToProducts() {
        clickable(BACK_BUTTON).click();
        return new InventoryPage(driver);
    }
}
