package com.Ecom.api_gateway_service.Filter;

import com.Ecom.api_gateway_service.utils.JwtUtil;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleFilter extends AbstractGatewayFilterFactory<RoleFilter.Config> {
    @Autowired
    private JwtUtil jwtUtil;
    public RoleFilter()
    {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange,chain)->{
            String authHeader=exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if(authHeader!=null &&authHeader.startsWith("Bearer "))
            {
                authHeader=authHeader.substring(7);
            }
            List<String> role=jwtUtil.extractRoles(authHeader);
            if(role.stream().noneMatch(rRole->config.allowedRoles.contains(rRole)))
            {
                throw new RuntimeException("You are not authorized to access this resource access denied");
            }


                      return chain.filter(exchange);
        };
    }

    public static class Config{
        private List<String> allowedRoles;
        public List<String> getAllowedRoles() { return allowedRoles; }
        public void setAllowedRoles(List<String> allowedRoles) {
            this.allowedRoles = allowedRoles;
        }
    }
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("allowedRoles");
    }

}
