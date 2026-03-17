package apisTestCases;

import base.BaseTest;
import helpers.UserIdHolder;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.reqres.test.engine.api.constants.ApiConstants;
import org.reqres.test.engine.api.responses.ApiResponse;
import org.reqres.test.engine.api.endpoints.ReqResEndpoint;
import org.reqres.test.engine.api.verification.ApiVerification;
import org.reqres.test.engine.utilities.logs.logsUtils;
import org.testng.annotations.Test;

@Feature("ReqRes User API")
public class GetUserTest extends BaseTest {

    @Test(priority = 2)
    @Story("Retrieve an existing user")
    @Description("GET user 2, check 200 and data fields")
    public void getExistingUserById() {
        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.getUser(2);

        logsUtils.info("get user 2 returned ", String.valueOf(response.getStatusCode()));
        logsUtils.info("response body: ", response.getBody());

        ApiVerification.verifyBodyIsJson(response);
        ApiVerification.verifyStatusCode(response, 200);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_DATA_ID);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_DATA_EMAIL);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_DATA_FIRST_NAME);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_DATA_LAST_NAME);
        ApiVerification.verifyResponseTime(response, 5000);
    }

    @Test(priority = 3)
    @Story("Retrieve the user that was just created")
    @Description("GET created user by id, check name matches")
    public void getCreatedUser() {
        int userId = UserIdHolder.hasCreatedUser() ? UserIdHolder.getCreatedUserId() : 2;
        logsUtils.info("getting user by id ", String.valueOf(userId));

        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.getUser(userId);

        logsUtils.info("get created user returned ", String.valueOf(response.getStatusCode()));
        ApiVerification.verifyBodyIsJson(response);
        if (response.getStatusCode() == 200) {
            ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_DATA_FIRST_NAME);
            ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_DATA_LAST_NAME);
            ApiVerification.verifyFieldValue(response, ApiConstants.FIELD_DATA_FIRST_NAME, "John");
            ApiVerification.verifyFieldValue(response, ApiConstants.FIELD_DATA_LAST_NAME, "Doe");
            ApiVerification.verifyResponseTime(response, 5000);
        }
    }
}
