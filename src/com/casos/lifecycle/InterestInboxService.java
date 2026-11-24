package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import com.casos.domain.CaseId;
import com.casos.domain.Interest;
import com.casos.domain.InterestId;
import com.casos.domain.InterestRepository;
import com.casos.domain.InterestStatus;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
import com.casos.identity.UnauthorizedException;
import java.util.List;
import java.util.Map;

/** Coordinator view of expressions of interest (U12.0, S12.1 to S12.4). */
public class InterestInboxService {

    private final InterestRepository interests;
    private final AuthorizationPolicy authz;

    public InterestInboxService(InterestRepository interests, AuthorizationPolicy authz) {
        this.interests = interests;
        this.authz = authz;
    }

    public List<Interest> forAgency(AgencyId a, Session actor) {
        require(a, actor);
        return interests.forAgency(a);
    }

    public Map<CaseId, Integer> countsByCase(AgencyId a, Session actor) {
        require(a, actor);
        return interests.countByCase(a);
    }

    public Interest advance(InterestId id, InterestStatus next, Session actor) {
        Interest interest = interests.findById(id).orElseThrow(() -> new IllegalArgumentException("Interest not found."));
        require(interest.agencyId(), actor);
        interest.advance(next);
        interests.save(interest);
        return interest;
    }

    private void require(AgencyId a, Session actor) {
        if (!authz.canManageCases(actor, a)) {
            throw new UnauthorizedException("Only this agency's coordinators can see its interest inbox (S12.4).");
        }
    }
}
