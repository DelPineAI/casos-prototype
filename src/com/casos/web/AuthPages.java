package com.casos.web;

import static com.casos.web.Html.esc;

import com.casos.app.CasosSystem;
import com.casos.app.SeedData;
import com.casos.identity.InvalidLinkException;
import com.casos.identity.Session;
import com.casos.notification.DevMailboxSender;
import com.casos.web.WebServer.Reply;
import java.util.List;
import java.util.Map;

/** SCR-05 Sign In, the magic-link landing, sign out, and the prototype's development outbox. */
final class AuthPages {

    private final CasosSystem system;
    private final Map<String, Session> sessions;

    AuthPages(CasosSystem system, Map<String, Session> sessions) {
        this.system = system;
        this.sessions = sessions;
    }

    Reply signInForm(Request req, String header) {
        return Reply.ok(Html.page("Agency sign in", header, form(req.get("email"), null)));
    }

    private String form(String email, String error) {
        return "<section class=\"panel\" style=\"max-width:460px;margin:24px auto\"><h1>Agency sign in</h1>"
                + "<p class=\"sub\">Enter your work email. We'll send a one-time sign-in link. Casos has no passwords.</p>"
                + (error == null ? "" : Html.banner("error", error))
                + "<form method=\"post\" action=\"/signin\"><p><label for=\"email\">Work email</label>"
                + "<input id=\"email\" name=\"email\" type=\"email\" required value=\"" + esc(email) + "\"></p>"
                + "<button class=\"btn\" type=\"submit\">Email me a sign-in link</button></form>"
                + "<p class=\"meta\" style=\"margin-top:14px\">Demo: <code>" + SeedData.DEMO_COORDINATOR
                + "</code> has a pending invitation to Sunrise Behavioral Health.</p></section>";
    }

    Reply requestLink(Request req, String header) {
        try {
            system.auth.requestMagicLink(req.get("email"));
        } catch (IllegalArgumentException e) {
            return Reply.ok(Html.page("Agency sign in", header, form(req.get("email"), e.getMessage())));
        }
        return Reply.ok(Html.page("Check your email", header,
                "<section class=\"panel\" style=\"max-width:520px;margin:24px auto\"><h1>Check your email</h1>"
                        + "<p>We sent a sign-in link to <strong>" + esc(req.get("email").toLowerCase()) + "</strong>. "
                        + "It works once and expires in 15 minutes.</p>"
                        + Html.banner("info", "Prototype: email is delivered to the development outbox instead of a real inbox.")
                        + "<a class=\"btn\" href=\"/dev/outbox\">Open the outbox</a></section>"));
    }

    Reply verify(Request req, String header) {
        Session session;
        try {
            session = system.auth.verify(req.get("token"));
        } catch (InvalidLinkException e) {
            return new Reply(400, Html.page("Link not valid", header, Html.banner("error", e.getMessage())
                    + "<p><a class=\"btn\" href=\"/signin\">Request a new link</a></p>"));
        }
        if (session.isAnonymous()) {
            return new Reply(403, Html.page("No agency access", header, Html.banner("info",
                    "You're signed in as " + session.email() + ", but no agency has invited this address. "
                            + "Ask an agency owner to invite you.") + "<p><a href=\"/\">Back to the board</a></p>"));
        }
        sessions.put(session.sessionId(), session);
        return Reply.redirect("/agency?msg=" + java.net.URLEncoder.encode("Signed in as " + session.email(),
                java.nio.charset.StandardCharsets.UTF_8)).cookie(WebServer.SESSION_COOKIE, session.sessionId());
    }

    Reply signOut(Request req) {
        req.cookie(WebServer.SESSION_COOKIE).ifPresent(id -> {
            sessions.remove(id);
            system.auth.signOut(id);
        });
        return Reply.redirect("/").clearCookie(WebServer.SESSION_COOKIE);
    }

    Reply outbox(String header) {
        List<DevMailboxSender.Delivered> mail = system.outbox.outbox();
        StringBuilder b = new StringBuilder("<h1>Development outbox</h1><p class=\"sub\">Emails the system would send. "
                + "In production these go through the transactional email provider.</p>");
        if (mail.isEmpty()) {
            b.append("<div class=\"panel empty\">No emails yet. Request a sign-in link first.</div>");
        }
        for (DevMailboxSender.Delivered d : mail) {
            b.append("<div class=\"mail\"><div class=\"meta\">").append(Html.when(d.at())).append(" | to ")
             .append(esc(String.join(", ", d.message().to()))).append("</div><strong>")
             .append(esc(d.message().subject())).append("</strong>")
             // The body is produced by TemplateRenderer from trusted templates.
             .append(d.message().htmlBody()).append("</div>");
        }
        return Reply.ok(Html.page("Outbox", header, b.toString()));
    }
}
