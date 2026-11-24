package com.casos.board;

import com.casos.domain.CaseQuery;
import com.casos.domain.DomainEventBus;
import com.casos.domain.Interest;
import com.casos.domain.InterestRepository;
import java.time.Clock;

/** The one write on the public board: expressing interest (U4.0, S4.1 to S4.4). */
public class InterestService {

    private final CaseQuery cases;
    private final InterestRepository interests;
    private final DomainEventBus bus;
    private final Clock clock;

    public InterestService(CaseQuery cases, InterestRepository interests, DomainEventBus bus, Clock clock) {
        this.cases = cases;
        this.interests = interests;
        this.bus = bus;
        this.clock = clock;
    }

    /** Throws CaseNoLongerOpenException when the case is not visible (S4.4). Publishes InterestSubmitted. */
    public Interest expressInterest(InterestRequest req) {
        return null;
    }
}
