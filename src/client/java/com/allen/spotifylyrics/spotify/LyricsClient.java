package com.allen.spotifylyrics.spotify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LyricsClient {

    private static final String API =
            "https://lrclib.net/api/get";

    private final HttpClient client = HttpClient.newHttpClient();


    public JsonObject getLyrics(
            String title,
            String artist,
            String album,
            double duration
    ) throws Exception {

        // First: Exact match
        JsonObject exact = requestObject(
                API
                        + "?track_name=" + encode(title)
                        + "&artist_name=" + encode(artist)
                        + "&album_name=" + encode(album)
                        + "&duration=" + Math.round(duration)
        );

        if (hasSyncedLyrics(exact)) {
            System.out.println("Found synchronized lyrics!");
            return exact;
        }

        System.out.println("Trying alternative matches...");

        // Second: Search alternative records
        String searchUrl = "https://lrclib.net/api/search"
                + "?track_name=" + encode(title)
                + "&artist_name=" + encode(artist);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(searchUrl))
                .header("User-Agent", "SpotifyLyricsMinecraftMod/1.0")
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println("Search failed: " + response.statusCode());
            return exact;
        }

        var results = JsonParser.parseString(
                response.body()
        ).getAsJsonArray();

        JsonObject bestPlain = exact;

        for (var item : results) {

            JsonObject track = item.getAsJsonObject();

            if (track.get("artistName").getAsString()
                    .equalsIgnoreCase(artist)
                    && Math.abs(
                    track.get("duration").getAsDouble() - duration
            ) <= 5) {

                if (hasSyncedLyrics(track)) {
                    System.out.println("Found alternative synced lyrics!");
                    return track;
                }

                if (bestPlain == null
                        && !track.get("plainLyrics").isJsonNull()) {
                    bestPlain = track;
                }
            }
        }

        return bestPlain;
    }

    private boolean hasSyncedLyrics(JsonObject result) {
        return result != null
                && result.has("syncedLyrics")
                && !result.get("syncedLyrics").isJsonNull()
                && !result.get("syncedLyrics").getAsString().isBlank();
    }

    private JsonObject requestObject(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "SpotifyLyricsMinecraftMod/1.0")
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 404) {
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "LRCLIB error: " + response.statusCode()
            );
        }

        return JsonParser.parseString(
                response.body()
        ).getAsJsonObject();
    }

    private String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}

