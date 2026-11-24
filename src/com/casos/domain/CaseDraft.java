package com.casos.domain;

import java.util.Optional;
import java.util.Set;

/** Agency input for a new case. Every field is a selection from a controlled list (S10.2). */
public final class CaseDraft {

    private final AgencyId agencyId;
    private final CoverageType coverageType;
    private final Credential credentialRequired;
    private final ZipCode zip;
    private final County county;
    private final AgeBand ageBand;
    private final int weeklyHours;
    private final WeeklySchedule schedule;
    private final InsurancePlan insurancePlan;
    private final Set<Competency> competencies;
    private final Optional<Integer> hourlyRateCents;
    private final Set<StaffingPreference> preferences;

    public CaseDraft(AgencyId agencyId,
                     CoverageType coverageType,
                     Credential credentialRequired,
                     ZipCode zip,
                     County county,
                     AgeBand ageBand,
                     int weeklyHours,
                     WeeklySchedule schedule,
                     InsurancePlan insurancePlan,
                     Set<Competency> competencies,
                     Optional<Integer> hourlyRateCents,
                     Set<StaffingPreference> preferences) {
        this.agencyId = agencyId;
        this.coverageType = coverageType;
        this.credentialRequired = credentialRequired;
        this.zip = zip;
        this.county = county;
        this.ageBand = ageBand;
        this.weeklyHours = weeklyHours;
        this.schedule = schedule;
        this.insurancePlan = insurancePlan;
        this.competencies = competencies;
        this.hourlyRateCents = hourlyRateCents;
        this.preferences = preferences;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public CoverageType coverageType() {
        return coverageType;
    }

    public Credential credentialRequired() {
        return credentialRequired;
    }

    public ZipCode zip() {
        return zip;
    }

    public County county() {
        return county;
    }

    public AgeBand ageBand() {
        return ageBand;
    }

    public int weeklyHours() {
        return weeklyHours;
    }

    public WeeklySchedule schedule() {
        return schedule;
    }

    public InsurancePlan insurancePlan() {
        return insurancePlan;
    }

    public Set<Competency> competencies() {
        return competencies;
    }

    public Optional<Integer> hourlyRateCents() {
        return hourlyRateCents;
    }

    public Set<StaffingPreference> preferences() {
        return preferences;
    }
}
