package com.casos.identity;

import com.casos.domain.AgencyId;
import com.casos.domain.AgencyMember;
import com.casos.domain.MemberId;
import com.casos.domain.MemberRepository;
import com.casos.domain.Role;
import java.util.List;

/** Owner invitations and their claim on first sign-in (S9.2, S9.3). */
public class InvitationService {

    private final InvitationRepository invitations;
    private final MemberRepository members;

    public InvitationService(InvitationRepository invitations, MemberRepository members) {
        this.invitations = invitations;
        this.members = members;
    }

    public Invitation invite(AgencyId agency, String email, Role role, MemberId by) {
        return null;
    }

    /** Creates an AgencyMember for every open invitation addressed to this email. */
    public List<AgencyMember> claimOnSignIn(String email) {
        return List.of();
    }
}
