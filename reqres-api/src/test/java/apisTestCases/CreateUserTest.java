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
public class CreateUserTest extends BaseTest {

    @Test(priority = 1)
    @Story("Create a new user")
    @Description("POST /api/users, check 201 and store id")
    public void createUserSuccessfully() throws IOException {
        String requestBody;
        try (InputStream stream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("testData/UserRequest.json"),
                "testData/UserRequest.json not found on classpath")) {
            requestBody = new String(stream.readAllBytes());
        }

        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.createUser(requestBody);

        logsUtils.info("create user returned ", String.valueOf(response.getStatusCode()));
        logsUtils.info("response body: ", response.getBody());

        ApiVerification.verifyBodyIsJson(response);
        ApiVerification.verifyStatusCode(response, 201);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_ID);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_NAME);
        ApiVerification.verifyFieldPresence(response, ApiConstants.FIELD_JOB);
        ApiVerification.verifyFieldValue(response, ApiConstants.FIELD_NAME, "John Doe");
        ApiVerification.verifyFieldValue(response, ApiConstants.FIELD_JOB, "QA Engineer");
        ApiVerification.verifyResponseTime(response, 5000);

        int createdId = Integer.parseInt(response.getString(ApiConstants.FIELD_ID));
        UserIdHolder.setCreatedUserId(createdId);
        logsUtils.info("stored created user id: ", String.valueOf(createdId));
    }
}
