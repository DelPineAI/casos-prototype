package com.casos.lifecycle;

import com.casos.domain.Case;
import com.casos.domain.CaseId;
import com.casos.domain.CaseRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Development implementation of CaseRepository. */
public class InMemoryCaseRepository implements CaseRepository {

    private final Map<CaseId, Case> store;

    public InMemoryCaseRepository() {
        this.store = new HashMap<>();
    }

    @Override
    public void save(Case c) {
        store.put(c.caseId(), c);
    }

    @Override
    public List<Case> findOpen(Instant now) {
        return List.of();
    }

    @Override
    public Optional<Case> findById(CaseId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Case> findExpiringBetween(Instant from, Instant to) {
        return List.of();
    }
}
