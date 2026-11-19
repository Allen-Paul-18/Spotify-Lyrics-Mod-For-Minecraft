
package com.allen.spotifylyrics.spotify;

import java.util.List;

public class LyricsSync {

    private final List<LyricLine> lyrics;

    // Offset in seconds
    private double offset = 0.0;

    public LyricsSync(List<LyricLine> lyrics) {
        this.lyrics = lyrics;
    }

    public LyricLine getCurrentLyric(double currentPosition) {

        double adjustedPosition = currentPosition + offset;

        LyricLine currentLyric = null;

        for (LyricLine lyric : lyrics) {

            if (lyric.getTimestamp() <= adjustedPosition) {
                currentLyric = lyric;
            } else {
                break;
            }
        }

        return currentLyric;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getOffset() {
        return offset;
    }
}
