package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.MemberId;
import com.casos.domain.Role;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** An owner's invitation for an email address to join the agency (S9.2). */
public class Invitation {

    private final UUID invitationId;
    private final AgencyId agencyId;
    private final String email;
    private final Role role;
    private final MemberId invitedBy;
    private final Instant createdAt;
    private Optional<Instant> claimedAt;

    public Invitation(UUID invitationId, AgencyId agencyId, String email, Role role,
                      MemberId invitedBy, Instant createdAt) {
        this.invitationId = invitationId;
        this.agencyId = agencyId;
        this.email = email;
        this.role = role;
        this.invitedBy = invitedBy;
        this.createdAt = createdAt;
        this.claimedAt = Optional.empty();
    }

    public boolean isClaimed() {
        return claimedAt.isPresent();
    }

    public void markClaimed(Instant at) {
        this.claimedAt = Optional.of(at);
    }

    public UUID invitationId() {
        return invitationId;
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

    public MemberId invitedBy() {
        return invitedBy;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
