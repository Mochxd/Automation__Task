package org.amazon.test.engine.ui.pages;

import io.qameta.allure.Step;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.ui.driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class CartPage {

    private final Driver driver;

    /* ======== Locators ======== */
    private static final By CART_ITEM_TITLE         = By.cssSelector("span.a-truncate-cut");
    private static final By CART_ITEM_TITLE_ALT     = By.cssSelector("span.sc-product-title");
    private static final By CART_ROW                = By.cssSelector(".sc-list-item");
    private static final By PROCEED_TO_CHECKOUT_BTN = By.name("proceedToRetailCheckout");

    public CartPage(Driver driver) {
        this.driver = driver;
    }

    @Step("Verify added products are in cart")
    public CartPage verifyProductsInCart(List<String> expected) {
        List<String> inCart = getCartTitles();
        logsUtils.info("Cart items (" + inCart.size() + "): " + inCart);
        logsUtils.info("Expected  (" + expected.size() + "): " + expected);

        Assert.assertFalse(expected.isEmpty(), "No products were added — cannot verify cart.");
        Assert.assertTrue(inCart.size() > 0, "Cart is empty.");

        int matched = 0;
        for (String exp : expected) {
            if (exp == null || exp.isBlank()) continue;
            String expLow = exp.toLowerCase();
            boolean found = inCart.stream().anyMatch(name -> {
                if (name == null) return false;
                String nameLow = name.toLowerCase();
                return nameLow.contains(expLow) || expLow.contains(nameLow) || fuzzyMatch(nameLow, expLow);
            });
            if (found) { matched++; logsUtils.info("  ✓ " + exp); }
            else        { logsUtils.warn("  ✗ Not found: " + exp); }
        }
        long nonBlank = expected.stream().filter(s -> s != null && !s.isBlank()).count();
        logsUtils.info("Matched " + matched + "/" + nonBlank + " expected items in cart.");
        Assert.assertEquals(matched, (int) nonBlank,
                "Not all products found in cart — missing " + (nonBlank - matched) + " item(s).");
        return this;
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        driver.element().clickByJS(PROCEED_TO_CHECKOUT_BTN);
        logsUtils.info("Proceeding to checkout");
        return new CheckoutPage(driver);
    }

    /* ---- helpers ---- */

    private List<String> getCartTitles() {
        List<String> titles = driver.element().findElements(CART_ITEM_TITLE).stream()
                .map(WebElement::getText).filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());
        if (!titles.isEmpty()) return titles;

        titles = driver.element().findElements(CART_ITEM_TITLE_ALT).stream()
                .map(WebElement::getText).filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());
        if (!titles.isEmpty()) return titles;

        return driver.element().findElements(CART_ROW).stream().map(row -> {
            try { return row.findElement(By.cssSelector(".sc-product-title, span.a-truncate-cut")).getText(); }
            catch (NoSuchElementException e) { return null; }
        }).filter(s -> s != null && !s.isBlank()).collect(Collectors.toList());
    }

    private boolean fuzzyMatch(String a, String b) {
        int hits = 0;
        for (String word : a.split("\\s+")) {
            if (word.length() > 3 && b.contains(word)) hits++;
        }
        return hits >= 3;
    }
}
