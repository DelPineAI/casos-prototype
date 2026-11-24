package com.casos.domain;

import java.util.Set;
import java.util.UUID;

/** Published minimum requirements to join an agency's roster (S13.5). */
public class RosterRequirement {

    private final UUID requirementId;
    private final AgencyId agencyId;
    private final Credential minimumCredential;
    private final Set<InServiceType> requiredInServices;
    private final Set<InsurancePlan> insurancePlans;

    public RosterRequirement(UUID requirementId, AgencyId agencyId, Credential minimumCredential,
                             Set<InServiceType> requiredInServices, Set<InsurancePlan> insurancePlans) {
        this.requirementId = requirementId;
        this.agencyId = agencyId;
        this.minimumCredential = minimumCredential;
        this.requiredInServices = requiredInServices;
        this.insurancePlans = insurancePlans;
    }

    public UUID requirementId() {
        return requirementId;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public Credential minimumCredential() {
        return minimumCredential;
    }

    public Set<InServiceType> requiredInServices() {
        return requiredInServices;
    }

    public Set<InsurancePlan> insurancePlans() {
        return insurancePlans;
    }
}
