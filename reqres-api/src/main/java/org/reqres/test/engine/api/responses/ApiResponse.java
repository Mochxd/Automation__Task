package org.reqres.test.engine.api.responses;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/** Status code, body string, response time. */
public class ApiResponse {

    private final int statusCode;
    private final String body;
    private final long responseTimeMs;

    public ApiResponse(int statusCode, String body, long responseTimeMs) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.responseTimeMs = responseTimeMs;
    }

    public static ApiResponse from(Response response) {
        return new ApiResponse(
                response.getStatusCode(),
                response.getBody().asString(),
                response.getTime()
        );
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public String getString(String jsonPath) {
        return JsonPath.from(body).getString(jsonPath);
    }

    public <T> T get(String jsonPath) {
        return JsonPath.from(body).get(jsonPath);
    }
}
