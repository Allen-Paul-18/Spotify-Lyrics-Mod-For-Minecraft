
package com.allen.spotifylyrics.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpotifyLyricsConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("spotifylyrics.json");

    private static SpotifyLyricsConfig instance;

    // HUD settings
    public boolean hudEnabled = true;

    public HudPosition hudPosition = HudPosition.BOTTOM_LEFT;

    public int hudX = 15;
    public int hudY = 65;

    // Lyrics synchronization
    public double lyricsOffset = 0.0;

    // Appearance settings
    public float hudScale = 1.0f;
    public float backgroundOpacity = 0.8f;

    public boolean showSongTitle = true;
    public boolean showArtist = true;
    public boolean showBackground = true;

    // HUD positions
    public enum HudPosition {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
        CUSTOM
    }

    // Load configuration
    public static SpotifyLyricsConfig load() {

        if (Files.exists(CONFIG_PATH)) {

            try {
                String json = Files.readString(
                        CONFIG_PATH,
                        StandardCharsets.UTF_8
                );

                SpotifyLyricsConfig config =
                        GSON.fromJson(json, SpotifyLyricsConfig.class);

                if (config != null) {
                    config.validate();
                    instance = config;
                    return instance;
                }

            } catch (Exception e) {
                System.err.println(
                        "Failed to load Spotify Lyrics config: "
                                + e.getMessage()
                );
            }
        }

        instance = new SpotifyLyricsConfig();
        instance.save();

        return instance;
    }

    // Get current configuration
    public static SpotifyLyricsConfig getInstance() {

        if (instance == null) {
            return load();
        }

        return instance;
    }

    // Save configuration
    public void save() {

        validate();

        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            String json = GSON.toJson(this);

            Files.writeString(
                    CONFIG_PATH,
                    json,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            System.err.println(
                    "Failed to save Spotify Lyrics config: "
                            + e.getMessage()
            );
        }
    }

    // Validate settings
    private void validate() {

        if (hudPosition == null) {
            hudPosition = HudPosition.BOTTOM_LEFT;
        }

        lyricsOffset = Double.isFinite(lyricsOffset)
                ? Math.max(-5.0, Math.min(5.0, lyricsOffset))
                : 0.0;

        hudScale = Float.isFinite(hudScale)
                ? Math.max(0.5f, Math.min(2.0f, hudScale))
                : 1.0f;

        backgroundOpacity = Float.isFinite(backgroundOpacity)
                ? Math.max(0.0f, Math.min(1.0f, backgroundOpacity))
                : 0.8f;

        hudX = Math.max(0, Math.min(10000, hudX));
        hudY = Math.max(0, Math.min(10000, hudY));
    }
}
