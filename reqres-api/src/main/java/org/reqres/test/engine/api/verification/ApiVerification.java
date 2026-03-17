package org.reqres.test.engine.api.verification;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.reqres.test.engine.api.responses.ApiResponse;
import org.reqres.test.engine.utilities.logs.logsUtils;
import org.testng.Assert;

/** Assertions for API responses (status, body, fields, time). */
public class ApiVerification {

    private ApiVerification() {}

    public static void verifyStatusCode(ApiResponse response, int expectedCode) {
        int actual = response.getStatusCode();
        logsUtils.info("status code: expected ", String.valueOf(expectedCode), " got ", String.valueOf(actual));
        if (actual != expectedCode) {
            logsUtils.error("wrong status: got ", String.valueOf(actual), " expected ", String.valueOf(expectedCode), ". body: ", response.getBody());
        }
        Assert.assertEquals(actual, expectedCode, "Expected status " + expectedCode + " but received " + actual);
    }

    public static void verifyBodyIsJson(ApiResponse response) {
        String body = response.getBody();
        if (body == null || body.isBlank()) {
            logsUtils.error("response body is null or empty");
            Assert.fail("Response body is null or empty");
        }
        try {
            JsonPath.from(body).get("");
        } catch (Exception e) {
            logsUtils.error("response is not valid json. body: ", body);
            Assert.fail("Response body is not valid JSON: " + e.getMessage());
        }
    }

    public static void verifyFieldPresence(ApiResponse response, String fieldPath) {
        Object value = JsonPath.from(response.getBody()).get(fieldPath);
        boolean present = value != null;
        logsUtils.info("field '", fieldPath, "' present: ", String.valueOf(present));
        if (!present) {
            logsUtils.error("field '", fieldPath, "' not in response. body: ", response.getBody());
        }
        Assert.assertTrue(present, "Expected field '" + fieldPath + "' not found in response");
    }

    public static void verifyFieldValue(ApiResponse response, String fieldPath, String expectedValue) {
        String actual = JsonPath.from(response.getBody()).getString(fieldPath);
        logsUtils.info("field '", fieldPath, "': expected '", expectedValue, "' got '", actual, "'");
        if (actual == null || !actual.equals(expectedValue)) {
            logsUtils.error("field '", fieldPath, "': expected '", expectedValue, "' got '", String.valueOf(actual), "'. body: ", response.getBody());
        }
        Assert.assertEquals(actual, expectedValue, "Field '" + fieldPath + "' mismatch — expected: " + expectedValue + ", got: " + actual);
    }

    public static void verifyResponseTime(ApiResponse response, long maxMs) {
        long actual = response.getResponseTimeMs();
        logsUtils.info("response time ", String.valueOf(actual), " ms");
        if (actual > maxMs) {
            logsUtils.error("response took ", String.valueOf(actual), " ms (limit was ", String.valueOf(maxMs), " ms)");
        }
        Assert.assertTrue(actual <= maxMs, "Response too slow — expected <= " + maxMs + "ms but took " + actual + "ms");
    }

    public static void verifyStatusCodeNot(ApiResponse response, int unexpectedCode) {
        int actual = response.getStatusCode();
        if (actual == unexpectedCode) {
            logsUtils.error("got status ", String.valueOf(actual), " but expected not ", String.valueOf(unexpectedCode), ". body: ", response.getBody());
        }
        Assert.assertNotEquals(actual, unexpectedCode, "Response status should NOT be " + unexpectedCode + " but it was");
    }

    public static void verifyStatusCode(Response response, int expectedCode) {
        int actual = response.getStatusCode();
        logsUtils.info("status code: expected ", String.valueOf(expectedCode), " got ", String.valueOf(actual));
        if (actual != expectedCode) {
            logsUtils.error("wrong status: got ", String.valueOf(actual), " expected ", String.valueOf(expectedCode), ". body: ", response.getBody().asString());
        }
        Assert.assertEquals(actual, expectedCode,
                "Expected status " + expectedCode + " but received " + actual);
    }

    public static void verifyContentType(Response response, String expectedType) {
        String actual = response.getContentType();
        logsUtils.info("content-type: expected ", expectedType, " got ", actual);
        if (actual == null || !actual.contains(expectedType)) {
            logsUtils.error("wrong content-type: got ", String.valueOf(actual));
        }
        Assert.assertTrue(actual != null && actual.contains(expectedType),
                "Expected content type containing '" + expectedType + "' but got: " + actual);
    }

    public static void verifyFieldPresence(Response response, String field) {
        boolean present = response.jsonPath().get(field) != null;
        logsUtils.info("field '", field, "' present: ", String.valueOf(present));
        if (!present) {
            logsUtils.error("field '", field, "' not in response. body: ", response.getBody().asString());
        }
        Assert.assertTrue(present, "Expected field '" + field + "' not found in response");
    }

    public static void verifyFieldValue(Response response, String field, String expectedValue) {
        String actual = response.jsonPath().getString(field);
        logsUtils.info("field '", field, "': expected '", expectedValue, "' got '", actual, "'");
        if (actual == null || !actual.equals(expectedValue)) {
            logsUtils.error("field '", field, "': expected '", expectedValue, "' got '", String.valueOf(actual), "'");
        }
        Assert.assertEquals(actual, expectedValue,
                "Field '" + field + "' mismatch — expected: " + expectedValue + ", got: " + actual);
    }

    public static void verifyResponseTime(Response response, long maxMs) {
        long actual = response.getTime();
        logsUtils.info("response time ", String.valueOf(actual), " ms");
        if (actual > maxMs) {
            logsUtils.error("response took ", String.valueOf(actual), " ms (limit ", String.valueOf(maxMs), " ms)");
        }
        Assert.assertTrue(actual <= maxMs,
                "Response too slow — expected <= " + maxMs + "ms but took " + actual + "ms");
    }

    public static void verifyBodyIsJson(Response response) {
        String contentType = response.getContentType();
        String body = response.getBody().asString();
        if (contentType == null || !contentType.contains("application/json")) {
            logsUtils.error("response not json. content-type: ", String.valueOf(contentType));
        }
        Assert.assertTrue(contentType != null && contentType.contains("application/json"),
                "Response is not JSON — Content-Type: " + contentType + "\nBody: " + body);
    }

    public static void verifyStatusCodeNot(Response response, int unexpectedCode) {
        int actual = response.getStatusCode();
        if (actual == unexpectedCode) {
            logsUtils.error("got status ", String.valueOf(actual), " but expected not ", String.valueOf(unexpectedCode), ". body: ", response.getBody().asString());
        }
        Assert.assertNotEquals(actual, unexpectedCode,
                "Response status should NOT be " + unexpectedCode + " but it was");
    }
}
