package org.amazon.test.engine.ui.pages;

import io.qameta.allure.Step;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.ui.driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutPage {

    private final Driver driver;

    /* ======== Address form ======== */
    /** "Deliver to this address" submit button — shown when a saved address already exists and is selected. */
    private static final By DELIVER_TO_SAVED_ADDRESS_BTN = By.xpath("//input[@value='Deliver to this address']");

    private static final By ADD_NEW_ADDRESS_BTN     = By.id("add-new-address-desktop-sasp-tango-link");
    private static final By ADD_NEW_ADDRESS_BTN_ALT = By.cssSelector("a[id*='add-new-address'], a[href*='add-new-address']");

    private static final By FULL_NAME_FIELD      = By.id("address-ui-widgets-enterAddressFullName");
    private static final By PHONE_FIELD          = By.id("address-ui-widgets-enterAddressPhoneNumber");
    private static final By STREET_FIELD         = By.id("address-ui-widgets-enterAddressLine1");
    private static final By BUILDING_FIELD       = By.id("address-ui-widgets-enter-building-name-or-number");
    private static final By CITY_FIELD           = By.id("address-ui-widgets-enterAddressCity");
    private static final By DISTRICT_FIELD       = By.id("address-ui-widgets-enterAddressDistrictOrCounty");
    private static final By GOVERNORATE_FIELD    = By.id("address-ui-widgets-enterAddressStateOrRegion");

    /** First autocomplete suggestion used for city, district, and governorate fields. */
    private static final By AUTOCOMPLETE_FIRST_RESULT    = By.id("address-ui-widgets-autoCompleteResult-0");
    private static final By AUTOCOMPLETE_DROPDOWN_ITEMS  = By.cssSelector("ul.autoCompleteResult, li.autoOp, li[role='option']");

    private static final By USE_THIS_ADDRESS_BTN     = By.cssSelector("input[aria-labelledby='checkout-primary-continue-button-id-announce']");
    private static final By USE_THIS_ADDRESS_BTN_ALT = By.cssSelector("#checkout-primary-continue-button-id input, .a-button-primary input[type='submit']");

    /* ======== Payment ======== */
    private static final By CASH_ON_DELIVERY_RADIO = By.cssSelector("input[value*='COD']");
    private static final By PAYMENT_RADIO_BTNS     = By.cssSelector("input[type='radio']");
    private static final By CONTINUE_BTN         = By.cssSelector("input[aria-labelledby*='continue-button'], .a-button-primary input[type='submit']");

    /* ======== Order summary parsing ======== */
    private static final Pattern PRICE_NUM = Pattern.compile("([\\d,]+\\.\\d{2})");

    public CheckoutPage(Driver driver) {
        this.driver = driver;
    }

    // Step 8: add a new delivery address

    @Step("Add new delivery address")
    public CheckoutPage addDeliveryAddress(String fullName, String phone,
                                           String street, String building,
                                           String city, String district,
                                           String governorate) {
        logsUtils.info("Checkout URL: " + driver.get().getCurrentUrl());

        // Always add a new address, even if there is already a saved one
        driver.element().clickByJSWithFallback(ADD_NEW_ADDRESS_BTN, ADD_NEW_ADDRESS_BTN_ALT);

        driver.element()
              .fillField(FULL_NAME_FIELD, fullName)
              .fillField(PHONE_FIELD, phone)
              .fillField(STREET_FIELD, street)
              .fillField(BUILDING_FIELD, building);

        typeAndSelectFirst(CITY_FIELD, city, "City");

        clickAndSelectFirst(DISTRICT_FIELD, "District");

        if (driver.element().isFieldEmpty(GOVERNORATE_FIELD)) {
            clickAndSelectFirst(GOVERNORATE_FIELD, "Governorate");
        } else {
            logsUtils.info("Governorate auto-filled — skipping");
        }

        driver.element().clickByJSWithFallback(USE_THIS_ADDRESS_BTN, USE_THIS_ADDRESS_BTN_ALT);
        logsUtils.info("Address submitted");
        return this;
    }

    // Step 8: choose payment method

    @Step("Select Cash on Delivery")
    public CheckoutPage selectCashOnDelivery() {
        logsUtils.info("Payment URL: " + driver.get().getCurrentUrl());

        try {
            driver.element().clickByJS(CASH_ON_DELIVERY_RADIO);
            logsUtils.info("Cash on Delivery selected");
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            logsUtils.warn("COD radio not found by value — searching by label");
            clickCodByLabel();
        }

        driver.element().clickByJS(CONTINUE_BTN);
        logsUtils.info("Clicked Continue");
        return this;
    }

    // Step 9: verify totals including shipping

    @Step("Verify order total = items + shipping")
    public CheckoutPage verifyOrderSummary() {
        logsUtils.info("Review URL: " + driver.get().getCurrentUrl());

        String summary = (String) driver.element().executeScript(
            "var s=document.querySelector('#checkout-subtotals-section,#subtotals-marketplace-table');" +
            "return s?s.innerText:document.body.innerText.substring(0,3000);"
        );
        logsUtils.info("Order summary:\n" + summary);

        double items      = extractPrice(summary, "Items", "المنتجات");
        double shipping   = extractPrice(summary, "Shipping", "handling", "الشحن");
        if (shipping < 0) shipping = 0;
        double total      = extractPrice(summary, "Total");
        double orderTotal = extractPrice(summary, "Order total", "الإجمالي");

        logsUtils.info("items=" + items + ", shipping=" + shipping
                + ", total=" + total + ", orderTotal=" + orderTotal);

        if (items >= 0 && total >= 0) {
            double expected = items + shipping;
            boolean match = Math.abs(total - expected) <= 1.0;
            logsUtils.info("items + shipping = " + expected + " vs Total = " + total
                    + " → " + (match ? "PASS" : "FAIL"));
            Assert.assertTrue(match,
                    "Total mismatch: " + items + " + " + shipping + " = " + expected + " ≠ " + total);
            if (orderTotal > 0 && orderTotal != total) {
                logsUtils.info("Order total after discounts: " + orderTotal
                        + " (discount: " + (total - orderTotal) + ")");
            }
        } else {
            logsUtils.warn("Could not parse all summary values — checking order total > 0");
            double check = orderTotal > 0 ? orderTotal : total;
            Assert.assertTrue(check > 0, "Order total not found on page");
        }
        return this;
    }

    // Autocomplete helpers for city / district / governorate

    /** Type a partial value and choose the first autocomplete suggestion. */
    private void typeAndSelectFirst(By field, String text, String label) {
        driver.element().fillField(field, text);
        logsUtils.info("Typed into " + label + ": " + text);
        clickFirstSuggestion(label);
    }

    /** Click the field, wait for the list, then choose the first entry. */
    private void clickAndSelectFirst(By field, String label) {
        driver.element().click(field);
        logsUtils.info("Clicked " + label + " — waiting for dropdown");
        clickFirstSuggestion(label);
    }

    private void clickFirstSuggestion(String label) {
        try {
            driver.element().click(AUTOCOMPLETE_FIRST_RESULT);
            logsUtils.info(label + " — selected first suggestion");
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            logsUtils.warn(label + " — trying fallback suggestion list");
            driver.element().clickFirst(AUTOCOMPLETE_DROPDOWN_ITEMS);
            logsUtils.info(label + " — clicked fallback suggestion");
        }
    }

    // Fallback when COD cannot be found directly by value

    private void clickCodByLabel() {
        for (WebElement radio : driver.element().findElements(PAYMENT_RADIO_BTNS)) {
            try {
                String label = radio.findElement(By.xpath("./ancestor::div[1]")).getText().toLowerCase();
                if (label.contains("cash") || label.contains("cod")) {
                    driver.element().clickByJS(radio);
                    logsUtils.info("COD selected via label search");
                    return;
                }
            } catch (Exception ignored) { }
        }
        logsUtils.warn("No COD option found among radio buttons");
    }

    // Helpers to extract prices from the order summary text

    /**
     * Extract a price from the multi-line summary text.
     * When label is "total" we only match the exact "Total" line, not "Order total".
     */
    private double extractPrice(String text, String... labels) {
        if (text == null) return -1;
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase().trim();
            for (String label : labels) {
                String lbl = label.toLowerCase();
                boolean match = lbl.equals("total")
                        ? (line.equals("total:") || line.equals("total"))
                        : line.contains(lbl);
                if (match) {
                    Matcher m = PRICE_NUM.matcher(lines[i]);
                    if (m.find()) return parsePrice(m.group(1));
                    if (i + 1 < lines.length) {
                        Matcher m2 = PRICE_NUM.matcher(lines[i + 1]);
                        if (m2.find()) return parsePrice(m2.group(1));
                    }
                }
            }
        }
        return -1;
    }

    private double parsePrice(String num) {
        try { return Double.parseDouble(num.replace(",", "")); }
        catch (NumberFormatException e) { return -1; }
    }
}
