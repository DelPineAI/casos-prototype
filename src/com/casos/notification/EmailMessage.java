package com.casos.notification;

import java.util.List;

/** A rendered email ready for an EmailSender. */
public final class EmailMessage {

    private final List<String> to;
    private final String subject;
    private final String htmlBody;
    private final String textBody;

    public EmailMessage(List<String> to, String subject, String htmlBody, String textBody) {
        this.to = to;
        this.subject = subject;
        this.htmlBody = htmlBody;
        this.textBody = textBody;
    }

    public List<String> to() {
        return to;
    }

    public String subject() {
        return subject;
    }

    public String htmlBody() {
        return htmlBody;
    }

    public String textBody() {
        return textBody;
    }
}
