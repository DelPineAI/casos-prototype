package com.casos.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/** Flat persistence memento for a Case. Used only by repositories, never by presentation code. */
public record CaseRecord(CaseId caseId,
                         AgencyId agencyId,
                         int caseNumber,
                         CoverageType coverageType,
                         Credential credentialRequired,
                         ZipCode zip,
                         GeoPoint exactPin,
                         GeoPoint displayPin,
                         County county,
                         AgeBand ageBand,
                         int weeklyHours,
                         WeeklySchedule schedule,
                         InsurancePlan insurancePlan,
                         Set<Competency> competencies,
                         Optional<Integer> hourlyRateCents,
                         Set<StaffingPreference> preferences,
                         CaseStatus status,
                         Instant postedAt,
                         Instant expiresAt,
                         Optional<Instant> filledAt) {

    /** Rebuild the entity from storage. */
    public Case toCase() {
        return null;
    }
}
