package com.casos.roster;

import com.casos.domain.AgencyId;
import com.casos.domain.County;
import com.casos.domain.RosterRequirement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** One row of the agency directory (S7.1, S7.2, S7.5). */
public final class AgencyListing {

    private final AgencyId agencyId;
    private final String name;
    private final Set<County> serviceArea;
    private final List<RosterRequirement> requirements;
    private final Optional<Double> distanceMiles;
    private final Optional<RequirementGap> gap;

    public AgencyListing(AgencyId agencyId, String name, Set<County> serviceArea,
                         List<RosterRequirement> requirements, Optional<Double> distanceMiles,
                         Optional<RequirementGap> gap) {
        this.agencyId = agencyId;
        this.name = name;
        this.serviceArea = serviceArea;
        this.requirements = requirements;
        this.distanceMiles = distanceMiles;
        this.gap = gap;
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

    public List<RosterRequirement> requirements() {
        return requirements;
    }

    public Optional<Double> distanceMiles() {
        return distanceMiles;
    }

    public Optional<RequirementGap> gap() {
        return gap;
    }
}
