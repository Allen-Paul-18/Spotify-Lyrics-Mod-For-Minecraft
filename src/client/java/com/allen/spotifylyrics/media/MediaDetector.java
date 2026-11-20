
package com.allen.spotifylyrics.media;

import com.allen.spotifylyrics.media.providers.MediaProvider;
import com.allen.spotifylyrics.media.providers.SmtcMediaProvider;

import java.util.ArrayList;
import java.util.List;

public class MediaDetector {

    private static final List<MediaProvider> providers = new ArrayList<>();

    public static void initialize() {
        SmtcMediaProvider smtcProvider = new SmtcMediaProvider();

        smtcProvider.initialize();
        registerProvider(smtcProvider);
    }

    public static void registerProvider(MediaProvider provider) {
        providers.add(provider);
    }

    private static MediaProvider getActiveProvider() {
        for (MediaProvider provider : providers) {
            if (!provider.isAvailable()) {
                continue;
            }

            MediaInfo info = provider.getMediaInfo();

            if (info != null && info.isValid()) {
                return provider;
            }
        }

        return null;
    }

    public static MediaInfo getCurrentMediaInfo() {
        MediaProvider provider = getActiveProvider();

        return provider != null ? provider.getMediaInfo() : null;
    }

    public static PlaybackInfo getCurrentPlaybackInfo() {
        MediaProvider provider = getActiveProvider();

        return provider != null ? provider.getPlaybackInfo() : null;
    }
}
