package io.github.alwih.selenium.pages;

import io.github.alwih.selenium.config.Config;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");

    protected final WebDriver driver;
    private final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Long.parseLong(Config.get("timeout.seconds"))));
    }

    public String currentPath() {
        return URI.create(driver.getCurrentUrl()).getPath();
    }

    // the badge is absent when the cart is empty and is updated by the click itself, so no wait: callers wait for their page first
    public int cartCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText());
    }

    public CartPage openCart() {
        clickable(CART_LINK).click();
        return new CartPage(driver);
    }

    protected WebElement itemNamed(By items, String name) {
        return all(items).stream()
                .filter(item -> item.findElement(ITEM_NAME).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No product named " + name));
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> all(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}
