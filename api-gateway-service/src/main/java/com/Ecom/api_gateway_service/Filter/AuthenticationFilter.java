package com.Ecom.api_gateway_service.Filter;

import com.Ecom.api_gateway_service.utils.JwtUtil;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private RoutValidator routeValidator;
    @Autowired
    private JwtUtil util;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) ->
        {
            System.out.println("Request is inside the GatewayeFilter");
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                System.out.println("Going to check request");
                if (!(exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) && config.getIsTokenRequired()) {
                    System.out.println("Request missing header here for request with jwt required");
                    throw new RuntimeException("missing authorization header");
                }
                if (!(exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) && !config.getIsTokenRequired()) {
                    System.out.println("Request missing header for authorization but jwt not required");
                    System.out.println("By passing filter");
                    return chain.filter(exchange);

                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                System.out.println("Filter bypassed from this extracting token from Bearrer");
                if (!util.validateToken(authHeader)) {
                    throw new RuntimeException("Cannot validate token");
                }
                System.out.println("Extracting userId from token");
                Long userId = util.extractUserId(authHeader);
                System.out.println("UserId={}" + userId.toString());
                System.out.println("Modifiing header and request");
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().header("X-USER-ID", userId.toString()).build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());


            }
            return chain.filter(exchange);
        }
        );
    }

    public static class Config {
        private boolean isTokenRequired = true;

        public boolean getIsTokenRequired() {
            return isTokenRequired;
        }

        public void setIsTokenRequired(boolean isTokenRequired) {
            this.isTokenRequired = isTokenRequired;
        }
    }

}
