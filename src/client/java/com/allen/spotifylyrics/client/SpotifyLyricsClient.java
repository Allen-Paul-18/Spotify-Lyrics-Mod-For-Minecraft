
package com.allen.spotifylyrics.client;

import com.allen.spotifylyrics.client.config.SpotifyLyricsConfig;
import com.allen.spotifylyrics.client.hud.LyricsHud;
import com.allen.spotifylyrics.client.input.LyricsKeyBindings;
import com.allen.spotifylyrics.spotify.SpotifyDetector;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class SpotifyLyricsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		// Load configuration
		SpotifyLyricsConfig.load();

		// Initialize Spotify detection
		SpotifyDetector.initialize();

		// Apply saved lyrics offset
		SpotifyDetector.setLyricsOffset(
				SpotifyLyricsConfig.getInstance().lyricsOffset
		);

		// Register keyboard shortcuts
		LyricsKeyBindings.register();

		// Handle client ticks
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			LyricsKeyBindings.handleInput();

			if (client.level != null) {
				SpotifyDetector.detectSong();
			}
		});

		// Register lyrics HUD
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(
						"spotifylyrics", "lyrics_hud"
				),
				LyricsHud::render
		);

		System.out.println("Spotify Lyrics HUD Registered!");
	}
}
