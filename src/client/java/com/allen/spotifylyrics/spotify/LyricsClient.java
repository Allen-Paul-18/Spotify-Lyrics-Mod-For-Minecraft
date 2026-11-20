
package com.allen.spotifylyrics.spotify;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LyricsClient {

    private final LyricsApiClient apiClient =
            new LyricsApiClient();

    private final SongMetadataParser metadataParser =
            new SongMetadataParser();

    private final LyricsMatcher matcher =
            new LyricsMatcher();

    public JsonObject getLyrics(
            String title,
            String artist,
            String album,
            double duration
    ) throws Exception {

        System.out.println(
                "Original: " + title + " - " + artist
        );

        // 1. Exact matching
        try {

            JsonObject exact = apiClient.getExact(
                    title,
                    artist,
                    album,
                    duration
            );

            if (hasSyncedLyrics(exact)) {
                System.out.println("Exact match found!");
                return exact;
            }

        } catch (Exception e) {
            System.out.println(
                    "Exact search failed: " + e.getMessage()
            );
        }

        // 2. Parse metadata
        SongMetadataParser.Metadata metadata =
                metadataParser.parse(title, artist);

        String songTitle = metadata.title();
        String songArtist = metadata.artist();

        System.out.println("Detected song: " + songTitle);
        System.out.println("Detected artist: " + songArtist);

        // 3. Search multiple title variations
        Map<String, JsonObject> candidates =
                new LinkedHashMap<>();

        List<String> variations =
                metadataParser.getTitleVariations(songTitle);

        for (String variation : variations) {

            System.out.println("Trying title: " + variation);

            matcher.addResults(
                    candidates,
                    apiClient.search(variation, songArtist)
            );

            matcher.addResults(
                    candidates,
                    apiClient.search(variation, "")
            );
        }

        if (candidates.isEmpty()) {
            System.out.println("No LRCLIB search results.");
            return null;
        }

        // 4. Find the best fuzzy match
        return matcher.findBestMatch(candidates, variations, songArtist, duration);
    }

    private boolean hasSyncedLyrics(JsonObject result) {

        return result != null
                && result.has("syncedLyrics")
                && !result.get("syncedLyrics").isJsonNull()
                && !result.get("syncedLyrics").getAsString().isBlank();
    }
}
