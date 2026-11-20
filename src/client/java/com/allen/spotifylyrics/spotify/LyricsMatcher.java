
package com.allen.spotifylyrics.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LyricsMatcher {

    private static final double MINIMUM_SCORE = 0.80;

    public JsonObject findBestMatch(
            Map<String, JsonObject> candidates,
            List<String> titleVariations,
            String artist,
            double duration
    ) {
        JsonObject bestMatch = null;
        double bestScore = 0;

        for (JsonObject track : candidates.values()) {

            // Only consider tracks with synchronized lyrics
            if (!hasSyncedLyrics(track)) {
                continue;
            }

            String trackTitle = getString(track, "trackName");
            String trackArtist = getString(track, "artistName");

            double trackDuration = track.has("duration")
                    && !track.get("duration").isJsonNull()
                    ? track.get("duration").getAsDouble()
                    : 0;

            // Compare against every shortened title variation
            double titleScore = 0;

            for (String variation : titleVariations) {
                titleScore = Math.max(
                        titleScore,
                        similarity(variation, trackTitle)
                );
            }

            double artistScore = similarity(artist, trackArtist);
            double durationScore = durationSimilarity(duration, trackDuration);

            double score;

            if (artist == null || artist.isBlank()) {
                score = titleScore * 0.75 + durationScore * 0.25;
            } else {
                score = titleScore * 0.50
                        + artistScore * 0.30
                        + durationScore * 0.20;
            }

            System.out.println(
                    "Candidate: " + trackTitle + " - " + trackArtist
                            + " | Score: " + String.format("%.2f", score * 100) + "%"
            );

            if (score > bestScore) {
                bestScore = score;
                bestMatch = track;
            }
        }

        if (bestMatch != null && bestScore >= MINIMUM_SCORE) {
            System.out.println("Fuzzy match found: "
                    + getString(bestMatch, "trackName")
                    + " - " + getString(bestMatch, "artistName"));

            System.out.println("Match score: "
                    + String.format("%.2f", bestScore * 100) + "%");

            return bestMatch;
        }

        System.out.println("No reliable match. Best score: "
                + String.format("%.2f", bestScore * 100) + "%");

        return null;
    }

    public void addResults(
            Map<String, JsonObject> candidates,
            JsonArray results
    ) {
        for (var item : results) {
            JsonObject track = item.getAsJsonObject();
            String id = getString(track, "id");

            if (!id.isBlank()) {
                candidates.putIfAbsent(id, track);
            }
        }
    }

    private double similarity(String first, String second) {

        String a = normalize(first);
        String b = normalize(second);

        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }

        if (a.equals(b)) {
            return 1.0;
        }

        int distance = levenshtein(a, b);
        int maxLength = Math.max(a.length(), b.length());

        return 1.0 - ((double) distance / maxLength);
    }

    private String normalize(String value) {
        if (value == null) return "";

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }

    private int levenshtein(String a, String b) {

        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {

                int cost = a.charAt(i - 1) == b.charAt(j - 1)
                        ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        ),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[a.length()][b.length()];
    }

    private double durationSimilarity(double actual, double candidate) {

        if (actual <= 0 || candidate <= 0) return 0.5;

        double difference = Math.abs(actual - candidate);

        if (difference <= 3) return 1.0;
        if (difference <= 8) return 0.8;
        if (difference <= 15) return 0.5;

        return 0.0;
    }

    private String getString(JsonObject object, String key) {
        if (object == null || !object.has(key)
                || object.get(key).isJsonNull()) {
            return "";
        }

        return object.get(key).getAsString();
    }

    private boolean hasSyncedLyrics(JsonObject result) {
        return result != null
                && result.has("syncedLyrics")
                && !result.get("syncedLyrics").isJsonNull()
                && !result.get("syncedLyrics").getAsString().isBlank();
    }
}
