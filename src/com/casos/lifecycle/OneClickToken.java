package com.casos.lifecycle;

import com.casos.domain.CaseAction;
import com.casos.domain.CaseId;
import java.time.Instant;
import java.util.Optional;

/** A signed, single-use token embedded in the 48-hour nudge email (S11.6). */
public class OneClickToken {

    private final String token;
    private final CaseId caseId;
    private final CaseAction action;
    private final Instant expiresAt;
    private Optional<Instant> redeemedAt;

    public OneClickToken(String token, CaseId caseId, CaseAction action, Instant expiresAt) {
        this.token = token;
        this.caseId = caseId;
        this.action = action;
        this.expiresAt = expiresAt;
        this.redeemedAt = Optional.empty();
    }

    public boolean isRedeemable(Instant now) {
        return redeemedAt.isEmpty() && now.isBefore(expiresAt);
    }

    public void redeem(Instant at) {
        this.redeemedAt = Optional.of(at);
    }

    public String token() {
        return token;
    }

    public CaseId caseId() {
        return caseId;
    }

    public CaseAction action() {
        return action;
    }

    public Instant expiresAt() {
        return expiresAt;
    }
}
