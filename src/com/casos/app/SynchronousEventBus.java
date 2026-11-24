package com.casos.app;

import com.casos.domain.DomainEvent;
import com.casos.domain.DomainEventBus;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** In-process bus that dispatches to typed handlers and catch-all handlers synchronously. */
public class SynchronousEventBus implements DomainEventBus {

    private final Map<Class<? extends DomainEvent>, List<Consumer<? extends DomainEvent>>> typed;
    private final List<Consumer<DomainEvent>> catchAll;

    public SynchronousEventBus() {
        this.typed = new ConcurrentHashMap<>();
        this.catchAll = new CopyOnWriteArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(DomainEvent e) {
        for (Consumer<? extends DomainEvent> handler : typed.getOrDefault(e.getClass(), List.of())) {
            ((Consumer<DomainEvent>) handler).accept(e);
        }
        for (Consumer<DomainEvent> handler : catchAll) {
            handler.accept(e);
        }
    }

    @Override
    public <E extends DomainEvent> void subscribe(Class<E> type, Consumer<E> handler) {
        typed.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public void subscribeAll(Consumer<DomainEvent> handler) {
        catchAll.add(handler);
    }
}
