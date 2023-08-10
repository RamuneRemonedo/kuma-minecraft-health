package net.ramune.kumaminecrafthealth;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HealthHttpServer {

    private static HttpServer server = null;

    public static void main(String[] args) throws IOException {
        open("0.0.0.0", 3000);
    }

    public static void open(String host, int port) throws IOException {
        if (server != null) close();

        server = HttpServer.create(new InetSocketAddress(host, port), 0);

        server.createContext("/health", exchange -> {
            final Gson gson = new Gson();
            final HealthStatus healthStatus = new HealthStatus(Bukkit.getTPS()[0], Bukkit.getOnlinePlayers().size());
            final String response = gson.toJson(healthStatus);
            exchange.sendResponseHeaders(200, response.length());

            final OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes(StandardCharsets.UTF_8));
            responseBody.close();
            exchange.close();
        });

        server.start();
    }

    public static void close() {
        server.stop(0);
        server = null;
    }
}
