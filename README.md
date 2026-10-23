# Simple Autoclicker

A client-only Fabric mod for Minecraft 26.3. Toggleable left- and right-click
autoclicking with exact, per-button timings and an in-game settings screen.

## Usage

| Key (default) | Action |
| --- | --- |
| `,` (Comma) | Toggle left-click (attack) autoclicking |
| `.` (Period) | Toggle right-click (use) autoclicking |
| `[` (Left bracket) | Toggle holding left click (keeps mining) |
| `]` (Right bracket) | Toggle holding right click (keeps eating, blocking, using) |
| `;` (Semicolon) | Open the settings screen |

- Each toggle shows its ON/OFF state in the action bar. Holding and repeated
  clicking on the same button switch each other off.
- The settings screen has one row per button: a slider (50 ms – 10 s) plus
  seconds and milliseconds boxes next to it. Decimal seconds work — typing
  `1.5` in the seconds box becomes Sec: 1 / Ms: 500 when you click away.
  The slider snaps to the game's 50 ms tick steps; typed values are kept
  as-is. Changes apply immediately and are saved to
  `config/simpleautoclicker.json`.
- **Keep running when tabbed out** (settings screen, off by default): while a
  clicker or hold is running, tabbing out doesn't open the pause menu, so it
  keeps going in the background. With nothing running, the game pauses as
  normal; your own pause-on-focus-loss option is never changed.
- Autoclicking pauses while any GUI is open and all toggles switch off when
  you leave a world.
- Known limitations: stopping a hold releases the button even if you're
  physically holding it (your next click resyncs it), and a left-click hold
  stops mining if a screen opens and closes while you're tabbed out.

## Building

Requires JDK 25 (Minecraft 26.3). Either run `build-mod.bat` (points
`JAVA_HOME` at a local Adoptium JDK 25) or:

```
gradlew build
```

The jar lands in `build/libs/simpleautoclicker-<version>.jar`. Use
`run-client.bat` or `gradlew runClient` for a dev client.
