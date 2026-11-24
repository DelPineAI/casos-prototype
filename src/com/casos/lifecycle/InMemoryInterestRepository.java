package com.casos.lifecycle;

import com.casos.domain.AgencyId;
import com.casos.domain.CaseId;
import com.casos.domain.Interest;
import com.casos.domain.InterestId;
import com.casos.domain.InterestRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Prototype store for expressions of interest. */
public class InMemoryInterestRepository implements InterestRepository {

    private final Map<InterestId, Interest> interests = new ConcurrentHashMap<>();

    @Override
    public void save(Interest i) {
        interests.put(i.interestId(), i);
    }

    @Override
    public Optional<Interest> findById(InterestId id) {
        return Optional.ofNullable(interests.get(id));
    }

    @Override
    public List<Interest> forAgency(AgencyId agencyId) {
        return interests.values().stream()
                .filter(i -> i.agencyId().equals(agencyId))
                .sorted(Comparator.comparing(Interest::submittedAt).reversed())
                .toList();
    }

    @Override
    public Map<CaseId, Integer> countByCase(AgencyId agencyId) {
        Map<CaseId, Integer> counts = new HashMap<>();
        forAgency(agencyId).forEach(i -> counts.merge(i.caseId(), 1, Integer::sum));
        return counts;
    }
}
