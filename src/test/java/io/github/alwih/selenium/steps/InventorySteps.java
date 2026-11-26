package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.InventoryPage;
import java.math.BigDecimal;
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
}
