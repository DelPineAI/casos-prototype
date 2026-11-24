package com.casos.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InterestRepository {

    void save(Interest i);

    Optional<Interest> findById(InterestId id);

    List<Interest> forAgency(AgencyId agencyId);

    Map<CaseId, Integer> countByCase(AgencyId agencyId);
}
