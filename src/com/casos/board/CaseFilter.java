package com.casos.board;

import com.casos.domain.AgencyId;
import com.casos.domain.Case;
import com.casos.domain.CoverageType;
import com.casos.domain.FilterCriteria;
import java.util.Optional;
import java.util.Set;

/** Applies FilterCriteria to a case (S2.1 to S2.7, S6.3). Every criterion left empty matches everything. */
public class CaseFilter {

    private final FilterCriteria criteria;
    private final Set<AgencyId> viewerRosters;

    public CaseFilter(FilterCriteria criteria, Set<AgencyId> viewerRosters) {
        this.criteria = criteria;
        this.viewerRosters = viewerRosters;
    }

    public boolean matches(Case c, Optional<Double> distanceMiles) {
        if (criteria.coverageOnly() && c.coverageType() != CoverageType.COVERAGE) {
            return false;
        }
        if (criteria.maxDistanceMiles().isPresent()) {
            // A distance filter only makes sense once the visitor has set a location.
            if (distanceMiles.isEmpty() || distanceMiles.get() > criteria.maxDistanceMiles().get()) {
                return false;
            }
        }
        if (criteria.minWeeklyHours().isPresent() && c.weeklyHours() < criteria.minWeeklyHours().get()) {
            return false;
        }
        if (criteria.credential().isPresent() && c.credentialRequired() != criteria.credential().get()) {
            return false;
        }
        if (criteria.insurancePlan().isPresent() && !c.insurancePlan().equals(criteria.insurancePlan().get())) {
            return false;
        }
        if (!criteria.timeBlocks().isEmpty()
                && criteria.timeBlocks().stream().noneMatch(b -> c.schedule().includes(b))) {
            return false;
        }
        if (!criteria.competencies().isEmpty() && !c.competencies().containsAll(criteria.competencies())) {
            return false;
        }
        if (criteria.myRostersOnly() && !viewerRosters.contains(c.agencyId())) {
            return false;
        }
        return true;
    }
}
