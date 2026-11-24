package com.casos.analytics;

import com.casos.domain.AgencyId;
import com.casos.domain.CaseId;
import com.casos.domain.CaseQuery;
import com.casos.domain.DomainEvent;
import java.util.Optional;

/**
 * Server-side attribution (S15.3): an event about a case is always attributed to the agency that owns
 * that case, never to whatever agency a client claims. An event whose claimed agency disagrees is rejected.
 */
public class AttributionValidator {

    private final CaseQuery cases;

    public AttributionValidator(CaseQuery cases) {
        this.cases = cases;
    }

    public boolean isValid(DomainEvent e) {
        Optional<CaseId> caseId = EventFields.caseId(e);
        if (caseId.isEmpty() || e.agencyId().isEmpty()) {
            return true;
        }
        return owner(caseId.get()).map(owner -> owner.equals(e.agencyId().get())).orElse(true);
    }

    /** The agency to record against: the case owner when the event concerns a case, else the event's own. */
    public Optional<AgencyId> attributedAgency(DomainEvent e) {
        return EventFields.caseId(e).flatMap(this::owner).or(e::agencyId);
    }

    private Optional<AgencyId> owner(CaseId id) {
        return cases.findById(id).map(c -> c.agencyId());
    }
}
