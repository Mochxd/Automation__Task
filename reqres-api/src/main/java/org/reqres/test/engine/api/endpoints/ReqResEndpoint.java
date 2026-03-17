package org.reqres.test.engine.api.endpoints;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.reqres.test.engine.api.client.ReqResClient;
import org.reqres.test.engine.api.responses.ApiResponse;

/** ReqRes API calls. Returns ApiResponse. */
public class ReqResEndpoint {

    private final ReqResClient client;

    public ReqResEndpoint(RequestSpecification requestSpec) {
        this.client = new ReqResClient(requestSpec);
    }

    public ApiResponse createUser(String requestBody) {
        return ApiResponse.from(client.createUser(requestBody));
    }

    public ApiResponse getUser(int userId) {
        return ApiResponse.from(client.getUser(userId));
    }

    public ApiResponse updateUser(int userId, String requestBody) {
        return ApiResponse.from(client.updateUser(userId, requestBody));
    }

    public ApiResponse deleteUser(int userId) {
        return ApiResponse.from(client.deleteUser(userId));
    }

    public ApiResponse getUserExpectingError(int userId) {
        return ApiResponse.from(client.getUserWithInvalidId(userId));
    }

    public ApiResponse createUserWithEmptyBody() {
        return ApiResponse.from(client.createUserWithEmptyBody());
    }
}
