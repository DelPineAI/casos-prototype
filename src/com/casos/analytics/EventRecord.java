package com.casos.analytics;

import com.casos.domain.AgencyId;
import com.casos.domain.CaseId;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Immutable stored form of a domain event (S15.1). */
public record EventRecord(UUID recordId,
                          String type,
                          Instant occurredAt,
                          Optional<AgencyId> agencyId,
                          Optional<CaseId> caseId,
                          Optional<String> sessionId,
                          String payloadJson) {
}
