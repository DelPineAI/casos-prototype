package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.MemberId;
import com.casos.domain.MemberRepository;

/** Role checks against agency membership (S9.4, S13.4, S13.5). */
public class AuthorizationPolicy {

    private final MemberRepository members;

    public AuthorizationPolicy(MemberRepository members) {
        this.members = members;
    }

    public boolean isMemberOf(MemberId m, AgencyId a) {
        return false;
    }

    /** Owner or Coordinator. */
    public boolean canManageCases(Session s, AgencyId a) {
        return false;
    }

    /** Owner or Coordinator. */
    public boolean canDecideApplications(Session s, AgencyId a) {
        return false;
    }

    /** Owner only. */
    public boolean canPublishRequirements(Session s, AgencyId a) {
        return false;
    }
}
