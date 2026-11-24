package com.casos.identity;

import com.casos.domain.AgencyMember;
import com.casos.domain.DomainEventBus;
import com.casos.domain.MagicLinkRequested;
import com.casos.domain.MemberRepository;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Magic-link request and verification (S9.1, S9.3). No password exists anywhere in the system. */
public class AuthService {

    public static final Duration LINK_TTL = Duration.ofMinutes(15);

    private final MagicLinkRepository links;
    private final MemberRepository members;
    private final InvitationService invitations;
    private final DomainEventBus bus;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

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
        String normalized = email == null ? "" : email.trim().toLowerCase();
        if (!normalized.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        MagicLink link = new MagicLink(token, normalized, clock.instant(), LINK_TTL);
        links.save(link);
        bus.publish(new MagicLinkRequested(clock.instant(), normalized, token));
        return link;
    }

    /** Consumes the link, claims any open invitations, and opens a session. */
    public Session verify(String token) {
        MagicLink link = links.findByToken(token == null ? "" : token)
                .orElseThrow(() -> new InvalidLinkException("This sign-in link is not valid."));
        if (!link.isValid(clock.instant())) {
            throw new InvalidLinkException("This sign-in link has expired or was already used. Request a new one.");
        }
        link.consume();
        invitations.claimOnSignIn(link.email());
        List<AgencyMember> memberships = members.findByEmail(link.email());
        Optional<AgencyMember> first = memberships.stream().findFirst();
        return new Session(UUID.randomUUID().toString(), link.email(),
                first.map(AgencyMember::memberId), first.map(AgencyMember::agencyId), clock.instant());
    }

    public void signOut(String sessionId) {
        // Sessions live in the web layer's session table; removing the cookie entry ends the session.
    }
}
