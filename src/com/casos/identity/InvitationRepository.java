package com.casos.identity;

import java.util.List;

public interface InvitationRepository {

    void save(Invitation invitation);

    /** Unclaimed invitations addressed to this email. */
    List<Invitation> openFor(String email);
}
