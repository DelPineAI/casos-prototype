package com.casos.domain;

import java.time.Instant;

/** A staff user's membership in one agency, with a role. */
public class AgencyMember {

    private final MemberId memberId;
    private final AgencyId agencyId;
    private final String email;
    private final Role role;
    private final Instant joinedAt;

    public AgencyMember(MemberId memberId, AgencyId agencyId, String email, Role role, Instant joinedAt) {
        this.memberId = memberId;
        this.agencyId = agencyId;
        this.email = email;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public boolean isOwner() {
        return role == Role.OWNER;
    }

    public MemberId memberId() {
        return memberId;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public String email() {
        return email;
    }

    public Role role() {
        return role;
    }

    public Instant joinedAt() {
        return joinedAt;
    }
}
