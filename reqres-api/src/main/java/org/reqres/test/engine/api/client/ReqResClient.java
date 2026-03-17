package org.reqres.test.engine.api.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.reqres.test.engine.api.constants.ApiConstants;
import org.reqres.test.engine.utilities.logs.logsUtils;
import org.reqres.test.engine.utilities.properties.PropertiesManager;

import static io.restassured.RestAssured.given;

/** HTTP client for /api/users. Paths from ApiConstants. */
public class ReqResClient {

    private final RequestSpecification requestSpec;

    public ReqResClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public Response createUser(String body) {
        String endpoint = PropertiesManager.getConfig("createUserEndPoint");
        if (endpoint == null || endpoint.isBlank()) {
            endpoint = ApiConstants.PATH_USERS;
        }
        logsUtils.info("request: POST ", endpoint);
        return given(requestSpec).body(body).post(endpoint);
    }

    public Response getUser(int userId) {
        String endpoint = ApiConstants.PATH_USER_BY_ID + userId;
        logsUtils.info("request: GET ", endpoint);
        return given(requestSpec).get(endpoint);
    }

    public Response updateUser(int userId, String body) {
        String endpoint = ApiConstants.PATH_USER_BY_ID + userId;
        logsUtils.info("request: PUT ", endpoint);
        return given(requestSpec).body(body).put(endpoint);
    }

    public Response deleteUser(int userId) {
        String endpoint = ApiConstants.PATH_USER_BY_ID + userId;
        logsUtils.info("request: DELETE ", endpoint);
        return given(requestSpec).delete(endpoint);
    }

    public Response getUserWithInvalidId(int userId) {
        String endpoint = ApiConstants.PATH_USER_BY_ID + userId;
        logsUtils.info("request: GET ", endpoint, " (expect error)");
        return given(requestSpec).get(endpoint);
    }

    public Response createUserWithEmptyBody() {
        String endpoint = PropertiesManager.getConfig("createUserEndPoint");
        if (endpoint == null || endpoint.isBlank()) {
            endpoint = ApiConstants.PATH_USERS;
        }
        logsUtils.info("request: POST ", endpoint, " with empty body");
        return given(requestSpec).body("{}").post(endpoint);
    }
}
