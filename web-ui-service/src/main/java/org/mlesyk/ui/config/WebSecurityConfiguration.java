package org.mlesyk.ui.config;

import org.mlesyk.ui.repository.TokenRepository;
import org.mlesyk.ui.repository.UserRepository;
import org.mlesyk.ui.security.EVEOAuth2UserService;
import org.mlesyk.ui.security.JpaOAuth2AuthorizedClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(this.oAuth2UserService()) // Override Spring's built-in implementations for OAuth2UserService
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/orders_table").permitAll()
                        .requestMatchers("/login/oauth2/code/eve", "/oauth2/authorization/eve", "/css/**", "/img/**", "/webjars/**", "/js/**", "/orders/**", "/profile", "/about").permitAll()
                        .anyRequest().hasRole("USER")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );
        return http.build();
    }

    /**
     * Override Spring's built-in implementation for OAuth2AuthorizedClientService
     * The application use database to store authentication information.
     */
    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(UserRepository userRepository, TokenRepository tokenRepository, ClientRegistrationRepository clientRegistrationRepository) {
        return new JpaOAuth2AuthorizedClientService(userRepository, tokenRepository, clientRegistrationRepository.findByRegistrationId("eve"));
    }

    /**
     * Override Spring's built-in implementations.<br>
     * Because these implementations will try to send a request to the OAuth2 server to query the user's information,
     * but the OAuth2 server of ESI does not support this method, so use access token (JWT) to parse out character information.
     */
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new EVEOAuth2UserService();
    }
}
