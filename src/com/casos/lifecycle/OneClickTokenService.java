package com.casos.lifecycle;

import com.casos.domain.CaseAction;
import com.casos.domain.CaseId;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Issues and redeems one-click tokens (S11.6). */
public class OneClickTokenService {

    private final Map<String, OneClickToken> tokens;
    private final Clock clock;

    public OneClickTokenService(Clock clock) {
        this.tokens = new HashMap<>();
        this.clock = clock;
    }

    public OneClickToken issue(CaseId caseId, CaseAction action, Duration ttl) {
        return null;
    }

    /** Returns the case id if the token is valid and matches the expected action. */
    public Optional<CaseId> redeem(String token, CaseAction expected) {
        return Optional.empty();
    }
}
