package io.github.alwih.selenium.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.alwih.selenium.driver.DriverManager;
import io.github.alwih.selenium.pages.CartPage;
import io.github.alwih.selenium.pages.InventoryPage;
import io.github.alwih.selenium.pages.ProductPage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.Assert;

public class CartSteps {

    private final Map<String, BigDecimal> added;

    public CartSteps(ScenarioCart cart) {
        this.added = cart.added;
    }

    @When("I add the first {int} product(s) to the cart")
    public void addFirstProductsToCart(int count) {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        List<String> names = inventoryPage.productNames();
        List<BigDecimal> prices = inventoryPage.productPrices();
        for (int i = 0; i < count; i++) {
            inventoryPage.addToCart(names.get(i));
            added.put(names.get(i), prices.get(i));
        }
    }

    @When("I add the first product to the cart from the {word} page")
    public void addFirstProductToCartFrom(String page) {
        if (page.equals("list")) {
            addFirstProductsToCart(1);
            return;
        }
        Assert.assertEquals(page, "detail", "Page to add from");
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        String name = inventoryPage.productNames().get(0);
        BigDecimal price = inventoryPage.productPrices().get(0);
        inventoryPage.openFirstProduct().addToCart();
        added.put(name, price);
    }

    // the products added are the first ones listed, so the first product on the list or detail page is the first one added
    @When("I remove the first added product from the {word} page")
    public void removeFirstAddedProductFrom(String page) {
        String name = added.keySet().iterator().next();
        InventoryPage inventoryPage = new InventoryPage(DriverManager.get());
        switch (page) {
            case "list" -> inventoryPage.removeFromCart(name);
            case "detail" -> inventoryPage.openFirstProduct().removeFromCart();
            case "cart" -> new CartPage(DriverManager.get()).remove(name);
            default -> Assert.fail("Unknown page to remove from: " + page);
        }
        added.remove(name);
    }

    // the cart link is in the header of every page, so any page object can open it
    @When("I open the cart")
    public void openCart() {
        new InventoryPage(DriverManager.get()).openCart();
    }

    @When("I reload the page")
    public void reloadPage() {
        new InventoryPage(DriverManager.get()).reload();
    }

    @When("I continue shopping")
    public void continueShopping() {
        new CartPage(DriverManager.get()).continueShopping();
    }

    @Then("the cart badge should show {int}")
    public void cartBadgeShouldShow(int count) {
        Assert.assertEquals(new InventoryPage(DriverManager.get()).cartCount(), count, "Cart badge (0 means no badge)");
    }

    @Then("the cart should be empty")
    public void cartShouldBeEmpty() {
        CartPage cartPage = new CartPage(DriverManager.get());
        Assert.assertEquals(cartPage.itemNames(), List.of(), "Products in the cart");
        Assert.assertEquals(cartPage.cartCount(), 0, "Cart badge (0 means no badge)");
    }

    @Then("the product page should offer to remove it from the cart")
    public void productPageShouldOfferRemove() {
        Assert.assertTrue(new ProductPage(DriverManager.get()).inCart(), "Product page shows Remove for a product in the cart");
    }

    @Then("I should see the cart page")
    public void shouldSeeCartPage() {
        CartPage cartPage = new CartPage(DriverManager.get());
        Assert.assertEquals(cartPage.currentPath(), "/cart.html", "Cart page path");
        Assert.assertEquals(cartPage.title(), "Your Cart", "Cart page title");
    }

    @Then("the cart should list the products I added with their prices")
    public void cartShouldListAddedProducts() {
        CartPage cartPage = new CartPage(DriverManager.get());
        List<String> names = cartPage.itemNames();
        List<BigDecimal> prices = cartPage.itemPrices();
        Assert.assertEquals(names, new ArrayList<>(added.keySet()), "Products in the cart, found " + names);
        Assert.assertEquals(prices, new ArrayList<>(added.values()), "Prices in the cart, found " + prices);
    }
}
