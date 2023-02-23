package logixtek.docsoup.api.infrastructure.configurations;

import com.google.common.base.Strings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity(debug = true)
public class JWTSecurityConfig extends  WebSecurityConfigurerAdapter{

  private static final String JWT_ROLE_NAME = "roles";
  private static final String ROLE_PREFIX = "ROLE_";
  @Bean
  public WebMvcConfigurer corsConfigurer() {
  return new WebMvcConfigurer() {
 @Value("${docsoup.client.url}")
   private  String clientUrl;
   @Value("${docsoup.internal.client.url}")
   private String internalClientUrl;
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        if(Strings.isNullOrEmpty(clientUrl)) throw new NullPointerException();

      registry.addMapping("/**")
              .allowedOrigins(clientUrl, internalClientUrl)
              .allowedMethods("*")
              .allowedHeaders("*");
    }
    };

  }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf()
                    .ignoringAntMatchers("/view-link/**","/ws/**","/health","/mock-ip", "/account/reset-password", "/company/user/accept-invitation",
                            "/guest/**", "/webhooks/paypal-webhooks/subscriptions/**", "/webhooks/paypal-webhooks/payments/**",
                            "/tawk-webhooks/webhooks/start-end/**")
                    .and()
                .cors()
                .and()
                .authorizeRequests(authz -> authz
                       .antMatchers("/v2/api-docs",
                                   "/configuration/ui",
                                   "/swagger-resources/**",
                                   "/configuration/security",
                                   "/swagger-ui.html",
                                   "/webjars/**").permitAll()
                        .antMatchers("/view-link/**","/ws/**","/health","/mock-ip", "/account/reset-password", "/company/user/accept-invitation",
                                "/guest/**", "/webhooks/paypal-webhooks/subscriptions/**", "/webhooks/paypal-webhooks/payments/**",
                                "/tawk-webhooks/webhooks/start-end/**").permitAll()
                        .anyRequest().authenticated())
                      
                .oauth2ResourceServer(oauth2 -> oauth2.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()));
    }
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
      // create a custom JWT converter to map the roles from the token as granted authorities
      JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
      jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(JWT_ROLE_NAME); // default is: scope, scp
      jwtGrantedAuthoritiesConverter.setAuthorityPrefix(ROLE_PREFIX); // default is: SCOPE_

      JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
      jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
      return jwtAuthenticationConverter;
    }
}
