
package com.allen.spotifylyrics.media;

public record MediaInfo(
        String title,
        String artist,
        String album,
        double duration,
        String sourceApp
) {
    public boolean isValid() {
        return title != null && !title.isBlank();
    }
}
