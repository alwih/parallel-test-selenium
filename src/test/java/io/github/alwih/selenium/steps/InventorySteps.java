package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.InventoryPage;
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
}
