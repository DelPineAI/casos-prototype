package com.casos.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Rich entity owning the three-state case lifecycle (OPEN, FILLED, EXPIRED).
 * The exact pin and ZIP are private with no getters; only distance is exposed (S14.3, S14.4).
 */
public class Case {

    public static final Duration EXPIRY = Duration.ofDays(7);

    private final CaseId caseId;
    private final AgencyId agencyId;
    private final int caseNumber;
    private final CoverageType coverageType;
    private final Credential credentialRequired;
    private final ZipCode zip;
    private final GeoPoint exactPin;
    private final GeoPoint displayPin;
    private final County county;
    private final AgeBand ageBand;
    private final int weeklyHours;
    private final WeeklySchedule schedule;
    private final InsurancePlan insurancePlan;
    private final Set<Competency> competencies;
    private final Optional<Integer> hourlyRateCents;
    private final Set<StaffingPreference> preferences;
    private CaseStatus status;
    private Instant postedAt;
    private Instant expiresAt;
    private Optional<Instant> filledAt;

    public Case(CaseId caseId, AgencyId agencyId, int caseNumber, CaseDraft draft,
                GeoPoint exactPin, GeoPoint displayPin, Instant postedAt) {
        this.caseId = caseId;
        this.agencyId = agencyId;
        this.caseNumber = caseNumber;
        this.coverageType = draft.coverageType();
        this.credentialRequired = draft.credentialRequired();
        this.zip = draft.zip();
        this.exactPin = exactPin;
        this.displayPin = displayPin;
        this.county = draft.county();
        this.ageBand = draft.ageBand();
        this.weeklyHours = draft.weeklyHours();
        this.schedule = draft.schedule();
        this.insurancePlan = draft.insurancePlan();
        this.competencies = draft.competencies();
        this.hourlyRateCents = draft.hourlyRateCents();
        this.preferences = draft.preferences();
        this.status = CaseStatus.OPEN;
        this.postedAt = postedAt;
        this.expiresAt = postedAt.plus(EXPIRY);
        this.filledAt = Optional.empty();
    }

    public boolean isOpen() {
        return status == CaseStatus.OPEN;
    }

    /** OPEN and not yet past its expiry instant (S1.5). */
    public boolean isVisibleAt(Instant now) {
        return status == CaseStatus.OPEN && now.isBefore(expiresAt);
    }

    /** Transition OPEN to FILLED. */
    public void markFilled(Instant at) {
        if (status != CaseStatus.OPEN) {
            throw new IllegalStateException("Only an OPEN case can be filled");
        }
        this.status = CaseStatus.FILLED;
        this.filledAt = Optional.of(at);
    }

    /** Reset the 7-day window from the given instant (S11.4). */
    public void renew(Instant at) {
        if (status != CaseStatus.OPEN) {
            throw new IllegalStateException("Only an OPEN case can be renewed");
        }
        this.postedAt = at;
        this.expiresAt = at.plus(EXPIRY);
    }

    /** Transition OPEN to EXPIRED. */
    public void expire() {
        if (status != CaseStatus.OPEN) {
            throw new IllegalStateException("Only an OPEN case can expire");
        }
        this.status = CaseStatus.EXPIRED;
    }

    /** Distance from a viewer to the exact pin, without revealing the pin itself. */
    public double distanceMilesFrom(GeoPoint viewer) {
        return exactPin.milesTo(viewer);
    }

    /** Persistence memento; the only path that carries the ZIP and exact pin out of the entity. */
    public CaseRecord toRecord() {
        return new CaseRecord(caseId, agencyId, caseNumber, coverageType, credentialRequired, zip,
                exactPin, displayPin, county, ageBand, weeklyHours, schedule, insurancePlan,
                competencies, hourlyRateCents, preferences, status, postedAt, expiresAt, filledAt);
    }

    public CaseId caseId() {
        return caseId;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public int caseNumber() {
        return caseNumber;
    }

    public CoverageType coverageType() {
        return coverageType;
    }

    public Credential credentialRequired() {
        return credentialRequired;
    }

    public GeoPoint displayPin() {
        return displayPin;
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

    public CaseStatus status() {
        return status;
    }

    public Instant postedAt() {
        return postedAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Optional<Instant> filledAt() {
        return filledAt;
    }
}
