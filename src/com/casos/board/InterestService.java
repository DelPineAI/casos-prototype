package com.casos.board;

import com.casos.compliance.FreeTextGuard;
import com.casos.domain.Case;
import com.casos.domain.CaseQuery;
import com.casos.domain.DomainEventBus;
import com.casos.domain.Interest;
import com.casos.domain.InterestId;
import com.casos.domain.InterestRepository;
import com.casos.domain.InterestSubmitted;
import java.time.Clock;
import java.time.Instant;

/** The one write on the public board: expressing interest, with no account required (U4.0, S4.1 to S4.4). */
public class InterestService {

    private final CaseQuery cases;
    private final InterestRepository interests;
    private final DomainEventBus bus;
    private final Clock clock;
    private final FreeTextGuard guard = new FreeTextGuard();

    public InterestService(CaseQuery cases, InterestRepository interests, DomainEventBus bus, Clock clock) {
        this.cases = cases;
        this.interests = interests;
        this.bus = bus;
        this.clock = clock;
    }

    /** Throws CaseNoLongerOpenException when the case is not visible (S4.4). Publishes InterestSubmitted. */
    public Interest expressInterest(InterestRequest req) {
        if (req.contact() == null || req.contact().name() == null || req.contact().name().isBlank()) {
            throw new IllegalArgumentException("Enter your name so the agency knows who you are.");
        }
        String email = req.contact().email() == null ? "" : req.contact().email().trim();
        String phone = req.contact().phone() == null ? "" : req.contact().phone().trim();
        if (email.isEmpty() && phone.isEmpty()) {
            throw new IllegalArgumentException("Enter an email or phone number so the agency can reach you.");
        }
        if (!email.isEmpty() && !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("That email address does not look right.");
        }
        if (req.note().map(guard::containsIdentifier).orElse(false)) {
            throw new IllegalArgumentException(
                    "Your note looks like it contains client details (a date of birth or member ID). Please remove them.");
        }

        Instant now = clock.instant();
        Case target = cases.findById(req.caseId())
                .filter(c -> c.isVisibleAt(now))
                .orElseThrow(() -> new CaseNoLongerOpenException(
                        "This case was just filled or has expired, so it is no longer taking interest."));

        Interest interest = new Interest(InterestId.newId(), target.caseId(), target.agencyId(),
                req.clinicianId(), req.contact(), req.note().filter(n -> !n.isBlank()), now);
        interests.save(interest);
        bus.publish(new InterestSubmitted(now, interest.interestId(), target.caseId(), target.agencyId()));
        return interest;
    }
}
