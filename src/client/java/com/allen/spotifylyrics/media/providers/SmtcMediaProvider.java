
package com.allen.spotifylyrics.media.providers;

import com.allen.spotifylyrics.media.MediaInfo;
import com.allen.spotifylyrics.media.PlaybackInfo;

import dev.codeman.smtc4j.SMTC4J;
import dev.codeman.smtc4j.PlaybackState;

public class SmtcMediaProvider implements MediaProvider {

    public void initialize() {
        try {
            if (SMTC4J.load()) {
                SMTC4J.startUpdateScheduler(500);
                System.out.println("SMTC Media Provider Initialized!");
            }
        } catch (Exception e) {
            System.err.println("SMTC initialization failed: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "SMTC";
    }

    @Override
    public MediaInfo getMediaInfo() {

        dev.codeman.smtc4j.MediaInfo media =
                SMTC4J.getCachedMediaInfo();

        if (media == null || media.title() == null || media.title().isBlank()) {
            return null;
        }

        return new MediaInfo(
                media.title(),
                media.artist(),
                media.album(),
                media.duration(),
                media.sourceApp()
        );
    }

    @Override
    public PlaybackInfo getPlaybackInfo() {

        PlaybackState state = SMTC4J.getCachedPlaybackState();

        if (state == null) {
            return null;
        }

        return new PlaybackInfo(
                state.getStateCode() == PlaybackState.StateCode.PLAYING,
                state.position()
        );
    }

    @Override
    public boolean isAvailable() {
        return SMTC4J.isLoaded();
    }
}
