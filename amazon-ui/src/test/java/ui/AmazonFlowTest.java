package ui;

import base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.amazon.test.engine.ui.driver.Driver;
import org.amazon.test.engine.ui.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.amazon.test.engine.common.properties.TestDataManager.getData;

@Feature("Amazon Egypt Shopping Flow")
public class AmazonFlowTest extends BaseTest {

    // Getting data from TestData.properties file
    String email       = getData("amazon.email");
    String password    = getData("amazon.password");
    String fullName    = getData("checkout.fullName");
    String phone       = getData("checkout.mobileNumber");
    String addressLine = getData("checkout.addressLine");
    String building    = getData("checkout.building");
    String city        = getData("checkout.city");
    String district    = getData("checkout.district");
    String governorate = getData("checkout.governorate");
    
    List<String> addedProducts = new ArrayList<>();

    @BeforeMethod
    public void setUpBrowser() {
        driver = new Driver();
    }

    @Test
    @Story("Full Amazon.eg flow per task: login → menu → filters → sort → add under 15k → cart check → checkout → total check")
    @Description(
        "1. Open https://www.amazon.eg/ and login\n" +
        "2. Open 'All' menu from the left side\n" +
        "3. Click 'Video Games' then 'All Video Games'\n" +
        "4. Left filter: Free Shipping + condition New\n" +
        "5. Right sort: Price High to Low\n" +
        "6. Add all products below 15k EGP; if none on page, go to next page\n" +
        "7. Verify ALL added products are in cart\n" +
        "8. Add address and choose Cash on Delivery\n" +
        "9. Verify total = items + shipping (if any)"
    )
    public void fullAmazonShoppingFlow() {
        new LoginPage(driver)
                .login(email, password)
                .openAllMenu()
                .clickVideoGames()
                .clickAllVideoGames()
                .applyFreeShippingFilter()
                .applyNewConditionFilter()
                .sortByPriceHighToLow()
                .addProductsUnder15k(addedProducts)
                .verifyProductsInCart(addedProducts)
                .proceedToCheckout()
                .addDeliveryAddress(fullName, phone, addressLine, building, city, district, governorate)
                .selectCashOnDelivery()
                .verifyOrderSummary();
    }
}
