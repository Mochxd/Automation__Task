package org.reqres.test.engine.api.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HeaderConfig {

    public Map<String, String> defaultHeaders() {
        Map<String, String> headers = Collections.synchronizedMap(new HashMap<>());
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("accept", "application/json");
        return headers;
    }

    public Map<String, String> defaultHeadersNoCharset() {
        Map<String, String> headers = Collections.synchronizedMap(new HashMap<>());
        headers.put("Content-Type", "application/json");
        headers.put("accept", "application/json");
        return headers;
    }

    public Map<String, String> headersWithBearerToken(String token) {
        Map<String, String> headers = Collections.synchronizedMap(new HashMap<>());
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
