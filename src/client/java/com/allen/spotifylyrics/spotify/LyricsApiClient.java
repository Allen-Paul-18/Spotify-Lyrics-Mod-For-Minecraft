
package com.allen.spotifylyrics.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LyricsApiClient {

    private static final String API =
            "https://lrclib.net/api/get";

    private static final String SEARCH_API =
            "https://lrclib.net/api/search";

    private final HttpClient client = HttpClient.newHttpClient();

    public JsonObject getExact(
            String title,
            String artist,
            String album,
            double duration
    ) throws Exception {

        String url = API
                + "?track_name=" + encode(title)
                + "&artist_name=" + encode(artist)
                + "&album_name=" + encode(album)
                + "&duration=" + Math.round(duration);

        HttpResponse<String> response = send(url);

        if (response.statusCode() == 404) {
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "LRCLIB error: " + response.statusCode()
            );
        }

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public JsonArray search(
            String title,
            String artist
    ) throws Exception {

        String url = SEARCH_API
                + "?track_name=" + encode(title);

        if (artist != null && !artist.isBlank()) {
            url += "&artist_name=" + encode(artist);
        }

        HttpResponse<String> response = send(url);

        if (response.statusCode() != 200) {
            System.out.println(
                    "LRCLIB search failed: " + response.statusCode()
            );

            return new JsonArray();
        }

        return JsonParser.parseString(response.body()).getAsJsonArray();
    }

    private HttpResponse<String> send(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(
                        "User-Agent",
                        "UniversalLyricsMinecraftMod/1.0"
                )
                .GET()
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private String encode(String value) {

        return URLEncoder.encode(
                value == null ? "" : value,
                StandardCharsets.UTF_8
        );
    }
}
