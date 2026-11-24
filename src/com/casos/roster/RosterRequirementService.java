package com.casos.roster;

import com.casos.domain.Agency;
import com.casos.domain.AgencyId;
import com.casos.domain.AgencyRepository;
import com.casos.domain.RosterRequirement;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.Session;

/** Owner publishes the agency's roster requirement (S13.5). */
public class RosterRequirementService {

    private final AgencyRepository agencies;
    private final AuthorizationPolicy authz;

    public RosterRequirementService(AgencyRepository agencies, AuthorizationPolicy authz) {
        this.agencies = agencies;
        this.authz = authz;
    }

    public Agency publish(AgencyId a, RosterRequirement r, Session actor) {
        return null;
    }
}
