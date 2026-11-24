package com.casos.lifecycle;

import com.casos.domain.AgencyRepository;
import com.casos.domain.Case;
import com.casos.domain.CaseDraft;
import com.casos.domain.CaseId;
import com.casos.domain.CaseRepository;
import com.casos.domain.DomainEventBus;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
import java.time.Clock;

/** Orchestrates case transitions: post, fill, renew, and their one-click email variants (S10, S11). */
public class CaseService {

    private final CaseRepository cases;
    private final CaseFactory factory;
    private final AgencyRepository agencies;
    private final AuthorizationPolicy authz;
    private final OneClickTokenService tokens;
    private final DomainEventBus bus;
    private final Clock clock;

    public CaseService(CaseRepository cases, CaseFactory factory, AgencyRepository agencies,
                       AuthorizationPolicy authz, OneClickTokenService tokens,
                       DomainEventBus bus, Clock clock) {
        this.cases = cases;
        this.factory = factory;
        this.agencies = agencies;
        this.authz = authz;
        this.tokens = tokens;
        this.bus = bus;
        this.clock = clock;
    }

    /** S10.1 to S10.7. Publishes CasePosted. */
    public Case post(CaseDraft draft, Session actor) {
        return null;
    }

    /** S11.3. Publishes CaseFilled. */
    public Case markFilled(CaseId id, Session actor) {
        return null;
    }

    /** S11.4. Publishes CaseRenewed. */
    public Case renew(CaseId id, Session actor) {
        return null;
    }

    /** S11.6. Redeems a FILL token from the nudge email; no session required. */
    public Case markFilledByToken(String token) {
        return null;
    }

    /** S11.6. Redeems a RENEW token from the nudge email; no session required. */
    public Case renewByToken(String token) {
        return null;
    }
}
