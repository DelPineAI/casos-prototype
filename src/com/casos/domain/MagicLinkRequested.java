package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class MagicLinkRequested extends DomainEvent {

    private final String email;
    private final String token;

    public MagicLinkRequested(Instant occurredAt, String email, String token) {
        super(occurredAt, Optional.empty());
        this.email = email;
        this.token = token;
    }

    @Override
    public String type() {
        return "MagicLinkRequested";
    }

    public String email() {
        return email;
    }

    public String token() {
        return token;
    }
}
