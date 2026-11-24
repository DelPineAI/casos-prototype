package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/** In-memory append-only store. No update or delete methods exist by design (S15.1). */
public class AppendOnlyEventStore implements EventStore {

    private final List<EventRecord> records;

    public AppendOnlyEventStore() {
        this.records = new CopyOnWriteArrayList<>();
    }

    @Override
    public void append(EventRecord r) {
        records.add(r);
    }

    @Override
    public List<EventRecord> query(Optional<AgencyId> scope, Instant from, Instant to, Optional<String> type) {
        return records.stream()
                .filter(r -> scope.isEmpty() || scope.equals(r.agencyId()))
                .filter(r -> !r.occurredAt().isBefore(from) && r.occurredAt().isBefore(to))
                .filter(r -> type.isEmpty() || type.get().equals(r.type()))
                .toList();
    }

    public int size() {
        return records.size();
    }
}
