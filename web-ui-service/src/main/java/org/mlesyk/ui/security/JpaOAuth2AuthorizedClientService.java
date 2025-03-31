package org.mlesyk.ui.security;

import org.mlesyk.ui.model.User;
import org.mlesyk.ui.model.oauth.AccessToken;
import org.mlesyk.ui.model.oauth.RefreshToken;
import org.mlesyk.ui.model.oauth.Token;
import org.mlesyk.ui.repository.TokenRepository;
import org.mlesyk.ui.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.util.Objects;

public class JpaOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ClientRegistration clientRegistration;

    public JpaOAuth2AuthorizedClientService(UserRepository userRepository, TokenRepository tokenRepository, ClientRegistration clientRegistration) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.clientRegistration = clientRegistration;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        User user = userRepository.findByName(principalName);
        AccessToken accessToken = user.getToken().getAccessToken();
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken.getValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt());
        RefreshToken refreshToken = user.getToken().getRefreshToken();
        OAuth2RefreshToken oAuth2RefreshToken = new OAuth2RefreshToken(refreshToken.getValue(), refreshToken.getIssuedAt());
        return (T) new OAuth2AuthorizedClient(clientRegistration, principalName, oAuth2AccessToken, oAuth2RefreshToken);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        if (userRepository.findByName(principal.getName()) != null) {
            return;
        }
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
        EVEOAuth2User eveoAuth2User = (EVEOAuth2User) oAuth2AuthenticationToken.getPrincipal();
        Long userId = eveoAuth2User.getCharacterId();

        Token token = tokenRepository.save(buildToken(authorizedClient.getAccessToken(), Objects.requireNonNull(authorizedClient.getRefreshToken())));
        User user = new User(userId, eveoAuth2User.getName(), token);
        userRepository.save(user);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        User user = userRepository.findByName(principalName);
        userRepository.delete(user);
    }

    private Token buildToken(OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        return new Token(
                new AccessToken(accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(), accessToken.getScopes()),
                new RefreshToken(refreshToken.getTokenValue(), refreshToken.getIssuedAt())
        );
    }
}