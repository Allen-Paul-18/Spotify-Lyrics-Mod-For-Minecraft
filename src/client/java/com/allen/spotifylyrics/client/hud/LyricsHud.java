
package com.allen.spotifylyrics.client.hud;

import com.allen.spotifylyrics.client.config.SpotifyLyricsConfig;
import com.allen.spotifylyrics.spotify.LyricLine;
import com.allen.spotifylyrics.spotify.LyricsSync;
import com.allen.spotifylyrics.spotify.MediaController;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class LyricsHud {

    public static void render(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTracker) {

        SpotifyLyricsConfig config =
                SpotifyLyricsConfig.getInstance();

        if (!config.hudEnabled) return;

        Minecraft minecraft = Minecraft.getInstance();

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        List<String> lines = new ArrayList<>();

        String title = MediaController.getCurrentTitle();
        String artist = MediaController.getCurrentArtist();

        if (config.showSongTitle && !title.isBlank()) {
            lines.add(title);
        }

        if (config.showArtist && !artist.isBlank()) {
            lines.add(artist);
        }

        LyricsSync sync = MediaController.getLyricsSync();

        if (title.isBlank()) {
            lines.add("Waiting for music...");
        } else if (sync == null) {
            lines.add("Fetching lyrics...");
        } else {
            LyricLine current = sync.getCurrentLyric(
                    MediaController.getCurrentPosition()
            );

            if (current != null && !current.getText().isBlank()) {
                lines.add(current.getText());
            }
        }

        int padding = 8;
        int lineHeight = 14;
        int textWidth = 0;

        for (String line : lines) {
            textWidth = Math.max(
                    textWidth,
                    minecraft.font.width(line)
            );
        }

        int boxWidth = textWidth + padding * 2;
        int boxHeight = lines.size() * lineHeight + padding * 2;

        int x;
        int y;

        switch (config.hudPosition) {

            case TOP_LEFT -> {
                x = 15;
                y = 15;
            }

            case TOP_CENTER -> {
                x = (screenWidth - boxWidth) / 2;
                y = 15;
            }

            case TOP_RIGHT -> {
                x = screenWidth - boxWidth - 15;
                y = 15;
            }

            case BOTTOM_LEFT -> {
                x = 15;
                y = screenHeight - config.hudY - boxHeight;
            }

            case BOTTOM_CENTER -> {
                x = (screenWidth - boxWidth) / 2;
                y = screenHeight - config.hudY - boxHeight;
            }

            case BOTTOM_RIGHT -> {
                x = screenWidth - boxWidth - 15;
                y = screenHeight - config.hudY - boxHeight;
            }

            case CUSTOM -> {
                x = config.hudX;
                y = config.hudY;
            }

            default -> {
                x = 15;
                y = screenHeight - config.hudY - boxHeight;
            }
        }

        x = Math.max(0, Math.min(x, screenWidth - boxWidth));
        y = Math.max(0, Math.min(y, screenHeight - boxHeight));

        var matrices = graphics.pose();

        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(config.hudScale, config.hudScale);

        if (config.showBackground) {

            int alpha = Math.round(
                    config.backgroundOpacity * 255
            );

            int backgroundColor = (alpha << 24) | 0x00101010;

            graphics.fill(
                    0, 0,
                    boxWidth, boxHeight,
                    backgroundColor
            );
        }

        int textY = padding;

        for (int i = 0; i < lines.size(); i++) {

            int color = (i == lines.size() - 1 && sync != null
                    && !title.isBlank())
                    ? 0xFF55FF55
                    : 0xFFFFFFFF;

            graphics.text(
                    minecraft.font,
                    lines.get(i),
                    padding,
                    textY,
                    color,
                    true
            );

            textY += lineHeight;
        }

        matrices.popMatrix();
    }
}
