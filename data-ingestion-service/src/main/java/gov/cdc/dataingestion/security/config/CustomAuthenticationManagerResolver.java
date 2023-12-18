package gov.cdc.dataingestion.security.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManagerResolver implements AuthenticationManagerResolver {
    @Value("${auth.introspect-uri}")
    String introspectionUri;
    @Override
    public AuthenticationManager resolve(Object context) {
        HttpServletRequest request=(HttpServletRequest)context;
        String clientId = request.getHeader("client_id");
        String clientSecret = request.getHeader("client_secret");
        OpaqueTokenIntrospector opaquetokenintrospector;
        opaquetokenintrospector =  new NimbusOpaqueTokenIntrospector(
                introspectionUri,
                clientId,
                clientSecret);
        return new OpaqueTokenAuthenticationProvider(opaquetokenintrospector)::authenticate;
    }
}
