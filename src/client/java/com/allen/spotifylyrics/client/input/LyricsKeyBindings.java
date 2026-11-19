
package com.allen.spotifylyrics.client.input;

import com.allen.spotifylyrics.client.config.SpotifyLyricsConfig;
import com.allen.spotifylyrics.client.screen.LyricsSettingsScreen;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class LyricsKeyBindings {

    private static KeyMapping openSettingsKey;
    private static KeyMapping toggleHudKey;

    public static void register() {

        openSettingsKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.spotifylyrics.open_settings",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        KeyMapping.Category.MISC
                )
        );

        toggleHudKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.spotifylyrics.toggle_hud",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_CONTROL,
                        KeyMapping.Category.MISC
                )
        );
    }

    public static void handleInput() {

        Minecraft minecraft = Minecraft.getInstance();

        while (openSettingsKey.consumeClick()) {
            if (minecraft.gui.screen() == null) {
                minecraft.gui.setScreen(
                        new LyricsSettingsScreen(null)
                );
            }
        }

        while (toggleHudKey.consumeClick()) {

            SpotifyLyricsConfig config =
                    SpotifyLyricsConfig.getInstance();

            config.hudEnabled = !config.hudEnabled;
            config.save();

            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(
                        Component.literal(
                                "Spotify Lyrics HUD: "
                                        + (config.hudEnabled ? "ON" : "OFF")
                        )
                );
            }
        }
    }
}
