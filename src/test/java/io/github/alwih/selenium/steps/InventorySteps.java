package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.InventoryPage;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.testng.Assert;

public class InventorySteps {

    @When("I open the products page directly")
    public void openProductsPageDirectly() {
        new InventoryPage(DriverManager.get()).open();
    }

    @When("I log out")
    public void logOut() {
        new InventoryPage(DriverManager.get()).logOut();
    }

    @Then("I should see the products page")
    public void shouldSeeProductsPage() {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        Assert.assertEquals(inventoryPage.currentPath(), "/inventory.html", "Products page path");
        Assert.assertEquals(inventoryPage.title(), "Products", "Products page title");
    }

    @Then("I should see a product list with a price for each product")
    public void shouldSeeProductListWithPrices() {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        List<String> names = inventoryPage.productNames();
        Assert.assertFalse(names.isEmpty(), "Products on the list");
        Assert.assertEquals(inventoryPage.productPrices().size(), names.size(), "Prices for " + names);
    }

    @Then("every product should have a name and a price above zero")
    public void everyProductShouldHaveNameAndPrice() {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        List<String> names = inventoryPage.productNames();
        List<BigDecimal> prices = inventoryPage.productPrices();
        Assert.assertTrue(names.stream().noneMatch(String::isBlank), "Product names, none blank: " + names);
        Assert.assertTrue(prices.stream().allMatch(price -> price.signum() > 0), "Prices, all above zero: " + prices);
    }

    // capture, sort and compare live in one step because steps keep no state between them
    @Then("sorting the products by {string} should list them in that order")
    public void sortingShouldListProductsInOrder(String option) {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        boolean ascending = option.endsWith("(A to Z)") || option.endsWith("(low to high)");
        if (option.startsWith("Name")) {
            List<String> expected = sorted(inventoryPage.productNames(), ascending);
            if (option.equals("Name (A to Z)")) {
                // A to Z is the default order, so move away from it first or this row passes without sorting
                inventoryPage.sortBy("Name (Z to A)");
            }
            inventoryPage.sortBy(option);
            assertOrder(inventoryPage.productNames(), expected, "Product names sorted by " + option);
        } else {
            // two products share a price, so prices are compared and not names
            List<BigDecimal> expected = sorted(inventoryPage.productPrices(), ascending);
            inventoryPage.sortBy(option);
            assertOrder(inventoryPage.productPrices(), expected, "Product prices sorted by " + option);
        }
    }

    private static <T> void assertOrder(List<T> actual, List<T> expected, String what) {
        Assert.assertEquals(actual, expected, what + ", expected " + expected + " but found " + actual);
    }

    private static <T extends Comparable<T>> List<T> sorted(List<T> list, boolean ascending) {
        return list.stream().sorted(ascending ? Comparator.<T>naturalOrder() : Comparator.<T>reverseOrder()).toList();
    }
}
