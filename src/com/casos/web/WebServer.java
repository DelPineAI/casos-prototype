package com.casos.web;

import com.casos.app.CasosSystem;
import com.casos.identity.Session;
import com.casos.identity.UnauthorizedException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Presentation layer. Routes each request to a page, translates user intent into a service call, and
 * maps every failure to a plain-language message (D4: errors are never rendered verbatim).
 */
public final class WebServer {

    static final String SESSION_COOKIE = "casos_session";
    static final String VISITOR_COOKIE = "casos_visitor";

    private final CasosSystem system;
    private final HttpServer server;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final BoardPages boardPages;
    private final AgencyPages agencyPages;
    private final AuthPages authPages;

    public WebServer(CasosSystem system, int port) throws IOException {
        this.system = system;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newCachedThreadPool());
        this.boardPages = new BoardPages(system);
        this.agencyPages = new AgencyPages(system);
        this.authPages = new AuthPages(system, sessions);
        this.server.createContext("/", this::handle);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int port() {
        return server.getAddress().getPort();
    }

    private void handle(HttpExchange ex) throws IOException {
        try {
            Request req = Request.from(ex);
            Reply reply = route(req, ex);
            reply.send(ex);
        } catch (Exception e) {
            // Last-resort catalogue entry: never expose the exception text.
            new Reply(500, Html.page("Something went wrong", "",
                    Html.banner("error", "Something went wrong on our side. Go back and try again.")
                            + "<p><a href=\"/\">Back to the board</a></p>")).send(ex);
        }
    }

    private Reply route(Request req, HttpExchange ex) {
        String visitor = req.cookie(VISITOR_COOKIE).orElse(null);
        String newVisitor = null;
        if (visitor == null) {
            visitor = UUID.randomUUID().toString();
            newVisitor = visitor;
        }
        Optional<Session> session = req.cookie(SESSION_COOKIE).map(sessions::get);
        String header = header(session);

        Reply reply;
        try {
            reply = switch (req.path()) {
                case "/" -> boardPages.board(req, visitor, header);
                case "/case" -> boardPages.detail(req, visitor, header);
                case "/interest" -> req.isPost() ? boardPages.submitInterest(req, visitor, header) : Reply.redirect("/");
                case "/signin" -> req.isPost() ? authPages.requestLink(req, header) : authPages.signInForm(req, header);
                case "/auth" -> authPages.verify(req, header);
                case "/signout" -> authPages.signOut(req);
                case "/dev/outbox" -> authPages.outbox(header);
                case "/agency" -> agencyPages.dashboard(req, require(session), header);
                case "/agency/post" -> req.isPost()
                        ? agencyPages.submitCase(req, require(session), header)
                        : agencyPages.postForm(req, require(session), header, null, null);
                case "/agency/fill" -> agencyPages.fill(req, require(session));
                case "/agency/renew" -> agencyPages.renew(req, require(session));
                case "/agency/inbox" -> agencyPages.inbox(req, require(session), header);
                case "/agency/inbox/advance" -> agencyPages.advance(req, require(session));
                case "/agency/report.csv" -> agencyPages.reportCsv(require(session));
                default -> new Reply(404, Html.page("Not found", header,
                        Html.banner("info", "That page does not exist.") + "<p><a href=\"/\">Back to the board</a></p>"));
            };
        } catch (NotSignedIn e) {
            reply = Reply.redirect("/signin?next=" + URLEncoder.encode(req.path(), StandardCharsets.UTF_8));
        } catch (UnauthorizedException e) {
            reply = new Reply(403, Html.page("Not allowed", header, Html.banner("error", e.getMessage())
                    + "<p><a href=\"/agency\">Back to your dashboard</a></p>"));
        }
        if (newVisitor != null) {
            reply.cookie(VISITOR_COOKIE, newVisitor);
        }
        return reply;
    }

    private Session require(Optional<Session> session) {
        return session.orElseThrow(NotSignedIn::new);
    }

    private String header(Optional<Session> session) {
        if (session.isEmpty()) {
            return "<a href=\"/\">Board</a><a class=\"btn ghost small\" href=\"/signin\">Agency sign in</a>";
        }
        String agency = session.get().activeAgencyId()
                .flatMap(system.agencies::findById).map(a -> a.name()).orElse("No agency");
        return "<a href=\"/\">Board</a><a href=\"/agency\">Dashboard</a><a href=\"/agency/inbox\">Inbox</a>"
                + "<a class=\"btn small\" href=\"/agency/post\">Post a case</a>"
                + "<span class=\"meta\">" + Html.esc(agency) + "</span><a href=\"/signout\">Sign out</a>";
    }

    static final class NotSignedIn extends RuntimeException {
    }

    /** An HTTP response: status, body, headers. */
    static final class Reply {
        private final int status;
        private final String body;
        private final String contentType;
        private final Map<String, String> headers = new java.util.LinkedHashMap<>();
        private final java.util.List<String> cookies = new java.util.ArrayList<>();

        Reply(int status, String html) {
            this(status, html, "text/html; charset=utf-8");
        }

        Reply(int status, String body, String contentType) {
            this.status = status;
            this.body = body;
            this.contentType = contentType;
        }

        static Reply ok(String html) {
            return new Reply(200, html);
        }

        static Reply redirect(String location) {
            Reply r = new Reply(303, "");
            r.headers.put("Location", location);
            return r;
        }

        Reply header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        Reply cookie(String name, String value) {
            cookies.add(name + "=" + value + "; Path=/; HttpOnly; SameSite=Lax");
            return this;
        }

        Reply clearCookie(String name) {
            cookies.add(name + "=; Path=/; Max-Age=0");
            return this;
        }

        void send(HttpExchange ex) throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", contentType);
            ex.getResponseHeaders().set("Cache-Control", "no-store");
            headers.forEach((k, v) -> ex.getResponseHeaders().set(k, v));
            cookies.forEach(c -> ex.getResponseHeaders().add("Set-Cookie", c));
            ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
            if (bytes.length > 0) {
                try (OutputStream out = ex.getResponseBody()) {
                    out.write(bytes);
                }
            }
            ex.close();
        }
    }
}
