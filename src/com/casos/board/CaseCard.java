package com.casos.board;

import com.casos.domain.Case;
import com.casos.domain.CaseId;
import com.casos.domain.Competency;
import com.casos.domain.CoverageType;
import com.casos.domain.Credential;
import com.casos.domain.GeoPoint;
import com.casos.domain.InsurancePlan;
import com.casos.domain.WeeklySchedule;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/** Read model for one board row. Never carries the ZIP or exact pin (S14.3). */
public class CaseCard {

    private final CaseId caseId;
    private final int caseNumber;
    private final CoverageType coverageType;
    private final Optional<Double> distanceMiles;
    private final int weeklyHours;
    private final WeeklySchedule schedule;
    private final Credential credentialRequired;
    private final Set<Competency> competencies;
    private final InsurancePlan insurancePlan;
    private final Optional<Integer> hourlyRateCents;
    private final Optional<Integer> overlapScore;
    private final GeoPoint displayPin;
    private final Instant postedAt;

    public CaseCard(Case c, Optional<Double> distance, Optional<Integer> overlap) {
        this.caseId = c.caseId();
        this.caseNumber = c.caseNumber();
        this.coverageType = c.coverageType();
        this.distanceMiles = distance;
        this.weeklyHours = c.weeklyHours();
        this.schedule = c.schedule();
        this.credentialRequired = c.credentialRequired();
        this.competencies = c.competencies();
        this.insurancePlan = c.insurancePlan();
        this.hourlyRateCents = c.hourlyRateCents();
        this.overlapScore = overlap;
        this.displayPin = c.displayPin();
        this.postedAt = c.postedAt();
    }

    /** Shareable deep link to this case (S1.4). */
    public String shareUrl() {
        return "/cases/" + caseId.value();
    }

    public CaseId caseId() {
        return caseId;
    }

    public int caseNumber() {
        return caseNumber;
    }

    public CoverageType coverageType() {
        return coverageType;
    }

    public Optional<Double> distanceMiles() {
        return distanceMiles;
    }

    public int weeklyHours() {
        return weeklyHours;
    }

    public WeeklySchedule schedule() {
        return schedule;
    }

    public Credential credentialRequired() {
        return credentialRequired;
    }

    public Set<Competency> competencies() {
        return competencies;
    }

    public InsurancePlan insurancePlan() {
        return insurancePlan;
    }

    public Optional<Integer> hourlyRateCents() {
        return hourlyRateCents;
    }

    public Optional<Integer> overlapScore() {
        return overlapScore;
    }

    public GeoPoint displayPin() {
        return displayPin;
    }

    public Instant postedAt() {
        return postedAt;
    }
}
