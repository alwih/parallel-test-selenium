package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.CartPage;
import io.github.alwih.selenium.pages.CheckoutCompletePage;
import io.github.alwih.selenium.pages.CheckoutInfoPage;
import io.github.alwih.selenium.pages.CheckoutOverviewPage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;

public class CheckoutSteps {

    private final ScenarioCart cart;

    public CheckoutSteps(ScenarioCart cart) {
        this.cart = cart;
    }

    @When("I start the checkout")
    public void startCheckout() {
        new CartPage(DriverManager.get()).checkout();
    }

    @When("I continue with first name {string}, last name {string} and postal code {string}")
    public void continueWithDetails(String firstName, String lastName, String postalCode) {
        CheckoutInfoPage infoPage = new CheckoutInfoPage(DriverManager.get());
        infoPage.fillIn(firstName, lastName, postalCode);
        infoPage.continueToOverview();
    }

    @When("I finish the order")
    public void finishOrder() {
        new CheckoutOverviewPage(DriverManager.get()).finish();
        cart.added.clear();
    }

    @When("I go back home")
    public void goBackHome() {
        new CheckoutCompletePage(DriverManager.get()).backHome();
    }

    @Then("I should see the checkout information page")
    public void shouldSeeInformationPage() {
        CheckoutInfoPage infoPage = new CheckoutInfoPage(DriverManager.get());
        Assert.assertEquals(infoPage.currentPath(), "/checkout-step-one.html", "Checkout information page path");
        Assert.assertEquals(infoPage.title(), "Checkout: Your Information", "Checkout information page title");
    }

    @Then("I should see the checkout error {string}")
    public void shouldSeeCheckoutError(String error) {
        Assert.assertEquals(new CheckoutInfoPage(DriverManager.get()).error(), error, "Checkout error");
    }

    @Then("I should see the order overview")
    public void shouldSeeOverview() {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(DriverManager.get());
        Assert.assertEquals(overviewPage.currentPath(), "/checkout-step-two.html", "Order overview path");
        Assert.assertEquals(overviewPage.title(), "Checkout: Overview", "Order overview title");
    }

    @Then("the overview should list the products I added with their prices")
    public void overviewShouldListAddedProducts() {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(DriverManager.get());
        List<String> names = overviewPage.itemNames();
        List<BigDecimal> prices = overviewPage.itemPrices();
        Assert.assertEquals(names, new ArrayList<>(cart.added.keySet()), "Products in the overview, found " + names);
        Assert.assertEquals(prices, new ArrayList<>(cart.added.values()), "Prices in the overview, found " + prices);
    }

    @Then("the item total should be the sum of the prices of the products I added")
    public void itemTotalShouldBeSumOfPrices() {
        BigDecimal sum = cart.added.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertSameAmount("Item total", new CheckoutOverviewPage(DriverManager.get()).itemTotal(), sum);
    }

    @Then("the total should be the item total plus tax")
    public void totalShouldBeItemTotalPlusTax() {
        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(DriverManager.get());
        BigDecimal itemTotal = overviewPage.itemTotal();
        BigDecimal tax = overviewPage.tax();
        assertSameAmount("Total (item total " + itemTotal + " + tax " + tax + ")", overviewPage.total(), itemTotal.add(tax));
    }

    @Then("I should see the order confirmation")
    public void shouldSeeConfirmation() {
        CheckoutCompletePage completePage = new CheckoutCompletePage(DriverManager.get());
        Assert.assertEquals(completePage.currentPath(), "/checkout-complete.html", "Order confirmation path");
        Assert.assertEquals(completePage.title(), "Checkout: Complete!", "Order confirmation title");
        Assert.assertEquals(completePage.header(), "Thank you for your order!", "Order confirmation header");
    }

    // compareTo, not equals: 0 and 0.00 are different BigDecimals
    private void assertSameAmount(String what, BigDecimal actual, BigDecimal expected) {
        Assert.assertTrue(actual.compareTo(expected) == 0, what + " expected " + expected + " but found " + actual);
    }
}
