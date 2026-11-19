
package com.allen.spotifylyrics.spotify;

import dev.codeman.smtc4j.SMTC4J;
import dev.codeman.smtc4j.MediaInfo;
import dev.codeman.smtc4j.PlaybackState;

public class SpotifyDetector {

    private static boolean initialized = false;

    private static String lastTitle = "";
    private static String lastArtist = "";

    private static final LyricsManager lyricsManager =
            new LyricsManager();

    private static volatile String currentTitle = "";
    private static volatile String currentArtist = "";

    private static volatile double currentPosition = 0.0;

    public static void initialize() {

        try {
            SMTC4J.load();

            if (SMTC4J.isLoaded()) {

                SMTC4J.startUpdateScheduler(500);

                initialized = true;

                System.out.println(
                        "Spotify Detector Initialized!"
                );
            }

        } catch (Exception e) {
            System.err.println(
                    "Spotify initialization failed: "
                            + e.getMessage()
            );
        }
    }

    public static void detectSong() {

        if (!initialized) return;

        try {
            MediaInfo media = SMTC4J.getCachedMediaInfo();
            PlaybackState state =
                    SMTC4J.getCachedPlaybackState();

            if (media == null || state == null) return;

            String title = media.title();
            String artist = media.artist();

            if (title == null || title.isBlank()) return;

            if (artist == null) artist = "";

            currentPosition = state.position();

            currentTitle = title;
            currentArtist = artist;

            // Fetch lyrics only when song changes
            if (!title.equals(lastTitle)
                    || !artist.equals(lastArtist)) {

                lastTitle = title;
                lastArtist = artist;

                System.out.println(
                        "Now Playing: " + title
                                + " - " + artist
                );

                lyricsManager.loadLyrics(
                        title,
                        artist,
                        media.album(),
                        media.duration()
                );
            }

        } catch (Exception e) {
            System.err.println(
                    "Spotify detection error: "
                            + e.getMessage()
            );
        }
    }

    public static String getCurrentTitle() {
        return currentTitle;
    }

    public static String getCurrentArtist() {
        return currentArtist;
    }

    public static double getCurrentPosition() {
        return currentPosition;
    }

    public static LyricsSync getLyricsSync() {
        return lyricsManager.getLyricsSync();
    }

    public static void setLyricsOffset(double offset) {
        lyricsManager.setOffset(offset);
    }
}
