package org.mlesyk.ui.model.oauth;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Embeddable
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AccessToken {
    @Column(name = "access_token_value", length = 2000)
    private final String value;

    @Column(name = "access_token_issued_at")
    private final Instant issuedAt;

    @Column(name = "access_token_expires_at")
    private final Instant expiresAt;

    @Column(name = "access_token_scopes")
    @ElementCollection
    private final Set<String> scopes;
}