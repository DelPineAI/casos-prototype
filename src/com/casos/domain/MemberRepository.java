package com.casos.domain;

import java.util.List;

public interface MemberRepository {

    void save(AgencyMember m);

    List<AgencyMember> findByEmail(String email);

    List<AgencyMember> membersOf(AgencyId agencyId);
}
