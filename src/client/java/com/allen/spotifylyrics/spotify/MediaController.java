
package com.allen.spotifylyrics.spotify;

import com.allen.spotifylyrics.media.MediaDetector;
import com.allen.spotifylyrics.media.MediaInfo;
import com.allen.spotifylyrics.media.PlaybackInfo;

public class MediaController {

    private static String lastTitle = "";
    private static String lastArtist = "";

    private static final LyricsManager lyricsManager =
            new LyricsManager();

    private static volatile String currentTitle = "";
    private static volatile String currentArtist = "";
    private static volatile double currentPosition = 0.0;

    public static void initialize() {
        MediaDetector.initialize();
        System.out.println("Universal Media Detection Initialized!");
    }

    public static void detectSong() {

        MediaInfo media = MediaDetector.getCurrentMediaInfo();
        PlaybackInfo playback = MediaDetector.getCurrentPlaybackInfo();

        if (media == null || playback == null) {
            return;
        }

        String title = media.title();
        String artist = media.artist();

        if (title == null || title.isBlank()) {
            return;
        }

        if (artist == null) {
            artist = "";
        }

        currentPosition = playback.position();
        currentTitle = title;
        currentArtist = artist;

        if (!title.equals(lastTitle) || !artist.equals(lastArtist)) {

            lastTitle = title;
            lastArtist = artist;

            System.out.println("Now Playing: " + title + " - " + artist);
            System.out.println("Source: " + media.sourceApp());

            lyricsManager.loadLyrics(
                    title,
                    artist,
                    media.album(),
                    media.duration()
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
