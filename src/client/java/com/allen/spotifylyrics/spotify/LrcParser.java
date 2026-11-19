package com.allen.spotifylyrics.spotify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcParser {

    // Matches timestamps like [01:08.10]
    private static final Pattern LRC_PATTERN =
            Pattern.compile("\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{1,3}))?\\](.*)");

    public static List<LyricLine> parse(String syncedLyrics) {

        List<LyricLine> lyrics = new ArrayList<>();

        if (syncedLyrics == null || syncedLyrics.isBlank()) {
            return lyrics;
        }

        String[] lines = syncedLyrics.split("\\R");

        for (String line : lines) {

            Matcher matcher = LRC_PATTERN.matcher(line);

            if (matcher.matches()) {

                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));

                String fraction = matcher.group(3);
                double milliseconds = 0;

                if (fraction != null) {
                    milliseconds = Integer.parseInt(
                            fraction + "0".repeat(3 - fraction.length())
                    ) / 1000.0;
                }

                double timestamp = (minutes * 60) + seconds + milliseconds;

                String text = matcher.group(4).trim();

                lyrics.add(new LyricLine(timestamp, text));
            }
        }

        // Ensure lyrics are ordered by timestamp
        lyrics.sort(Comparator.comparingDouble(LyricLine::getTimestamp));

        return lyrics;
    }
}