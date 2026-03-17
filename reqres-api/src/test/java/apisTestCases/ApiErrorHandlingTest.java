package apisTestCases;

import base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.reqres.test.engine.api.responses.ApiResponse;
import org.reqres.test.engine.api.endpoints.ReqResEndpoint;
import org.reqres.test.engine.api.verification.ApiVerification;
import org.reqres.test.engine.utilities.logs.logsUtils;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Objects;

@Feature("ReqRes API Error Handling")
public class ApiErrorHandlingTest extends BaseTest {

    private static final String TEST_DATA = "testData";

    @Test(priority = 5)
    @Story("Request a non-existent user")
    @Description("GET 99999 returns 404")
    public void getNonExistentUserReturns404() {
        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.getUserExpectingError(99999);

        logsUtils.info("get user 99999 returned ", String.valueOf(response.getStatusCode()));
        logsUtils.info("response body: ", response.getBody());

        ApiVerification.verifyStatusCode(response, 404);
        ApiVerification.verifyResponseTime(response, 5000);
    }

    @Test(priority = 6)
    @Story("Create user with missing required fields")
    @Description("POST empty body, no 500")
    public void createUserWithEmptyBodyHandledGracefully() {
        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.createUserWithEmptyBody();

        logsUtils.info("post empty body returned ", String.valueOf(response.getStatusCode()));
        logsUtils.info("response body: ", response.getBody());

        ApiVerification.verifyBodyIsJson(response);
        ApiVerification.verifyStatusCodeNot(response, 500);
        ApiVerification.verifyResponseTime(response, 5000);
    }

    @Test(priority = 7)
    @Story("Create user with invalid request payload")
    @Description("POST InvalidUserRequest.json, no 500")
    public void createUserWithInvalidRequestHandledGracefully() {
        String invalidBody = loadTestData("InvalidUserRequest.json");
        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.createUser(invalidBody);

        logsUtils.info("post invalid body returned ", String.valueOf(response.getStatusCode()));
        logsUtils.info("response body: ", response.getBody());

        ApiVerification.verifyStatusCodeNot(response, 500);
        ApiVerification.verifyBodyIsJson(response);
        ApiVerification.verifyResponseTime(response, 5000);
    }

    @Test(priority = 8)
    @Story("Delete a non-existent user")
    @Description("DELETE 99999 returns 204")
    public void deleteNonExistentUserReturnsNoContent() {
        ReqResEndpoint endpoint = new ReqResEndpoint(requestSpec);
        ApiResponse response = endpoint.deleteUser(99999);

        logsUtils.info("delete returned ", String.valueOf(response.getStatusCode()));

        ApiVerification.verifyStatusCode(response, 204);
        ApiVerification.verifyResponseTime(response, 5000);
    }

    private String loadTestData(String filename) {
        String path = TEST_DATA + "/" + filename;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
            return new String(Objects.requireNonNull(stream, path + " not found on classpath").readAllBytes());
        } catch (Exception e) {
            logsUtils.error("could not load file ", path, ": ", e.getMessage());
            throw new AssertionError("Failed to load test data: " + path, e);
        }
    }
}
