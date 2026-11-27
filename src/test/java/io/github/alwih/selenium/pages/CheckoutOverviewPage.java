package io.github.alwih.selenium.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckoutOverviewPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='cart-list'] [data-test='inventory-item-price']");
    private static final By ITEM_TOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");
    private static final By CANCEL = By.cssSelector("[data-test='cancel']");

    public CheckoutOverviewPage(WebDriver driver) {
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

    public BigDecimal itemTotal() {
        return amount(ITEM_TOTAL);
    }

    public BigDecimal tax() {
        return amount(TAX);
    }

    public BigDecimal total() {
        return amount(TOTAL);
    }

    public CheckoutCompletePage finish() {
        clickable(FINISH).click();
        return new CheckoutCompletePage(driver);
    }

    public InventoryPage cancel() {
        loaded();
        clickable(CANCEL).click();
        return new InventoryPage(driver);
    }

    // the labels read "Item total: $39.98", "Tax: $3.20", "Total: $43.18"
    private BigDecimal amount(By label) {
        loaded();
        String text = visible(label).getText();
        return parsePrice(text.substring(text.indexOf('$')));
    }

    // the URL changes before the page renders and the info page's elements linger, so wait for this page's own button (an empty cart has no items to wait for)
    private void loaded() {
        visible(FINISH);
    }
}
