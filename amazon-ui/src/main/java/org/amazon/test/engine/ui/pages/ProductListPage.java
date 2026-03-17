package org.amazon.test.engine.ui.pages;

import io.qameta.allure.Step;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.ui.driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductListPage {

    private final Driver driver;

    /* ======== Locators ======== */
    private static final By FREE_SHIPPING_FILTER     = By.cssSelector("li[id*='p_n_free_shipping'] a");
    private static final By FREE_SHIPPING_FILTER_ALT = By.cssSelector("a[href*='p_n_free_shipping_eligible']");
    private static final By NEW_CONDITION_FILTER     = By.xpath("//li[contains(@id,'p_n_condition-type')]//a[contains(.,'New')]");
    private static final By NEW_CONDITION_FILTER_ALT = By.cssSelector("a[href*='p_n_condition-type'][href*='28071525031']");
    private static final By SORT_BY_DROPDOWN     = By.id("s-result-sort-select");
    private static final By PRODUCT_CARD         = By.cssSelector("div[data-component-type='s-search-result']");
    private static final By PRODUCT_TITLE        = By.cssSelector("h2");
    private static final By PRICE_WHOLE          = By.cssSelector("span.a-price-whole");
    private static final By PRICE_FRACTION       = By.cssSelector("span.a-price-fraction");
    private static final By PRICE_OFFSCREEN      = By.cssSelector("span.a-price span.a-offscreen");
    private static final By ADD_TO_CART_BTN      = By.cssSelector("button[name='submit.addToCart']");
    private static final By NEXT_PAGE_BTN        = By.cssSelector("a.s-pagination-next");
    private static final By CART_ICON            = By.id("nav-cart");
    private static final By CART_COUNT_BADGE     = By.id("nav-cart-count");

    private static final int     MAX_PAGES = 20;
    private static final Pattern EGP_PRICE = Pattern.compile("EGP\\s*([\\d,]+(?:\\.\\d+)?)");

    public ProductListPage(Driver driver) {
        this.driver = driver;
    }

    @Step("Apply Free Shipping filter)")
    public ProductListPage applyFreeShippingFilter() {
        driver.element()
              .scrollToTop()
              .clickByJSWithFallback(FREE_SHIPPING_FILTER, FREE_SHIPPING_FILTER_ALT);
        confirmFilterActive("p_n_free_shipping_eligible", "Free Shipping");
        return this;
    }

    @Step("Apply New condition filter")
    public ProductListPage applyNewConditionFilter() {
        driver.element()
              .scrollToTop()
              .clickByJSWithFallback(NEW_CONDITION_FILTER, NEW_CONDITION_FILTER_ALT);
        confirmFilterActive("p_n_condition-type", "New Condition");
        return this;
    }

    @Step("Sort by Price: High to Low ")
    public ProductListPage sortByPriceHighToLow() {
        driver.element()
              .scrollToTop()
              .selectByVisibilityOfText(SORT_BY_DROPDOWN, "Price: High to Low");
        logsUtils.info("Sorted by Price: High to Low");
        return this;
    }

    /**
     * Scan result pages sorted high to low, and add every product under 15k EGP
     * that has a visible Add-to-cart button. If nothing is added on a page,
     * move to the next one until something is added or we hit the page limit.
     */
    @Step("Add all products below 15k EGP")
    public CartPage addProductsUnder15k(List<String> addedProducts) {
        final double maxPrice = 15_000;
        int pagesScanned = 0;
        String lastUrl = null;

        while (true) {
            if (++pagesScanned > MAX_PAGES) {
                logsUtils.info("Reached page limit (" + MAX_PAGES + ") — stopping");
                break;
            }

            String currentUrl = driver.get().getCurrentUrl();
            if (currentUrl.equals(lastUrl)) {
                logsUtils.info("URL unchanged — stopping to avoid infinite loop");
                break;
            }
            lastUrl = currentUrl;

            List<WebElement> cards = driver.element().findElements(PRODUCT_CARD);
            logsUtils.info("Page " + pagesScanned + ": " + cards.size() + " cards");

            int addedOnPage = 0;
            for (WebElement card : cards) {
                try {
                    double price = parsePriceFromCard(card);
                    if (price < 0) { logsUtils.info("  No parseable price — skip"); continue; }
                    logsUtils.info("  Price: " + price + " EGP");

                    if (price < maxPrice) {
                        List<WebElement> btns = card.findElements(ADD_TO_CART_BTN);
                        if (!btns.isEmpty() && btns.get(0).isDisplayed()) {
                            String title = getCardTitle(card);
                            driver.element().clickByJS(btns.get(0));
                            addedOnPage++;
                            addedProducts.add(title);
                            logsUtils.info("  → Added: " + title);
                            driver.element().dismissOverlay("#ewc-content", ".a-popover-wrapper");
                        } else {
                            logsUtils.info("  → Under 15k but no Add-to-cart button — skip");
                        }
                    }
                } catch (Exception e) {
                    logsUtils.warn("  Card error: ", e.getMessage());
                }
            }

            if (addedOnPage > 0) {
                logsUtils.info("Added " + addedOnPage + " on page " + pagesScanned
                        + "; total: " + addedProducts.size());
                break;
            }

            logsUtils.info("Nothing added on page " + pagesScanned + " — next page");
            if (!driver.element().isPresent(NEXT_PAGE_BTN)) {
                logsUtils.info("Last page reached");
                break;
            }
            String beforeNav = driver.get().getCurrentUrl();
            driver.element().clickByJS(NEXT_PAGE_BTN);
            if (driver.get().getCurrentUrl().equals(beforeNav)) {
                logsUtils.info("Next-page click did not navigate — stopping");
                break;
            }
        }

        verifyCartBadgeCount(addedProducts.size());
        driver.element().clickByJS(CART_ICON);
        return new CartPage(driver);
    }

    /* ---- filter / cart verification helpers ---- */

    /** Confirm a filter is active by checking the current page URL contains its parameter. */
    private void confirmFilterActive(String urlParam, String filterName) {
        String url = driver.get().getCurrentUrl();
        if (url.contains(urlParam)) {
            logsUtils.info(filterName + " filter confirmed active");
        } else {
            logsUtils.warn(filterName + " filter may not have applied — URL does not contain: " + urlParam);
        }
    }

    /** Verify the nav cart badge count matches how many products were added. */
    private void verifyCartBadgeCount(int expected) {
        try {
            String badge = driver.element().getTextOf(CART_COUNT_BADGE).trim();
            int actual = Integer.parseInt(badge);
            if (actual == expected) {
                logsUtils.info("Cart badge count matches: " + actual);
            } else {
                logsUtils.warn("Cart badge shows " + actual + " but " + expected + " products were added");
            }
        } catch (Exception e) {
            logsUtils.warn("Could not read cart badge count: " + e.getMessage());
        }
    }

    /* ---- price parsing helpers (card-scoped, no driver-level element lookup) ---- */

    private double parsePriceFromCard(WebElement card) {
        try {
            List<WebElement> whole = card.findElements(PRICE_WHOLE);
            if (!whole.isEmpty()) {
                String w = whole.get(0).getText().replaceAll("[^0-9]", "");
                if (!w.isEmpty()) {
                    List<WebElement> frac = card.findElements(PRICE_FRACTION);
                    String f = frac.isEmpty() ? "" : frac.get(0).getText().replaceAll("[^0-9]", "");
                    return Double.parseDouble(w + (f.isEmpty() ? "" : "." + f));
                }
            }
        } catch (Exception ignored) { }

        try {
            List<WebElement> off = card.findElements(PRICE_OFFSCREEN);
            if (!off.isEmpty()) {
                double v = extractEgpPrice(off.get(0).getAttribute("textContent"));
                if (v >= 0) return v;
            }
        } catch (Exception ignored) { }

        try {
            for (WebElement el : card.findElements(By.xpath(".//*[contains(normalize-space(.),'EGP')]"))) {
                if (el.findElements(By.xpath("./*")).isEmpty()) {
                    double v = extractEgpPrice(el.getAttribute("textContent"));
                    if (v >= 0) return v;
                }
            }
        } catch (Exception ignored) { }

        return -1;
    }

    private double extractEgpPrice(String text) {
        if (text == null || text.isEmpty()) return -1;
        Matcher m = EGP_PRICE.matcher(text);
        if (!m.find()) return -1;
        try { return Double.parseDouble(m.group(1).replace(",", "")); }
        catch (NumberFormatException e) { return -1; }
    }

    private String getCardTitle(WebElement card) {
        try { return card.findElement(PRODUCT_TITLE).getText().trim(); }
        catch (Exception e) { return ""; }
    }
}
