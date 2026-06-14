package com.example.Millesime.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS = 5;
    private static final int BURST = 10;
    private static final long WINDOW_MS = 1000;
    private static final long CLEANUP_INTERVAL_MS = 60000;
    private static final long ENTRY_TTL_MS = 300000;

    private final Map<String, SlidingWindow> windows = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RateLimitingFilter() {
        scheduler.scheduleAtFixedRate(this::cleanup, CLEANUP_INTERVAL_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();

        if (!"POST".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        boolean matches = path.equals("/login") || path.equals("/register")
                || path.equals("/reset-password") || path.startsWith("/api/");
        if (!matches) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.getSession(false) != null
                && httpRequest.getUserPrincipal() != null
                && httpRequest.isUserInRole("ADMIN")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }

        SlidingWindow window = windows.compute(ip, (key, existing) -> {
            SlidingWindow w = existing;
            if (w == null || System.currentTimeMillis() - w.start > WINDOW_MS + ENTRY_TTL_MS) {
                w = new SlidingWindow();
            }
            w.count++;
            return w;
        });

        if (window.count > BURST) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json; charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"Muitas requisições\",\"retryAfter\":1}");
            return;
        }

        if (window.count > MAX_REQUESTS) {
            long elapsed = System.currentTimeMillis() - window.start;
            if (elapsed < WINDOW_MS) {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json; charset=UTF-8");
                httpResponse.getWriter().write("{\"error\":\"Muitas requisições\",\"retryAfter\":1}");
                return;
            }
            windows.put(ip, new SlidingWindow());
        }

        chain.doFilter(request, response);
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        windows.values().removeIf(w -> now - w.start > ENTRY_TTL_MS);
    }

    private static class SlidingWindow {
        final long start;
        int count;

        SlidingWindow() {
            this.start = System.currentTimeMillis();
            this.count = 1;
        }
    }
}
