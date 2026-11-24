package com.casos.roster;

import com.casos.domain.Agency;
import com.casos.domain.AgencyId;
import com.casos.domain.AgencyRepository;
import com.casos.domain.ClinicianId;
import com.casos.domain.ClinicianRepository;
import com.casos.domain.GeoPoint;
import com.casos.domain.GeoResolver;
import java.util.List;
import java.util.Optional;

/** Agency directory and requirement comparison (U7.0, S7.1 to S7.5). */
public class RosterDiscoveryService {

    private final AgencyRepository agencies;
    private final ClinicianRepository clinicians;
    private final GeoResolver geo;

    public RosterDiscoveryService(AgencyRepository agencies, ClinicianRepository clinicians, GeoResolver geo) {
        this.agencies = agencies;
        this.clinicians = clinicians;
        this.geo = geo;
    }

    public List<AgencyListing> discover(Optional<ClinicianId> viewer, Optional<GeoPoint> location) {
        return List.of();
    }

    public RequirementGap compare(ClinicianId id, AgencyId agency) {
        return null;
    }

    /** Agencies the clinician has not self-reported membership in (S7.3). */
    public List<Agency> unaffiliated(Optional<ClinicianId> viewer) {
        return List.of();
    }
}
