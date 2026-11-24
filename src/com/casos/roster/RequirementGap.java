package com.casos.roster;

import com.casos.domain.InServiceType;
import java.util.Set;

/** What a clinician is missing against an agency's published requirement (S7.4). */
public final class RequirementGap {

    private final boolean credentialMet;
    private final Set<InServiceType> missingInServices;
    private final boolean planMatch;

    public RequirementGap(boolean credentialMet, Set<InServiceType> missingInServices, boolean planMatch) {
        this.credentialMet = credentialMet;
        this.missingInServices = missingInServices;
        this.planMatch = planMatch;
    }

    public boolean isComplete() {
        return credentialMet && missingInServices.isEmpty() && planMatch;
    }

    public boolean credentialMet() {
        return credentialMet;
    }

    public Set<InServiceType> missingInServices() {
        return missingInServices;
    }

    public boolean planMatch() {
        return planMatch;
    }
}
