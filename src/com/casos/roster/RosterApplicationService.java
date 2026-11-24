package com.casos.roster;

import com.casos.domain.AgencyId;
import com.casos.domain.ApplicationId;
import com.casos.domain.ApplicationRepository;
import com.casos.domain.ApplicationStatus;
import com.casos.domain.Credential;
import com.casos.domain.DomainEventBus;
import com.casos.domain.RosterApplication;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

/** Apply, review and decide roster applications (U13.0, S7.6, S7.7, S13.1 to S13.4). */
public class RosterApplicationService {

    private final ApplicationRepository applications;
    private final AuthorizationPolicy authz;
    private final DomainEventBus bus;
    private final Clock clock;

    public RosterApplicationService(ApplicationRepository applications, AuthorizationPolicy authz,
                                    DomainEventBus bus, Clock clock) {
        this.applications = applications;
        this.authz = authz;
        this.bus = bus;
        this.clock = clock;
    }

    /** Takes an immutable snapshot and publishes ApplicationSubmitted. */
    public RosterApplication apply(ApplicationRequest req) {
        return null;
    }

    public List<RosterApplication> inbox(AgencyId a, Session actor, Optional<Credential> byCredential,
                                         Optional<ApplicationStatus> byStatus) {
        return List.of();
    }

    /** Publishes ApplicationDecided. */
    public RosterApplication decide(ApplicationId id, ApplicationStatus outcome, Session actor) {
        return null;
    }
}
