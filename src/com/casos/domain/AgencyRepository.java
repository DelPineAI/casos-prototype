package com.casos.domain;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository {

    void save(Agency a);

    Optional<Agency> findById(AgencyId id);

    List<Agency> findAll();
}
