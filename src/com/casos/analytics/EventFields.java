package com.casos.analytics;

import com.casos.domain.BoardViewed;
import com.casos.domain.CaseDetailViewed;
import com.casos.domain.CaseExpired;
import com.casos.domain.CaseFilled;
import com.casos.domain.CaseId;
import com.casos.domain.CasePosted;
import com.casos.domain.CaseRenewed;
import com.casos.domain.DomainEvent;
import com.casos.domain.FilterApplied;
import com.casos.domain.InterestSubmitted;
import java.util.Optional;

/** Reads the common optional fields (case and session) off any domain event. */
final class EventFields {

    private EventFields() {
    }

    static Optional<CaseId> caseId(DomainEvent e) {
        if (e instanceof CasePosted x) return Optional.of(x.caseId());
        if (e instanceof CaseFilled x) return Optional.of(x.caseId());
        if (e instanceof CaseRenewed x) return Optional.of(x.caseId());
        if (e instanceof CaseExpired x) return Optional.of(x.caseId());
        if (e instanceof InterestSubmitted x) return Optional.of(x.caseId());
        if (e instanceof CaseDetailViewed x) return Optional.of(x.caseId());
        return Optional.empty();
    }

    static Optional<String> sessionId(DomainEvent e) {
        if (e instanceof BoardViewed x) return Optional.ofNullable(x.sessionId());
        if (e instanceof FilterApplied x) return Optional.ofNullable(x.sessionId());
        if (e instanceof CaseDetailViewed x) return Optional.ofNullable(x.sessionId());
        return Optional.empty();
    }
}
