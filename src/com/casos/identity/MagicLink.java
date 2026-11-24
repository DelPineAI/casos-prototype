package com.casos.identity;

import java.time.Duration;
import java.time.Instant;

/** Single-use, time-limited sign-in token (S9.1). */
public class MagicLink {

    private final String token;
    private final String email;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private boolean used;

    public MagicLink(String token, String email, Instant issuedAt, Duration ttl) {
        this.token = token;
        this.email = email;
        this.issuedAt = issuedAt;
        this.expiresAt = issuedAt.plus(ttl);
        this.used = false;
    }

    public boolean isValid(Instant now) {
        return !used && now.isBefore(expiresAt);
    }

    public void consume() {
        this.used = true;
    }

    public String token() {
        return token;
    }

    public String email() {
        return email;
    }

    public Instant issuedAt() {
        return issuedAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }
}
