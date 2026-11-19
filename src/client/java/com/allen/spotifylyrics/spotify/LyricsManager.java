
package com.allen.spotifylyrics.spotify;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class LyricsManager implements AutoCloseable {

    private final ExecutorService executor =
            Executors.newCachedThreadPool();

    private final AtomicLong requestId = new AtomicLong(0);

    private volatile LyricsSync lyricsSync;
    private volatile String currentSongKey = "";

    // Lyrics synchronization offset in seconds
    private volatile double lyricsOffset = 0.0;

    private Future<?> currentTask;

    // Request lyrics without blocking the main thread
    public synchronized void loadLyrics(
            String title,
            String artist,
            String album,
            double duration) {

        String songKey = title + "|" + artist;

        // Avoid duplicate requests
        if (songKey.equals(currentSongKey)) {
            return;
        }

        currentSongKey = songKey;
        lyricsSync = null;

        long currentRequestId = requestId.incrementAndGet();

        // Cancel previous request
        if (currentTask != null) {
            currentTask.cancel(true);
        }

        System.out.println("Fetching lyrics in background...");

        currentTask = executor.submit(() -> {

            try {
                LyricsClient client = new LyricsClient();

                JsonObject result = client.getLyrics(
                        title,
                        artist,
                        album,
                        duration
                );

                // Ignore outdated results
                if (requestId.get() != currentRequestId) {
                    return;
                }

                if (result == null
                        || !result.has("syncedLyrics")
                        || result.get("syncedLyrics").isJsonNull()
                        || result.get("syncedLyrics")
                        .getAsString().isBlank()) {

                    System.out.println("Synced lyrics unavailable.");
                    return;
                }

                String syncedLyrics =
                        result.get("syncedLyrics").getAsString();

                List<LyricLine> lyrics =
                        LrcParser.parse(syncedLyrics);

                if (lyrics.isEmpty()) {
                    System.out.println("No lyric lines found.");
                    return;
                }

                // Create sync object and apply current offset
                if (requestId.get() == currentRequestId) {

                    LyricsSync newSync = new LyricsSync(lyrics);

                    newSync.setOffset(lyricsOffset);

                    lyricsSync = newSync;

                    System.out.println(
                            "Lyrics loaded successfully: "
                                    + lyrics.size() + " lines"
                    );

                    System.out.println(
                            "Current lyrics offset: "
                                    + lyricsOffset + " seconds"
                    );
                }

            } catch (Exception e) {

                if (requestId.get() == currentRequestId) {
                    System.out.println(
                            "Lyrics fetching failed: " + e.getMessage()
                    );
                }
            }
        });
    }

    // Get currently loaded lyrics
    public LyricsSync getLyricsSync() {
        return lyricsSync;
    }

    // Check whether lyrics are ready
    public boolean isLyricsReady() {
        return lyricsSync != null;
    }

    // Set synchronization offset
    public void setOffset(double offset) {

        if (!Double.isFinite(offset)) {
            throw new IllegalArgumentException(
                    "Offset must be a finite number."
            );
        }

        this.lyricsOffset = offset;

        LyricsSync sync = lyricsSync;

        if (sync != null) {
            sync.setOffset(offset);
        }

        System.out.println(
                "Lyrics offset updated: " + offset + " seconds"
        );
    }

    // Get current offset
    public double getOffset() {
        return lyricsOffset;
    }

    // Reset when needed
    public synchronized void reset() {

        requestId.incrementAndGet();

        if (currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }

        currentSongKey = "";
        lyricsSync = null;
    }

    // Stop background threads
    @Override
    public void close() {

        reset();
        executor.shutdownNow();
    }
}
