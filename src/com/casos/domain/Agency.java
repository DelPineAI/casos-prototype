package com.casos.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** An ABA agency that posts cases and publishes roster requirements. */
public class Agency {

    private final AgencyId agencyId;
    private final String name;
    private final Set<County> serviceArea;
    private Optional<ZipCode> lastPostedZip;
    private final List<RosterRequirement> requirements;

    public Agency(AgencyId agencyId, String name, Set<County> serviceArea) {
        this.agencyId = agencyId;
        this.name = name;
        this.serviceArea = serviceArea;
        this.lastPostedZip = Optional.empty();
        this.requirements = new ArrayList<>();
    }

    /** Publish or replace the agency's roster requirement (S13.5). */
    public void publishRequirement(RosterRequirement r) {
    }

    /** Remember the last posted ZIP so the next draft can default to it (S10.3). */
    public void recordPostedZip(ZipCode z) {
        this.lastPostedZip = Optional.of(z);
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public String name() {
        return name;
    }

    public Set<County> serviceArea() {
        return serviceArea;
    }

    public Optional<ZipCode> lastPostedZip() {
        return lastPostedZip;
    }

    public List<RosterRequirement> requirements() {
        return requirements;
    }
}
