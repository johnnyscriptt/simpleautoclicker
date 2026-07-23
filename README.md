# Simple Autoclicker

A client-only Fabric mod for Minecraft 26.2. Toggleable left- and right-click
autoclicking with exact, per-button timings and an in-game settings screen.

## Usage

| Key (default) | Action |
| --- | --- |
| `,` (Comma) | Toggle left-click (attack) autoclicking |
| `.` (Period) | Toggle right-click (use) autoclicking |
| `;` (Semicolon) | Open the settings screen |

- Each toggle shows "Left/Right autoclick: ON/OFF" in the action bar.
- The settings screen has one row per button: a slider (50 ms – 10 s) plus
  seconds and milliseconds boxes next to it. Decimal seconds work — typing
  `1.5` in the seconds box becomes Sec: 1 / Ms: 500 when you click away.
  The slider snaps to the game's 50 ms tick steps; typed values are kept
  as-is. Changes apply immediately and are saved to
  `config/simpleautoclicker.json`.
- Autoclicking pauses while any GUI is open and both toggles switch off when
  you leave a world.

## Building

Requires JDK 25 (Minecraft 26.2). Either run `build-mod.bat` (points
`JAVA_HOME` at a local Adoptium JDK 25) or:

```
gradlew build
```

The jar lands in `build/libs/simpleautoclicker-<version>.jar`. Use
`run-client.bat` or `gradlew runClient` for a dev client.
