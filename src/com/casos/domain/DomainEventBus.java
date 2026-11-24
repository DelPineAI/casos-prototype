package com.casos.domain;

import java.util.function.Consumer;

/** Typed publish/subscribe port. Implemented in com.casos.app (Round 4, W3). */
public interface DomainEventBus {

    void publish(DomainEvent e);

    <E extends DomainEvent> void subscribe(Class<E> type, Consumer<E> handler);

    void subscribeAll(Consumer<DomainEvent> handler);
}
