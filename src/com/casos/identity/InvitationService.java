package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.AgencyMember;
import com.casos.domain.MemberId;
import com.casos.domain.MemberRepository;
import com.casos.domain.Role;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Owner invitations and their claim on first sign-in (S9.2, S9.3). Membership is never self-granted. */
public class InvitationService {

    private final InvitationRepository invitations;
    private final MemberRepository members;
    private final Clock clock;

    public InvitationService(InvitationRepository invitations, MemberRepository members) {
        this(invitations, members, Clock.systemUTC());
    }

    public InvitationService(InvitationRepository invitations, MemberRepository members, Clock clock) {
        this.invitations = invitations;
        this.members = members;
        this.clock = clock;
    }

    public Invitation invite(AgencyId agency, String email, Role role, MemberId by) {
        Invitation invitation = new Invitation(UUID.randomUUID(), agency, email.trim().toLowerCase(), role, by, clock.instant());
        invitations.save(invitation);
        return invitation;
    }

    /** Creates an AgencyMember for every open invitation addressed to this email. */
    public List<AgencyMember> claimOnSignIn(String email) {
        List<AgencyMember> created = new ArrayList<>();
        for (Invitation invitation : invitations.openFor(email)) {
            AgencyMember member = new AgencyMember(MemberId.newId(), invitation.agencyId(), email,
                    invitation.role(), clock.instant());
            members.save(member);
            invitation.markClaimed(clock.instant());
            created.add(member);
        }
        return created;
    }
}
