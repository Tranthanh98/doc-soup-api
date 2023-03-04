package logixtek.docsoup.api.infrastructure.configurations;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class KeycloakConfig {

    private static final String REALM = "master";
    private static final String CLIENT_ID = "admin-cli";

    @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.server}")
    String serverUrl;

     @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.user}")
     String username;

     @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.pass}")
     String password;



    @Bean
    @RequestScope
    Keycloak Keycloak () {
        int a = 1;
        return KeycloakBuilder
                .builder()
                .serverUrl(serverUrl)
                .realm(REALM)
                .username(username)
                .password(password)
                .clientId(CLIENT_ID)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }
}
