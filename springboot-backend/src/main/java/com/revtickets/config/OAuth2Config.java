package com.revtickets.config;

/**
 * OAuth2 Configuration for Social Login
 * Supports Google, Facebook, GitHub authentication
 * 
 * To enable OAuth2:
 * 1. Add spring-boot-starter-oauth2-client dependency to pom.xml:
 *    <dependency>
 *        <groupId>org.springframework.boot</groupId>
 *        <artifactId>spring-boot-starter-oauth2-client</artifactId>
 *    </dependency>
 * 2. Uncomment the imports below
 * 3. Uncomment @Configuration annotation
 * 4. Configure client IDs and secrets in application.yml
 * 5. Uncomment the beans below
 */

// Uncomment when OAuth2 dependency is added:
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.oauth2.client.registration.ClientRegistration;
// import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
// import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
// import org.springframework.security.oauth2.core.AuthorizationGrantType;

// @Configuration
public class OAuth2Config {

    // Uncomment and configure when OAuth2 is needed
    
    /*
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
            googleClientRegistration(),
            facebookClientRegistration(),
            githubClientRegistration()
        );
    }
    
    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
            .clientId("your-google-client-id")
            .clientSecret("your-google-client-secret")
            .scope("openid", "profile", "email")
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .tokenUri("https://www.googleapis.com/oauth2/v4/token")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName("sub")
            .clientName("Google")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .build();
    }
    
    private ClientRegistration facebookClientRegistration() {
        return ClientRegistration.withRegistrationId("facebook")
            .clientId("your-facebook-app-id")
            .clientSecret("your-facebook-app-secret")
            .scope("email", "public_profile")
            .authorizationUri("https://www.facebook.com/v12.0/dialog/oauth")
            .tokenUri("https://graph.facebook.com/v12.0/oauth/access_token")
            .userInfoUri("https://graph.facebook.com/me?fields=id,name,email")
            .userNameAttributeName("id")
            .clientName("Facebook")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .build();
    }
    
    private ClientRegistration githubClientRegistration() {
        return ClientRegistration.withRegistrationId("github")
            .clientId("your-github-client-id")
            .clientSecret("your-github-client-secret")
            .scope("read:user", "user:email")
            .authorizationUri("https://github.com/login/oauth/authorize")
            .tokenUri("https://github.com/login/oauth/access_token")
            .userInfoUri("https://api.github.com/user")
            .userNameAttributeName("id")
            .clientName("GitHub")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .build();
    }
    */
}
