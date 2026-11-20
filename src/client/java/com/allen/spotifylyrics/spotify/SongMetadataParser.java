
package com.allen.spotifylyrics.spotify;

import java.util.ArrayList;
import java.util.List;

public class SongMetadataParser {

    public record Metadata(String title, String artist) {}

    public Metadata parse(String title, String artist) {

        String cleanTitle = clean(title);
        String cleanArtist = clean(artist);

        // Extract artist from "Artist - Song" format
        String[] parts = cleanTitle.split("\\s+-\\s+", 2);

        if (parts.length == 2) {
            return new Metadata(parts[1].trim(), parts[0].trim());
        }

        return new Metadata(cleanTitle, cleanArtist);
    }

    public List<String> getTitleVariations(String title) {

        List<String> variations = new ArrayList<>();

        String[] words = title.trim().split("\\s+");

        // Try full title and progressively shorter prefixes
        for (int length = words.length; length >= Math.min(3, words.length); length--) {

            String variation = String.join(
                    " ",
                    java.util.Arrays.copyOfRange(words, 0, length)
            );

            if (!variations.contains(variation)) {
                variations.add(variation);
            }
        }

        return variations;
    }

    private String clean(String value) {

        if (value == null) return "";

        String result = value;

        result = result.replaceAll(
                "(?i)\\([^)]*(lyrics?|official|audio|video|visualizer|4k|hd|music video)[^)]*\\)",
                " "
        );

        result = result.replaceAll(
                "(?i)\\[[^]]*(lyrics?|official|audio|video|visualizer|4k|hd)[^]]*\\]",
                " "
        );

        result = result.replaceAll(
                "(?i)\\b(official|lyrics?|audio|video|visualizer|4k|hd|music video)\\b",
                " "
        );

        return result.replaceAll("\\s+", " ").trim();
    }
}
