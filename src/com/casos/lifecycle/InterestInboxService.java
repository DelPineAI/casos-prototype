package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import com.casos.domain.CaseId;
import com.casos.domain.Interest;
import com.casos.domain.InterestId;
import com.casos.domain.InterestRepository;
import com.casos.domain.InterestStatus;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;
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
        return List.of();
    }

    public Map<CaseId, Integer> countsByCase(AgencyId a, Session actor) {
        return Map.of();
    }

    public Interest advance(InterestId id, InterestStatus next, Session actor) {
        return null;
    }
}
