package com.casos.notification;

import com.casos.domain.ApplicationDecided;
import com.casos.domain.CaseExpiringSoon;
import com.casos.domain.CasePosted;
import com.casos.domain.ClinicianRepository;
import com.casos.domain.DomainEventBus;
import com.casos.domain.InServiceExpiring;
import com.casos.domain.MagicLinkRequested;
import com.casos.domain.MemberRepository;

/** One typed handler per event. Registered on the bus by the composition root (Round 4, W3). */
public class NotificationHandlers {

    private final EmailSender sender;
    private final TemplateRenderer templates;
    private final MemberRepository members;
    private final ClinicianRepository clinicians;

    public NotificationHandlers(EmailSender sender, TemplateRenderer templates,
                                MemberRepository members, ClinicianRepository clinicians) {
        this.sender = sender;
        this.templates = templates;
        this.members = members;
        this.clinicians = clinicians;
    }

    public void register(DomainEventBus bus) {
        bus.subscribe(MagicLinkRequested.class, this::onMagicLink);
        bus.subscribe(CaseExpiringSoon.class, this::onExpiringSoon);
        bus.subscribe(CasePosted.class, this::onCasePosted);
        bus.subscribe(ApplicationDecided.class, this::onApplicationDecided);
        bus.subscribe(InServiceExpiring.class, this::onInServiceExpiring);
    }

    public void onMagicLink(MagicLinkRequested e) {
        sender.send(templates.magicLink(e.email(), e.token()));
    }

    /** S11.5: nudge every member of the agency with one-click links. */
    public void onExpiringSoon(CaseExpiringSoon e) {
    }

    /** S8.2: county alert to subscribed clinicians. */
    public void onCasePosted(CasePosted e) {
        clinicians.subscribedTo(e.county()).forEach(c ->
                sender.send(templates.newCaseInCounty(c.email(), e.county(), 0)));
    }

    /** S8.4. */
    public void onApplicationDecided(ApplicationDecided e) {
    }

    /** S5.3. */
    public void onInServiceExpiring(InServiceExpiring e) {
    }
}
