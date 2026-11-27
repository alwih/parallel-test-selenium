package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.InventoryPage;
import io.github.alwih.selenium.pages.ProductPage;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
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

    @When("I go back to the products")
    public void goBackToProducts() {
        new ProductPage(DriverManager.get()).backToProducts();
    }

    @When("I open the first product")
    public void openFirstProduct() {
        new InventoryPage(DriverManager.get()).openFirstProduct();
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

    // the list is captured before the click, and steps keep no state, so capture, open and compare share one step
    @Then("opening the first product should show its name and price from the list")
    public void openingFirstProductShouldShowListedNameAndPrice() {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        String name = inventoryPage.productNames().get(0);
        BigDecimal price = inventoryPage.productPrices().get(0);
        ProductPage productPage = inventoryPage.openFirstProduct();
        Assert.assertEquals(productPage.name(), name, "Product name on the product page");
        Assert.assertEquals(productPage.price(), price, "Product price on the product page for " + name);
        Assert.assertEquals(productPage.currentPath(), "/inventory-item.html", "Product page path");
    }

    // same reason as above; names are compared as sets of products because the sort is not kept (see the feature)
    @Then("opening a product after sorting by {string} and going back should list the same products")
    public void openingProductAndGoingBackShouldListSameProducts(String option) {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        List<String> before = inventoryPage.productNames();
        inventoryPage.sortBy(option);
        List<String> after = inventoryPage.openFirstProduct().backToProducts().productNames();
        assertOrder(sorted(after, true), sorted(before, true), "Products after going back, in any order");
    }

    @Then("every product should have its own image")
    public void everyProductShouldHaveOwnImage() {
        List<String> images = new InventoryPage(DriverManager.get()).productImages();
        Assert.assertEquals(new HashSet<>(images).size(), images.size(), "Distinct images among the products, found " + images);
    }

    private static <T> void assertOrder(List<T> actual, List<T> expected, String what) {
        Assert.assertEquals(actual, expected, what + ", expected " + expected + " but found " + actual);
    }

    private static <T extends Comparable<T>> List<T> sorted(List<T> list, boolean ascending) {
        return list.stream().sorted(ascending ? Comparator.<T>naturalOrder() : Comparator.<T>reverseOrder()).toList();
    }
}
