package org.mlesyk.ui.security;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Override Spring's built-in implementations.<br>
 * Because these implementations will try to send a request to the OAuth2 server to query the user's information,
 * but the OAuth2 server of ESI does not support this method, so use access token (JWT) to parse out character information.
 */
public class EVEOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public EVEOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> userAttributes = extractUserAttributesFromOAuth2UserRequest(userRequest);

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new EVEOAuth2User(authorities, userAttributes, "name");
    }

    private Map<String, Object> extractUserAttributesFromOAuth2UserRequest(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        Map<String, Object> payload;
        try {
            payload = ((SignedJWT) JWTParser.parse(userRequest.getAccessToken().getTokenValue())).getPayload().toJSONObject();
        } catch (ParseException e) {
            throw new OAuth2AuthenticationException(String.valueOf(e));
        }
        return Map.of(
                "name", payload.get("name"),
                "characterId", Long.valueOf(String.valueOf(payload.get("sub")).substring(14))
        );
    }
}