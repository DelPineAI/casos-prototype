package com.casos.lifecycle;

import com.casos.domain.Agency;
import com.casos.domain.AgencyId;
import com.casos.domain.AgencyRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Prototype store for agencies. */
public class InMemoryAgencyRepository implements AgencyRepository {

    private final Map<AgencyId, Agency> agencies = new ConcurrentHashMap<>();

    @Override
    public void save(Agency a) {
        agencies.put(a.agencyId(), a);
    }

    @Override
    public Optional<Agency> findById(AgencyId id) {
        return Optional.ofNullable(agencies.get(id));
    }

    @Override
    public List<Agency> findAll() {
        return List.copyOf(agencies.values());
    }
}
