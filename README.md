# Spotify Lyrics Mod for Minecraft

A Minecraft Fabric mod that displays real-time, synchronized Spotify lyrics directly inside Minecraft.

Listen to your favourite songs while playing Minecraft without switching windows to check the lyrics!

## Features

- **Spotify Detection** – Automatically detects the currently playing Spotify song.
- **Synchronized Lyrics** – Fetches timestamped lyrics using the LRCLIB API.
- **Real-Time Updates** – Displays lyrics according to the current Spotify playback position.
- **Customizable HUD** – Change the position, scale and background appearance.
- **Lyrics Offset** – Adjust synchronization when lyrics appear early or late.
- **Settings Screen** – Configure the HUD directly inside Minecraft.
- **Keyboard Shortcuts** – Quickly open settings or toggle the HUD.

## Tech Stack

- Java 25
- Minecraft 26.2
- Fabric Loader
- Fabric API
- Gradle
- SMTC4J – Spotify media detection
- LRCLIB API – Synchronized lyrics
- Gson – JSON processing

## Project Structure

```text
SpotifyLyricsMod/
├── src/
│   ├── main/
│   └── client/
│       └── java/
│           └── com/allen/spotifylyrics/
│               └── client/
│                   ├── config/
│                   ├── hud/
│                   ├── input/
│                   └── screen/
├── README.md
├── .gitignore
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Keyboard Controls

| Key | Action |
|---|---|
| Right Shift | Open lyrics settings |
| Right Ctrl | Toggle lyrics HUD |

## Requirements

- Minecraft Java Edition 26.2
- Java 25
- Fabric Loader
- Fabric API
- Spotify Desktop application

## Building from Source

Clone the repository:

```bash
git clone https://github.com/Allen-Paul-18/Spotify-Lyrics-Mod-For-Minecraft.git
```

Navigate into the project:

```bash
cd Spotify-Lyrics-Mod-For-Minecraft
```

Build the mod:

**Windows**
```bash
gradlew.bat build
```

**Linux / macOS**
```bash
./gradlew build
```

The generated mod JAR will be available inside:

```text
build/libs/
```

## Lyrics Source

Lyrics are retrieved from [LRCLIB](https://lrclib.net/), an API that provides synchronized and plain song lyrics.

## Project Status

**Currently working:** Spotify detection, synchronized lyrics retrieval and in-game HUD display.

More features and improvements are planned.

---

Developed by [Allen-Paul-18](https://github.com/Allen-Paul-18).