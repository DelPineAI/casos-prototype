package com.casos.web;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Parsed view of one HTTP request: method, path, query and form fields, cookies. */
public final class Request {

    private final String method;
    private final String path;
    private final Map<String, List<String>> params;
    private final Map<String, String> cookies;

    private Request(String method, String path, Map<String, List<String>> params, Map<String, String> cookies) {
        this.method = method;
        this.path = path;
        this.params = params;
        this.cookies = cookies;
    }

    public static Request from(HttpExchange exchange) throws IOException {
        Map<String, List<String>> params = new HashMap<>();
        parse(exchange.getRequestURI().getRawQuery(), params);
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStream in = exchange.getRequestBody()) {
                parse(new String(in.readAllBytes(), StandardCharsets.UTF_8), params);
            }
        }
        Map<String, String> cookies = new HashMap<>();
        for (String header : exchange.getRequestHeaders().getOrDefault("Cookie", List.of())) {
            for (String pair : header.split(";")) {
                String[] kv = pair.trim().split("=", 2);
                if (kv.length == 2) {
                    cookies.put(kv[0], kv[1]);
                }
            }
        }
        return new Request(exchange.getRequestMethod().toUpperCase(), exchange.getRequestURI().getPath(),
                params, cookies);
    }

    private static void parse(String raw, Map<String, List<String>> into) {
        if (raw == null || raw.isEmpty()) {
            return;
        }
        for (String pair : raw.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            into.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public boolean isPost() {
        return "POST".equals(method);
    }

    /** First value of a field, trimmed, or empty string. */
    public String get(String name) {
        List<String> values = params.get(name);
        return values == null || values.isEmpty() ? "" : values.get(0).trim();
    }

    public Optional<String> optional(String name) {
        String value = get(name);
        return value.isEmpty() ? Optional.empty() : Optional.of(value);
    }

    public List<String> all(String name) {
        return params.getOrDefault(name, List.of());
    }

    public boolean has(String name) {
        return !get(name).isEmpty();
    }

    public Optional<String> cookie(String name) {
        return Optional.ofNullable(cookies.get(name));
    }
}
