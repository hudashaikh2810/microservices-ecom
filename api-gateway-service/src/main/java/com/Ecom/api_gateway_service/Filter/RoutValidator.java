package com.Ecom.api_gateway_service.Filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RoutValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/auth/signup",
            "/auth/token",
            "/eureka",
            "/auth/login",
            "/product/all"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
