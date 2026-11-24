package com.casos.app;

import com.casos.web.WebServer;
import java.time.Clock;

/** Starts the Casos prototype: builds the system, loads demo data, serves the web UI. */
public class CasosApplication {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String baseUrl = "http://localhost:" + port;

        CasosSystem system = new CasosSystem(Clock.systemUTC(), baseUrl);
        SeedData.load(system);

        WebServer server = new WebServer(system, port);
        server.start();
        System.out.println("Casos prototype running at " + baseUrl);
        System.out.println("Agency demo sign-in: " + SeedData.DEMO_COORDINATOR);
        System.out.println("Press Ctrl+C to stop.");
    }
}
