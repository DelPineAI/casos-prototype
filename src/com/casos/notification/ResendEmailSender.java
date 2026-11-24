package com.casos.notification;

/** EmailSender backed by the Resend API. */
public class ResendEmailSender implements EmailSender {

    private final String apiKey;
    private final String fromAddress;

    public ResendEmailSender(String apiKey, String fromAddress) {
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
    }

    @Override
    public void send(EmailMessage m) {
    }
}
