package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import com.casos.domain.AgencyRepository;
import com.casos.domain.Case;
import com.casos.domain.CaseDraft;
import com.casos.domain.CaseFilled;
import com.casos.domain.CaseId;
import com.casos.domain.CasePosted;
import com.casos.domain.CaseRenewed;
import com.casos.domain.CaseRepository;
import com.casos.domain.DomainEventBus;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
import com.casos.identity.UnauthorizedException;
import java.time.Clock;
import java.util.List;

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

    /** S10.1 to S10.7. The case is posted into the actor's active agency. Publishes CasePosted. */
    public Case post(CaseDraft draft, Session actor) {
        AgencyId agency = activeAgency(actor);
        Case created = factory.create(draft, agency);
        cases.save(created);
        agencies.findById(agency).ifPresent(a -> {
            a.recordPostedZip(draft.zip());
            agencies.save(a);
        });
        bus.publish(new CasePosted(clock.instant(), created.caseId(), agency, created.county()));
        return created;
    }

    /** S11.3. Publishes CaseFilled. */
    public Case markFilled(CaseId id, Session actor) {
        Case c = loadFor(id, actor);
        try {
            c.markFilled(clock.instant());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Case #" + c.caseNumber() + " is no longer open, so it cannot be marked filled.");
        }
        cases.save(c);
        bus.publish(new CaseFilled(clock.instant(), c.caseId(), c.agencyId()));
        return c;
    }

    /** S11.4. Resets the seven-day window. Publishes CaseRenewed. */
    public Case renew(CaseId id, Session actor) {
        Case c = loadFor(id, actor);
        try {
            c.renew(clock.instant());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Case #" + c.caseNumber() + " is no longer open, so it cannot be renewed.");
        }
        cases.save(c);
        bus.publish(new CaseRenewed(clock.instant(), c.caseId(), c.agencyId(), c.expiresAt()));
        return c;
    }

    /** Every case the actor's agency has posted (SCR-06 Agency Dashboard). */
    public List<Case> casesFor(Session actor) {
        return cases.forAgency(activeAgency(actor));
    }

    /** S11.6. One-click action from the 48-hour nudge email. Specified; not part of this prototype's journey. */
    public Case markFilledByToken(String token) {
        throw new UnsupportedOperationException("One-click email actions are specified (SD-4) but not in the prototype.");
    }

    /** S11.6. One-click action from the 48-hour nudge email. Specified; not part of this prototype's journey. */
    public Case renewByToken(String token) {
        throw new UnsupportedOperationException("One-click email actions are specified (SD-4) but not in the prototype.");
    }

    private AgencyId activeAgency(Session actor) {
        AgencyId agency = actor == null ? null : actor.activeAgencyId().orElse(null);
        if (agency == null || !authz.canManageCases(actor, agency)) {
            throw new UnauthorizedException("Sign in as an agency coordinator to manage cases.");
        }
        return agency;
    }

    private Case loadFor(CaseId id, Session actor) {
        Case c = cases.findById(id).orElseThrow(() -> new IllegalArgumentException("Case not found."));
        if (!authz.canManageCases(actor, c.agencyId())) {
            throw new UnauthorizedException("This case belongs to another agency.");
        }
        return c;
    }
}
