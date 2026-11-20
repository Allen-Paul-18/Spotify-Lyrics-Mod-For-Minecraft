
package com.allen.spotifylyrics.media.providers;

import com.allen.spotifylyrics.media.MediaInfo;
import com.allen.spotifylyrics.media.PlaybackInfo;

public interface MediaProvider {

    String getName();

    MediaInfo getMediaInfo();

    PlaybackInfo getPlaybackInfo();

    boolean isAvailable();
}
