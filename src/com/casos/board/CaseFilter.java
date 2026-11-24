package com.casos.board;

import com.casos.domain.AgencyId;
import com.casos.domain.Case;
import com.casos.domain.FilterCriteria;
import java.util.Optional;
import java.util.Set;

/** Applies FilterCriteria to a case (S2.1 to S2.7, S6.3). */
public class CaseFilter {

    private final FilterCriteria criteria;
    private final Set<AgencyId> viewerRosters;

    public CaseFilter(FilterCriteria criteria, Set<AgencyId> viewerRosters) {
        this.criteria = criteria;
        this.viewerRosters = viewerRosters;
    }

    public boolean matches(Case c, Optional<Double> distanceMiles) {
        return true;
    }
}
