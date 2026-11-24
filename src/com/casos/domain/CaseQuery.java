package com.casos.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Read-only access to cases. The board depends on this, not on the lifecycle module. */
public interface CaseQuery {

    List<Case> findOpen(Instant now);

    Optional<Case> findById(CaseId id);

    List<Case> findExpiringBetween(Instant from, Instant to);
}
