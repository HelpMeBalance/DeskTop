package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class HomeController {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private JsonArray quotesArray;

    public void initialize() {
        fetchQuotes();
        scheduleNotifications();
    }

    private void fetchQuotes() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://type.fit/api/quotes"))
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseQuotes)
                .join();
    }

    private JsonArray parseQuotes(String responseBody) {
        quotesArray = JsonParser.parseString(responseBody).getAsJsonArray();
        return quotesArray;
    }

    private void scheduleNotifications() {
        Runnable notifier = () -> {
            if (quotesArray != null && quotesArray.size() > 0) {
                Random random = new Random();
                var randomQuote = quotesArray.get(random.nextInt(quotesArray.size())).getAsJsonObject();
                String text = randomQuote.get("text").getAsString();
                String author = randomQuote.has("author") ? "fares" : "fares";
                Platform.runLater(() -> {
                    Notifications.create()
                            .title("Quote of the Moment")
                            .text(text + "\n~ " + author)
                            .hideAfter(Duration.seconds(4))
                            .showInformation();
                });
            }
        };
        scheduler.scheduleAtFixedRate(notifier, 0, 25, TimeUnit.SECONDS);
    }

    @FXML
    private void stopNotifications(ActionEvent event) {
        scheduler.shutdownNow();
    }
}
