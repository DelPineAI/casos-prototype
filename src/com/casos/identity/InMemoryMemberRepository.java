package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.AgencyMember;
import com.casos.domain.MemberId;
import com.casos.domain.MemberRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Prototype store for agency memberships. */
public class InMemoryMemberRepository implements MemberRepository {

    private final Map<MemberId, AgencyMember> members = new ConcurrentHashMap<>();

    @Override
    public void save(AgencyMember m) {
        members.put(m.memberId(), m);
    }

    @Override
    public List<AgencyMember> findByEmail(String email) {
        return members.values().stream()
                .filter(m -> m.email().equalsIgnoreCase(email))
                .sorted(Comparator.comparing(AgencyMember::joinedAt))
                .toList();
    }

    @Override
    public List<AgencyMember> membersOf(AgencyId agencyId) {
        return members.values().stream().filter(m -> m.agencyId().equals(agencyId)).toList();
    }
}
