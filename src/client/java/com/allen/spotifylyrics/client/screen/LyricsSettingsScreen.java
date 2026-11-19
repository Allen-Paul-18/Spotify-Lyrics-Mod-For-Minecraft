
package com.allen.spotifylyrics.client.screen;

import com.allen.spotifylyrics.client.config.SpotifyLyricsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LyricsSettingsScreen extends Screen {

    private final Screen parent;

    private final SpotifyLyricsConfig config =
            SpotifyLyricsConfig.getInstance();

    public LyricsSettingsScreen(Screen parent) {
        super(Component.literal("Spotify Lyrics Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {

        int centerX = this.width / 2;
        int startY = 55;

        int buttonWidth = 150;
        int buttonHeight = 20;
        int gap = 10;

        int leftX = centerX - buttonWidth - gap / 2;
        int rightX = centerX + gap / 2;

        // HUD position
        addButton(
                "Position: " + config.hudPosition.name(),
                leftX, startY, buttonWidth, buttonHeight,
                button -> {
                    SpotifyLyricsConfig.HudPosition[] positions =
                            SpotifyLyricsConfig.HudPosition.values();

                    int next = (config.hudPosition.ordinal() + 1)
                            % positions.length;

                    config.hudPosition = positions[next];
                    saveAndRefresh();
                }
        );

        // HUD enable/disable
        addButton(
                "HUD: " + (config.hudEnabled ? "ON" : "OFF"),
                rightX, startY, buttonWidth, buttonHeight,
                button -> {
                    config.hudEnabled = !config.hudEnabled;
                    saveAndRefresh();
                }
        );

        // Lyrics offset
        addButton(
                "Offset: -0.1 sec",
                leftX, startY + 30, buttonWidth, buttonHeight,
                button -> {
                    config.lyricsOffset =
                            Math.max(-5.0, config.lyricsOffset - 0.1);
                    saveAndRefresh();
                }
        );

        addButton(
                "Offset: +0.1 sec",
                rightX, startY + 30, buttonWidth, buttonHeight,
                button -> {
                    config.lyricsOffset =
                            Math.min(5.0, config.lyricsOffset + 0.1);
                    saveAndRefresh();
                }
        );

        // Show song title
        addButton(
                "Song Title: " + (config.showSongTitle ? "ON" : "OFF"),
                leftX, startY + 60, buttonWidth, buttonHeight,
                button -> {
                    config.showSongTitle = !config.showSongTitle;
                    saveAndRefresh();
                }
        );

        // Show artist
        addButton(
                "Artist: " + (config.showArtist ? "ON" : "OFF"),
                rightX, startY + 60, buttonWidth, buttonHeight,
                button -> {
                    config.showArtist = !config.showArtist;
                    saveAndRefresh();
                }
        );

        // Background visibility
        addButton(
                "Background: " + (config.showBackground ? "ON" : "OFF"),
                leftX, startY + 90, buttonWidth, buttonHeight,
                button -> {
                    config.showBackground = !config.showBackground;
                    saveAndRefresh();
                }
        );

        // HUD scale
        addButton(
                "Scale: -0.1",
                leftX, startY + 120, buttonWidth, buttonHeight,
                button -> {
                    config.hudScale =
                            Math.max(0.5f, config.hudScale - 0.1f);
                    saveAndRefresh();
                }
        );

        addButton(
                "Scale: +0.1",
                rightX, startY + 120, buttonWidth, buttonHeight,
                button -> {
                    config.hudScale =
                            Math.min(2.0f, config.hudScale + 0.1f);
                    saveAndRefresh();
                }
        );

        // Close settings
        addButton(
                "Done",
                centerX - 75, startY + 160, 150, buttonHeight,
                button -> onClose()
        );
    }

    private void addButton(
            String text,
            int x,
            int y,
            int width,
            int height,
            Button.OnPress action) {

        Button button = Button.builder(
                Component.literal(text),
                action
        ).bounds(x, y, width, height).build();

        this.addRenderableWidget(button);
    }

    private void saveAndRefresh() {
        config.save();

        Minecraft.getInstance().gui.setScreen(
                new LyricsSettingsScreen(parent)
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta) {

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        String title = "Spotify Lyrics Settings";

        graphics.text(
                this.font,
                title,
                (this.width - this.font.width(title)) / 2,
                25,
                0xFFFFFFFF,
                true
        );

        String offset = "Current Offset: "
                + String.format("%.1f", config.lyricsOffset)
                + " seconds";

        graphics.text(
                this.font,
                offset,
                (this.width - this.font.width(offset)) / 2,
                this.height - 35,
                0xFFAAAAAA,
                true
        );
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
