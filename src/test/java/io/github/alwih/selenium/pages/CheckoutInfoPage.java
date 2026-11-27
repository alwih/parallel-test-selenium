package io.github.alwih.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutInfoPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By CANCEL = By.cssSelector("[data-test='cancel']");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    public CheckoutInfoPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        loaded();
        return visible(TITLE).getText();
    }

    public void fillIn(String firstName, String lastName, String postalCode) {
        loaded();
        visible(FIRST_NAME).sendKeys(firstName);
        visible(LAST_NAME).sendKeys(lastName);
        visible(POSTAL_CODE).sendKeys(postalCode);
    }

    public CheckoutOverviewPage continueToOverview() {
        clickable(CONTINUE).click();
        return new CheckoutOverviewPage(driver);
    }

    public CartPage cancel() {
        clickable(CANCEL).click();
        return new CartPage(driver);
    }

    public String error() {
        return visible(ERROR).getText();
    }

    // the URL changes before the page renders and the previous page's elements linger, so wait for this page's own button (the overview also has cancel)
    private void loaded() {
        visible(CONTINUE);
    }
}
