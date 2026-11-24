package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import com.casos.domain.Case;
import com.casos.domain.CaseId;
import com.casos.domain.CaseRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prototype implementation of CaseRepository. findOpen applies the same rule as the production
 * public view: OPEN and not past its expiry instant, so expiry holds even if the nightly job never runs.
 */
public class InMemoryCaseRepository implements CaseRepository {

    private final Map<CaseId, Case> store;

    public InMemoryCaseRepository() {
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Case c) {
        store.put(c.caseId(), c);
    }

    @Override
    public List<Case> findOpen(Instant now) {
        return store.values().stream().filter(c -> c.isVisibleAt(now)).toList();
    }

    @Override
    public Optional<Case> findById(CaseId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Case> findExpiringBetween(Instant from, Instant to) {
        return store.values().stream()
                .filter(Case::isOpen)
                .filter(c -> !c.expiresAt().isBefore(from) && c.expiresAt().isBefore(to))
                .toList();
    }

    @Override
    public List<Case> forAgency(AgencyId agencyId) {
        return store.values().stream()
                .filter(c -> c.agencyId().equals(agencyId))
                .sorted(Comparator.comparing(Case::postedAt).reversed())
                .toList();
    }
}
