package com.casos.domain;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

    void save(RosterApplication a);

    Optional<RosterApplication> findById(ApplicationId id);

    List<RosterApplication> forAgency(AgencyId agencyId);
}
