package com.casos.domain;

import java.time.Instant;
import java.util.Optional;

public final class CaseExpiringSoon extends DomainEvent {

    private final CaseId caseId;
    private final Instant expiresAt;
    private final String fillToken;
    private final String renewToken;

    public CaseExpiringSoon(Instant occurredAt, CaseId caseId, AgencyId agencyId, Instant expiresAt, String fillToken, String renewToken) {
        super(occurredAt, Optional.of(agencyId));
        this.caseId = caseId;
        this.expiresAt = expiresAt;
        this.fillToken = fillToken;
        this.renewToken = renewToken;
    }

    @Override
    public String type() {
        return "CaseExpiringSoon";
    }

    public CaseId caseId() {
        return caseId;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public String fillToken() {
        return fillToken;
    }

    public String renewToken() {
        return renewToken;
    }
}
