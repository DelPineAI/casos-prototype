package com.casos.lifecycle;

import com.casos.domain.CaseRepository;
import com.casos.domain.DomainEventBus;
import java.time.Clock;
import java.time.Duration;

/** Scheduled expiry and 48-hour nudge scans (S11.1, S11.5). Owned by M2, not M7 (Round 2, F3). */
public class ExpiryJob {

    private final CaseRepository cases;
    private final OneClickTokenService tokens;
    private final DomainEventBus bus;
    private final Clock clock;
    private final Duration nudgeLead;

    public ExpiryJob(CaseRepository cases, OneClickTokenService tokens, DomainEventBus bus, Clock clock) {
        this.cases = cases;
        this.tokens = tokens;
        this.bus = bus;
        this.clock = clock;
        this.nudgeLead = Duration.ofHours(48);
    }

    /** Expires overdue OPEN cases and publishes CaseExpired. Returns the count expired. */
    public int runNightlyExpiry() {
        return 0;
    }

    /** Issues FILL and RENEW tokens for cases expiring in about 48 hours and publishes CaseExpiringSoon. */
    public int runHourlyNudgeScan() {
        return 0;
    }
}
