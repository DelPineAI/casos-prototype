package com.casos.identity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Prototype store for agency invitations. */
public class InMemoryInvitationRepository implements InvitationRepository {

    private final Map<java.util.UUID, Invitation> invitations = new ConcurrentHashMap<>();

    @Override
    public void save(Invitation invitation) {
        invitations.put(invitation.invitationId(), invitation);
    }

    @Override
    public List<Invitation> openFor(String email) {
        return invitations.values().stream()
                .filter(i -> !i.isClaimed() && i.email().equalsIgnoreCase(email))
                .toList();
    }
}
