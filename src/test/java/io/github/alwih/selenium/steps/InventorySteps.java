package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.InventoryPage;
import org.testng.Assert;

public class InventorySteps {

    @Then("I should see the products page")
    public void shouldSeeProductsPage() {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        Assert.assertEquals(inventoryPage.currentPath(), "/inventory.html", "Products page path");
        Assert.assertEquals(inventoryPage.title(), "Products", "Products page title");
    }
}
