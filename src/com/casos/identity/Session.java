package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.MemberId;
import java.time.Instant;
import java.util.Optional;

/** An authenticated staff session with an active agency context (S9.6). */
public class Session {

    private final String sessionId;
    private final String email;
    private final Optional<MemberId> memberId;
    private Optional<AgencyId> activeAgencyId;
    private final Instant createdAt;

    public Session(String sessionId, String email, Optional<MemberId> memberId,
                   Optional<AgencyId> activeAgencyId, Instant createdAt) {
        this.sessionId = sessionId;
        this.email = email;
        this.memberId = memberId;
        this.activeAgencyId = activeAgencyId;
        this.createdAt = createdAt;
    }

    public void switchAgency(AgencyId target) {
        this.activeAgencyId = Optional.of(target);
    }

    public boolean isAnonymous() {
        return memberId.isEmpty();
    }

    public String sessionId() {
        return sessionId;
    }

    public String email() {
        return email;
    }

    public Optional<MemberId> memberId() {
        return memberId;
    }

    public Optional<AgencyId> activeAgencyId() {
        return activeAgencyId;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
