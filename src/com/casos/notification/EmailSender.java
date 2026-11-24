package com.casos.notification;

/** Abstraction over the outbound email provider. */
public interface EmailSender {

    void send(EmailMessage m);
}
