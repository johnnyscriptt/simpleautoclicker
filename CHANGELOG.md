# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-10-20

### Added
- Hold mode: `[` toggles holding left click (keeps mining a block), `]` toggles
  holding right click (keeps eating, drawing a bow, blocking or placing).
  Holding and repeated clicking on the same button switch each other off.
- "Keep running when tabbed out" setting (off by default). While it is on and
  a clicker or hold is running, tabbing out no longer opens the pause menu, so
  the action keeps going. Your own pause-on-focus-loss option is left untouched.

### Known limitations
- Stopping a hold releases the button even if you are physically holding it;
  your next real click resyncs it.
- If a screen opens and closes while tabbed out, a left-click hold stops
  mining until you refocus the window (right-click holds are unaffected).

## [1.0.1] - 2026-09-30

### Changed
- Updated to Minecraft 26.3 (Fabric Loader 0.19.5, Fabric API 0.161.0+26.3). No gameplay changes.

## [1.0.0] - 2026-07-23

Initial public release for Minecraft 26.2 (Fabric).

### Added
- Toggleable left-click and right-click autoclicking with independent timers.
- Default keybinds: `,` toggles left autoclick, `.` toggles right autoclick,
  `;` opens the settings screen (all rebindable in vanilla Controls).
- In-game settings screen with a per-button interval slider (50 ms – 10 s)
  and precise seconds/milliseconds text inputs, applied live.
- Action-bar status messages when a clicker is toggled on or off.
- Config persistence to `config/simpleautoclicker.json`, with out-of-range
  values clamped on load.
- Clicks route through vanilla attack/use logic, so hit results, cooldowns,
  and item behavior work exactly as manual clicks do.
- Autoclicking pauses automatically while any GUI screen is open, and both
  toggles reset when leaving a world.
