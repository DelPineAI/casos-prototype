package com.casos.analytics;

import com.casos.domain.DomainEvent;
import com.casos.domain.DomainEventBus;
import com.casos.domain.MagicLinkRequested;

/** Records every domain event to the append-only log (S15.1, S15.2). Subscribed with subscribeAll. */
public class AnalyticsSubscriber {

    private final EventStore store;
    private final AttributionValidator validator;

    public AnalyticsSubscriber(EventStore store, AttributionValidator validator) {
        this.store = store;
        this.validator = validator;
    }

    public void register(DomainEventBus bus) {
        bus.subscribeAll(this::on);
    }

    public void on(DomainEvent e) {
        if (e instanceof MagicLinkRequested) {
            return; // sign-in tokens are never written to the analytics log
        }
        if (!validator.isValid(e)) {
            return; // mis-attributed events are dropped rather than counted against the wrong agency
        }
        store.append(toRecord(e));
    }

    private EventRecord toRecord(DomainEvent e) {
        return new EventRecord(e.eventId(), e.type(), e.occurredAt(), validator.attributedAgency(e),
                EventFields.caseId(e), EventFields.sessionId(e), "{}");
    }
}
