package com.casos.app;

import com.casos.analytics.AnalyticsSubscriber;
import com.casos.analytics.AppendOnlyEventStore;
import com.casos.analytics.AttributionValidator;
import com.casos.analytics.ReportService;
import com.casos.board.BoardService;
import com.casos.board.InterestService;
import com.casos.clinician.AvailabilityQueryAdapter;
import com.casos.clinician.InMemoryClinicianRepository;
import com.casos.compliance.ControlledVocabulary;
import com.casos.compliance.FreeTextGuard;
import com.casos.compliance.StandardDeidentificationPolicy;
import com.casos.geo.PinJitter;
import com.casos.geo.ZipCentroidTable;
import com.casos.geo.ZipResolver;
import com.casos.identity.AuthService;
import com.casos.identity.AuthorizationPolicy;
import com.casos.identity.InMemoryInvitationRepository;
import com.casos.identity.InMemoryMagicLinkRepository;
import com.casos.identity.InMemoryMemberRepository;
import com.casos.identity.InvitationService;
import com.casos.lifecycle.CaseFactory;
import com.casos.lifecycle.CaseNumberSequence;
import com.casos.lifecycle.CaseService;
import com.casos.lifecycle.ExpiryJob;
import com.casos.lifecycle.InMemoryAgencyRepository;
import com.casos.lifecycle.InMemoryCaseRepository;
import com.casos.lifecycle.InMemoryInterestRepository;
import com.casos.lifecycle.InterestInboxService;
import com.casos.lifecycle.OneClickTokenService;
import com.casos.notification.DevMailboxSender;
import com.casos.notification.NotificationHandlers;
import com.casos.notification.TemplateRenderer;
import java.time.Clock;

/**
 * Composition root. The only class that knows every concrete type; every service receives its
 * collaborators through the domain ports (Assignment 2, Section 6.2). Shared by the web server and the tests.
 */
public final class CasosSystem {

    public final Clock clock;
    public final SynchronousEventBus bus = new SynchronousEventBus();

    // Data (in-memory adapters behind the domain repository ports)
    public final InMemoryCaseRepository cases = new InMemoryCaseRepository();
    public final InMemoryAgencyRepository agencies = new InMemoryAgencyRepository();
    public final InMemoryInterestRepository interests = new InMemoryInterestRepository();
    public final InMemoryMemberRepository members = new InMemoryMemberRepository();
    public final InMemoryInvitationRepository invitationStore = new InMemoryInvitationRepository();
    public final InMemoryMagicLinkRepository links = new InMemoryMagicLinkRepository();
    public final InMemoryClinicianRepository clinicians = new InMemoryClinicianRepository();
    public final AppendOnlyEventStore eventStore = new AppendOnlyEventStore();

    // M9 and M6
    public final ControlledVocabulary vocabulary = ControlledVocabulary.standard();
    public final ZipCentroidTable zipTable = ZipCentroidTable.southFlorida();
    public final ZipResolver geo = new ZipResolver(zipTable);
    public final PinJitter jitter = new PinJitter();

    // M3
    public final AuthorizationPolicy authz = new AuthorizationPolicy(members);
    public final InvitationService invitations;
    public final AuthService auth;

    // M2, M1
    public final CaseService caseService;
    public final InterestInboxService inbox;
    public final ExpiryJob expiryJob;
    public final BoardService board;
    public final InterestService interestService;

    // M7, M8
    public final DevMailboxSender outbox;
    public final ReportService reports;

    public CasosSystem(Clock clock, String baseUrl) {
        this.clock = clock;
        StandardDeidentificationPolicy policy = new StandardDeidentificationPolicy(vocabulary, new FreeTextGuard());
        CaseFactory factory = new CaseFactory(policy, geo, jitter, new CaseNumberSequence(), clock);
        OneClickTokenService tokens = new OneClickTokenService(clock);

        this.invitations = new InvitationService(invitationStore, members, clock);
        this.auth = new AuthService(links, members, invitations, bus, clock);
        this.caseService = new CaseService(cases, factory, agencies, authz, tokens, bus, clock);
        this.inbox = new InterestInboxService(interests, authz);
        this.expiryJob = new ExpiryJob(cases, tokens, bus, clock);
        this.board = new BoardService(cases, new AvailabilityQueryAdapter(clinicians), bus, clock);
        this.interestService = new InterestService(cases, interests, bus, clock);
        this.reports = new ReportService(eventStore, authz, cases, clock);

        // Subscribers react to events; nothing calls notification or analytics directly.
        this.outbox = new DevMailboxSender(clock);
        new NotificationHandlers(outbox, new TemplateRenderer(baseUrl), members, clinicians).register(bus);
        new AnalyticsSubscriber(eventStore, new AttributionValidator(cases)).register(bus);
    }
}
