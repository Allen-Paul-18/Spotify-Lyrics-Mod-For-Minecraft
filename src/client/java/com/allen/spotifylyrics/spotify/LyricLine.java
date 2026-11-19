package com.allen.spotifylyrics.spotify;

public class LyricLine {

    private final double timestamp;
    private final String text;

    public LyricLine(double timestamp, String text) {
        this.timestamp = timestamp;
        this.text = text;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + text;
    }
}