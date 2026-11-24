package com.casos.notification;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Prototype EmailSender that keeps messages in an outbox page instead of sending them,
 * so the magic-link flow can be demonstrated without a mail provider account.
 * Production wiring swaps in ResendEmailSender through the same EmailSender interface.
 */
public class DevMailboxSender implements EmailSender {

    public record Delivered(Instant at, EmailMessage message) {
    }

    private final List<Delivered> outbox = Collections.synchronizedList(new ArrayList<>());
    private final Clock clock;

    public DevMailboxSender(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void send(EmailMessage m) {
        outbox.add(0, new Delivered(clock.instant(), m));
    }

    public List<Delivered> outbox() {
        synchronized (outbox) {
            return List.copyOf(outbox);
        }
    }
}
