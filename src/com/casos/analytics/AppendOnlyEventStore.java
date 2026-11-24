package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** In-memory append-only store. No update or delete methods exist by design. */
public class AppendOnlyEventStore implements EventStore {

    private final List<EventRecord> records;

    public AppendOnlyEventStore() {
        this.records = new ArrayList<>();
    }

    @Override
    public void append(EventRecord r) {
        records.add(r);
    }

    @Override
    public List<EventRecord> query(Optional<AgencyId> scope, Instant from, Instant to, Optional<String> type) {
        return List.of();
    }
}
