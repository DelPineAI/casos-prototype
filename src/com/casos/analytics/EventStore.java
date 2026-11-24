package com.casos.analytics;

import com.casos.domain.AgencyId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Append-only event log (S15.1, S15.2). */
public interface EventStore {

    void append(EventRecord r);

    List<EventRecord> query(Optional<AgencyId> scope, Instant from, Instant to, Optional<String> type);
}
