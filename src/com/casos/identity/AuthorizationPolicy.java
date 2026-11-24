package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.AgencyMember;
import com.casos.domain.MemberId;
import com.casos.domain.MemberRepository;
import com.casos.domain.Role;
import java.util.Optional;

/** Role checks against agency membership (S9.4, S13.4, S13.5). */
public class AuthorizationPolicy {

    private final MemberRepository members;

    public AuthorizationPolicy(MemberRepository members) {
        this.members = members;
    }

    public boolean isMemberOf(MemberId m, AgencyId a) {
        return membership(m, a).isPresent();
    }

    /** Owner or Coordinator, acting in that agency's context. */
    public boolean canManageCases(Session s, AgencyId a) {
        return actingIn(s, a) && s.memberId().map(m -> isMemberOf(m, a)).orElse(false);
    }

    /** Owner or Coordinator. */
    public boolean canDecideApplications(Session s, AgencyId a) {
        return canManageCases(s, a);
    }

    /** Owner only. */
    public boolean canPublishRequirements(Session s, AgencyId a) {
        return actingIn(s, a) && s.memberId().flatMap(m -> membership(m, a))
                .map(member -> member.role() == Role.OWNER).orElse(false);
    }

    private boolean actingIn(Session s, AgencyId a) {
        return s != null && !s.isAnonymous() && s.activeAgencyId().map(a::equals).orElse(false);
    }

    private Optional<AgencyMember> membership(MemberId m, AgencyId a) {
        return members.membersOf(a).stream().filter(member -> member.memberId().equals(m)).findFirst();
    }
}
