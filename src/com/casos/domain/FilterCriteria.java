package com.casos.domain;

import java.util.Optional;
import java.util.Set;

/** Immutable filter selection for the public board (S2.1 to S2.7). */
public final class FilterCriteria {

    private final boolean coverageOnly;
    private final Optional<Integer> maxDistanceMiles;
    private final Optional<Integer> minWeeklyHours;
    private final Optional<Credential> credential;
    private final Optional<InsurancePlan> insurancePlan;
    private final Set<TimeBlock> timeBlocks;
    private final Set<Competency> competencies;
    private final boolean myRostersOnly;

    public FilterCriteria(boolean coverageOnly,
                          Optional<Integer> maxDistanceMiles,
                          Optional<Integer> minWeeklyHours,
                          Optional<Credential> credential,
                          Optional<InsurancePlan> insurancePlan,
                          Set<TimeBlock> timeBlocks,
                          Set<Competency> competencies,
                          boolean myRostersOnly) {
        this.coverageOnly = coverageOnly;
        this.maxDistanceMiles = maxDistanceMiles;
        this.minWeeklyHours = minWeeklyHours;
        this.credential = credential;
        this.insurancePlan = insurancePlan;
        this.timeBlocks = timeBlocks;
        this.competencies = competencies;
        this.myRostersOnly = myRostersOnly;
    }

    public static FilterCriteria none() {
        return new FilterCriteria(false, Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of(), Set.of(), false);
    }

    public boolean coverageOnly() {
        return coverageOnly;
    }

    public Optional<Integer> maxDistanceMiles() {
        return maxDistanceMiles;
    }

    public Optional<Integer> minWeeklyHours() {
        return minWeeklyHours;
    }

    public Optional<Credential> credential() {
        return credential;
    }

    public Optional<InsurancePlan> insurancePlan() {
        return insurancePlan;
    }

    public Set<TimeBlock> timeBlocks() {
        return timeBlocks;
    }

    public Set<Competency> competencies() {
        return competencies;
    }

    public boolean myRostersOnly() {
        return myRostersOnly;
    }
}
