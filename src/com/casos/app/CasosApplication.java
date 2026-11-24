package com.casos.app;

import com.casos.analytics.AnalyticsSubscriber;
import com.casos.analytics.AppendOnlyEventStore;
import com.casos.analytics.AttributionValidator;
import com.casos.analytics.EventStore;
import com.casos.board.BoardController;
import com.casos.board.BoardService;
import com.casos.board.InterestService;
import com.casos.clinician.AvailabilityQueryAdapter;
import com.casos.clinician.InMemoryClinicianRepository;
import com.casos.compliance.ControlledVocabulary;
import com.casos.compliance.FreeTextGuard;
import com.casos.compliance.StandardDeidentificationPolicy;
import com.casos.domain.CaseRepository;
import com.casos.domain.ClinicianRepository;
import com.casos.domain.DomainEventBus;
import com.casos.geo.PinJitter;
import com.casos.geo.ZipCentroidTable;
import com.casos.geo.ZipResolver;
import com.casos.lifecycle.CaseFactory;
import com.casos.lifecycle.CaseNumberSequence;
import com.casos.lifecycle.ExpiryJob;
import com.casos.lifecycle.InMemoryCaseRepository;
import com.casos.lifecycle.OneClickTokenService;
import com.casos.notification.NotificationHandlers;
import com.casos.notification.ResendEmailSender;
import com.casos.notification.TemplateRenderer;
import java.time.Clock;
import java.util.Map;

/** Composition root. The only class that knows every module; everything else talks through domain ports. */
public class CasosApplication {

    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        DomainEventBus bus = new SynchronousEventBus();

        CaseRepository cases = new InMemoryCaseRepository();
        ClinicianRepository clinicians = new InMemoryClinicianRepository();

        ZipResolver geo = new ZipResolver(new ZipCentroidTable(Map.of()));
        StandardDeidentificationPolicy policy =
                new StandardDeidentificationPolicy(new ControlledVocabulary(Map.of(), Map.of()), new FreeTextGuard());
        CaseFactory factory = new CaseFactory(policy, geo, new PinJitter(), new CaseNumberSequence(), clock);
        OneClickTokenService tokens = new OneClickTokenService(clock);
        ExpiryJob expiryJob = new ExpiryJob(cases, tokens, bus, clock);

        BoardService board = new BoardService(cases, new AvailabilityQueryAdapter(clinicians), bus, clock);
        InterestService interests = new InterestService(cases, null, bus, clock);
        BoardController controller = new BoardController(board, interests);

        EventStore store = new AppendOnlyEventStore();
        new AnalyticsSubscriber(store, new AttributionValidator(cases)).register(bus);

        NotificationHandlers handlers = new NotificationHandlers(
                new ResendEmailSender("", "no-reply@casos.app"),
                new TemplateRenderer("https://casos.app"), null, clinicians);
        handlers.register(bus);
    }
}
