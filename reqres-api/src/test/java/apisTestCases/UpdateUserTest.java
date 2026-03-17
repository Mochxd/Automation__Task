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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Feature("ReqRes User API")
public class UpdateUserTest extends BaseTest {

    @Test(priority = 4)
    @Story("Update an existing user")
    @Description("PUT user with UpdatedUserRequest.json, check 200 and body")
    public void updateUserSuccessfully() throws IOException {
        int userId = UserIdHolder.hasCreatedUser() ? UserIdHolder.getCreatedUserId() : 2;
        logsUtils.info("updating user id ", String.valueOf(userId));

        String updatedBody;
        try (InputStream stream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("testData/UpdatedUserRequest.json"),
                "testData/UpdatedUserRequest.json not found on classpath")) {
            updatedBody = new String(stream.readAllBytes());
        }

        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.updateUser(userId, updatedBody);

        logsUtils.info("update returned ", String.valueOf(response.getStatusCode()));
        logsUtils.info("response body: ", response.getBody());

        ApiVerification.verifyBodyIsJson(response);
        ApiVerification.verifyStatusCode(response, 200);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_NAME);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_JOB);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_UPDATED_AT);
        ApiVerification.verifyFieldValue(response, ApiConstants.FIELD_NAME, "John Doe");
        ApiVerification.verifyFieldValue(response, ApiConstants.FIELD_JOB, "Senior QA Engineer");
        ApiVerification.verifyResponseTime(response, 5000);
    }
}
