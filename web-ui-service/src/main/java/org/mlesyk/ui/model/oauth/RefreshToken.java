package org.mlesyk.ui.model.oauth;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class RefreshToken {
    @Column(name = "refresh_token_value")
    private final String value;

    @Column(name = "refresh_token_issued_at")
    private final Instant issuedAt;
}