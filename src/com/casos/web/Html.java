package com.casos.web;

import com.casos.domain.AgeBand;
import com.casos.domain.County;
import com.casos.domain.Credential;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Shared page shell, escaping and display formatting. Views hold no rules (D4, Section 4.3). */
public final class Html {

    private static final ZoneId MIAMI = ZoneId.of("America/New_York");
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.US).withZone(MIAMI);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("MMM d", Locale.US).withZone(MIAMI);

    private Html() {
    }

    public static String esc(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '&' -> out.append("&amp;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    public static String when(Instant i) {
        return WHEN.format(i);
    }

    public static String day(Instant i) {
        return DAY.format(i);
    }

    public static String county(County c) {
        if (c == null) {
            return "";
        }
        return c == County.MIAMI_DADE ? "Miami-Dade" : "Broward";
    }

    public static String credential(Credential c) {
        return switch (c) {
            case RBT -> "RBT";
            case BCABA -> "BCaBA";
            case BCBA -> "BCBA";
        };
    }

    public static String ageBand(AgeBand a) {
        return switch (a) {
            case UNDER_3 -> "Under 3";
            case AGE_3_5 -> "Ages 3 to 5";
            case AGE_6_9 -> "Ages 6 to 9";
            case AGE_10_13 -> "Ages 10 to 13";
            case AGE_14_17 -> "Ages 14 to 17";
            case ADULT -> "Adult";
        };
    }

    public static String title(String enumName) {
        String lower = enumName.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    public static String selected(boolean on) {
        return on ? " selected" : "";
    }

    public static String checked(boolean on) {
        return on ? " checked" : "";
    }

    /** Message banner. kind is "ok", "info" or "error". */
    public static String banner(String kind, String message) {
        if (message == null || message.isBlank()) {
            return "";
        }
        return "<div class=\"banner " + kind + "\" role=\"status\">" + esc(message) + "</div>";
    }

    /** Full page. headerRight is trusted HTML for the context control (sign-in link or agency menu). */
    public static String page(String title, String headerRight, String body) {
        return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<title>" + esc(title) + " | Casos</title><style>" + CSS + "</style></head><body>"
                + "<header class=\"top\"><a class=\"mark\" href=\"/\">Casos<span>South Florida ABA cases</span></a>"
                + "<nav>" + headerRight + "</nav></header><main>" + body + "</main>"
                + "<footer>Casos prototype, CEN 4021 Deliverable 5. Every case is de-identified: no names, "
                + "dates of birth, member IDs or addresses are stored or shown.</footer></body></html>";
    }

    private static final String CSS = """
        :root{--ink:#14202b;--muted:#5b6876;--line:#dde3e9;--bg:#f6f7f9;--card:#fff;--brand:#0f5c74;
          --brand-ink:#fff;--urgent:#c2410c;--urgent-bg:#fff1e8;--ok:#166534;--ok-bg:#ecfdf3;--err:#b42318;
          --err-bg:#fef3f2;--info-bg:#eef6fa;--radius:10px}
        *{box-sizing:border-box}
        body{margin:0;font:15px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Helvetica,Arial,sans-serif;
          color:var(--ink);background:var(--bg)}
        a{color:var(--brand)}
        .top{display:flex;justify-content:space-between;align-items:center;gap:12px;padding:12px 20px;
          background:var(--card);border-bottom:1px solid var(--line);position:sticky;top:0;z-index:5}
        .mark{font-weight:800;font-size:20px;color:var(--ink);text-decoration:none;letter-spacing:-.02em}
        .mark span{display:block;font-weight:500;font-size:12px;color:var(--muted);letter-spacing:0}
        nav{display:flex;gap:10px;align-items:center;flex-wrap:wrap;font-size:14px}
        main{max-width:1120px;margin:0 auto;padding:20px 16px 40px}
        footer{max-width:1120px;margin:0 auto;padding:0 16px 32px;color:var(--muted);font-size:12px}
        h1{font-size:24px;margin:0 0 4px;letter-spacing:-.01em} h2{font-size:18px;margin:24px 0 10px}
        .sub{color:var(--muted);margin:0 0 16px}
        .btn{display:inline-block;border:1px solid var(--brand);background:var(--brand);color:var(--brand-ink);
          padding:8px 14px;border-radius:8px;font:inherit;font-weight:600;cursor:pointer;text-decoration:none}
        .btn.ghost{background:transparent;color:var(--brand)} .btn.small{padding:5px 10px;font-size:13px}
        .btn:focus-visible,input:focus-visible,select:focus-visible,textarea:focus-visible{outline:3px solid #7cc4dc;outline-offset:2px}
        .panel{background:var(--card);border:1px solid var(--line);border-radius:var(--radius);padding:16px;margin-bottom:16px}
        .filters{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px;align-items:end}
        label{display:block;font-size:13px;font-weight:600;color:var(--muted);margin-bottom:4px}
        input[type=text],input[type=email],input[type=tel],input[type=number],select,textarea{width:100%;
          padding:8px 10px;border:1px solid var(--line);border-radius:8px;font:inherit;background:#fff;color:var(--ink)}
        .checks{display:flex;flex-wrap:wrap;gap:6px 14px} .checks label{font-weight:500;color:var(--ink);display:flex;gap:6px;align-items:center;margin:0}
        .counts{display:flex;gap:16px;align-items:baseline;margin:6px 0 14px;flex-wrap:wrap}
        .counts strong{font-size:18px} .urgent-count{color:var(--urgent);font-weight:700}
        .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:14px}
        .card{background:var(--card);border:1px solid var(--line);border-radius:var(--radius);padding:14px;
          text-decoration:none;color:inherit;display:flex;flex-direction:column;gap:8px}
        .card:hover{border-color:var(--brand)}
        .card.cov{border-left:4px solid var(--urgent)}
        .hero{display:flex;gap:18px} .hero div{display:flex;flex-direction:column}
        .hero b{font-size:28px;line-height:1;letter-spacing:-.02em} .hero small{color:var(--muted);font-size:12px}
        .badge{display:inline-block;font-size:12px;font-weight:700;padding:2px 8px;border-radius:999px;background:#eef1f4;color:var(--muted)}
        .badge.cov{background:var(--urgent-bg);color:var(--urgent)} .badge.open{background:var(--ok-bg);color:var(--ok)}
        .badge.filled{background:#eef1f4;color:var(--ink)} .badge.expired{background:var(--err-bg);color:var(--err)}
        .meta{color:var(--muted);font-size:13px} .tags{display:flex;flex-wrap:wrap;gap:6px}
        .tag{font-size:12px;border:1px solid var(--line);border-radius:6px;padding:1px 6px;color:var(--muted)}
        .banner{padding:10px 14px;border-radius:8px;margin-bottom:14px;font-weight:500}
        .banner.ok{background:var(--ok-bg);color:var(--ok)} .banner.error{background:var(--err-bg);color:var(--err)}
        .banner.info{background:var(--info-bg);color:var(--brand)}
        table{width:100%;border-collapse:collapse;background:var(--card);border:1px solid var(--line);border-radius:var(--radius);overflow:hidden}
        th,td{text-align:left;padding:9px 10px;border-bottom:1px solid var(--line);font-size:14px;vertical-align:top}
        th{background:#f1f4f7;font-size:12px;text-transform:uppercase;letter-spacing:.04em;color:var(--muted)}
        .row-actions{display:flex;gap:6px;flex-wrap:wrap} form.inline{display:inline}
        .two{display:grid;grid-template-columns:1fr 1fr;gap:16px} .stat{font-size:26px;font-weight:800}
        .week{border-collapse:collapse;width:auto} .week td,.week th{padding:6px 8px;text-align:center;border:1px solid var(--line)}
        dl{display:grid;grid-template-columns:150px 1fr;gap:6px 12px;margin:0} dt{color:var(--muted);font-size:13px} dd{margin:0}
        .empty{padding:28px;text-align:center;color:var(--muted)}
        .mail{border:1px solid var(--line);border-radius:8px;padding:12px;background:#fff;margin-bottom:10px}
        @media (max-width:640px){.two{grid-template-columns:1fr} dl{grid-template-columns:1fr} .top{padding:10px 16px}
          table{display:block;overflow-x:auto}}
        @media (prefers-reduced-motion:reduce){*{transition:none!important}}
        """;
}
