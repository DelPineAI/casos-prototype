package com.casos.analytics;

import com.casos.domain.DomainEvent;
import com.casos.domain.DomainEventBus;

/** Records every domain event. Subscribed with subscribeAll by the composition root. */
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
    }

    private EventRecord toRecord(DomainEvent e) {
        return null;
    }
}
