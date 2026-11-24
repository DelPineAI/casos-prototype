package com.casos.identity;

import com.casos.domain.DomainEventBus;
import com.casos.domain.MemberRepository;
import java.time.Clock;

/** Magic-link request and verification (S9.1, S9.3). */
public class AuthService {

    private final MagicLinkRepository links;
    private final MemberRepository members;
    private final InvitationService invitations;
    private final DomainEventBus bus;
    private final Clock clock;

    public AuthService(MagicLinkRepository links, MemberRepository members,
                       InvitationService invitations, DomainEventBus bus, Clock clock) {
        this.links = links;
        this.members = members;
        this.invitations = invitations;
        this.bus = bus;
        this.clock = clock;
    }

    /** Issues a link and publishes MagicLinkRequested; the email itself is sent by the notification module. */
    public MagicLink requestMagicLink(String email) {
        return null;
    }

    /** Consumes the link, claims any open invitations, and opens a session. */
    public Session verify(String token) {
        return null;
    }

    public void signOut(String sessionId) {
    }
}
