# JuJu's World — Android App (v2)

> **Built by [sierengowskisierengowski-cpu](https://github.com/sierengowskisierengowski-cpu)**  
> Designed and developed with ❤️ for JuJu (Jewel) — a magical world just for her.

A magical Android launcher app for JuJu (Jewel), built with Kotlin + Jetpack Compose.
Theme: butterflies, unicorns, sparkles, princess — pink & purple palette.

---

## Quick Start

1. **Unzip** `juju-world-android.zip` anywhere on your computer
2. Open **Android Studio** → **Open** → select the `juju-world-android` folder
3. Let Gradle sync (first run downloads ~500 MB of dependencies — takes ~5 min on a good connection)
4. Connect your tablet via USB (ADB device ID: **TK12110626A041493**)  
   Enable USB Debugging on the tablet if prompted
5. Select your device in the toolbar → press **▶ Run**

---

## Setting Up Kiosk Mode (Recommended for JuJu)

Kiosk mode locks the app so JuJu can't exit to the Android home screen, notifications, or any other app.

**Run once from your computer after installing:**

```bash
adb shell dpm set-device-owner com.jujusworld/.AdminReceiver
```

Then open the app → go to **Parent Zone** → enter your PIN → flip **Kiosk Mode** ON.

- JuJu's Home button, Recents, and status bar are all disabled while kiosk is ON.
- To exit kiosk, open Parent Zone with your PIN and flip it OFF.
- To remove device owner entirely:  
  `adb shell dpm remove-active-admin com.jujusworld/.AdminReceiver`

> If you haven't run the ADB command, the kiosk toggle will show as disabled.  
> The rest of the app works perfectly without kiosk mode.

---

## All Screens

| Screen       | What JuJu can do |
|--------------|------------------|
| **Splash**   | 15-second animated reveal with butterflies, unicorn, "Welcome, JuJu!" |
| **Home**     | Time-aware sky (morning/afternoon/evening/night), floating unicorn, 10 big tiles |
| **Shows**    | Ms. Rachel featured + 8 show tiles (YouTube, PBS Kids, Bluey, etc.) |
| **Games**    | 4 mini-games: Letters, Counting, Colors, Shapes — with score + TTS feedback |
| **Books**    | 3 illustrated story books with tap-to-advance pages + Read-to-Me voice |
| **Music**    | Lullaby player with album art + animated disc |
| **Art**      | Full touch-drawing canvas: 12 colors, brush sizes, eraser, emoji stamps, undo |
| **Camera**   | CameraX with Hearts/Stars/Rainbow/Butterflies overlay filters + save to gallery |
| **Browser**  | Safe WebView browser with 9 kid-safe bookmarks + back/forward/home controls |
| **App Store**| 12 curated apps with Play Store deep-links (YouTube Kids, Khan Academy, etc.) |
| **Sleep**    | Starfield animation, breathing guide, lullaby/ocean audio player |
| **Parent**   | PIN gate → section toggles, kiosk on/off, star count, PIN change |

---

## Included Audio Files

9 greeting MP3s from the web app are bundled in:
`app/src/main/assets/audio/greetings/`

- `good_morning.mp3`, `good_afternoon.mp3`, `good_evening.mp3`, `goodnight.mp3`
- `welcome.mp3`, `great_job.mp3`, `you_did_it.mp3`, `lets_play.mp3`
- `i_love_you.mp3`, `happy_birthday.mp3`

These play on the Splash screen based on time of day. TTS is used as fallback.

## Adding Lullaby Audio (Optional)

For Music + Sleep screen playback, add MP3 files to:
`app/src/main/assets/audio/lullabies/`

Expected names: `twinkle.mp3`, `ocean.mp3`, `rain.mp3`, `baabaa.mp3`,
`hush.mp3`, `lavender.mp3`, `jingle.mp3`

The app handles missing files gracefully — no crash, just silence.

---

## Permissions

| Permission | Used for |
|------------|----------|
| `INTERNET` | Shows, Browser, App Store WebViews |
| `CAMERA` | Camera screen |
| `WRITE_EXTERNAL_STORAGE` | Save photos (Android ≤ 9) |
| `READ_MEDIA_IMAGES` | Photo gallery (Android 13+) |
| `BIND_DEVICE_ADMIN` | Kiosk / lock-task mode |
| `RECEIVE_BOOT_COMPLETED` | Auto-launch after reboot (as home screen) |

---

## Minimum Requirements

- Android **8.0** (API 26) or higher — most modern tablets are API 29+
- App is locked to **landscape** orientation (ideal for tablet)
- App registers as a **Home** intent, so Android will offer to set it as default launcher

---

## Troubleshooting

**Gradle sync fails** — Check internet connection. Android Studio needs to download ~500 MB on first sync.

**Device not found** — Run `adb devices` in terminal. Reconnect USB and allow USB Debugging when prompted on the tablet.

**Camera screen blank** — Tap "Allow Camera" when the permission dialog appears.

**Kiosk toggle greyed out** — Run the `dpm set-device-owner` ADB command first (see above).

**App shows as home screen option** — Go to Android Settings → Apps → Default Apps → Home App → select JuJu's World.

---

## Changelog

### v2.0 — Full V2 Vision Brief (May 2026)

**Bug Fixes**
- `AndroidManifest`: `screenOrientation` changed from `landscape` → `fullSensor` (auto-rotates for any tablet position)
- `SplashScreen`: delay extended to 12 000 ms so the greeting audio fully completes before navigating to Home

**Core Infrastructure**
- `Prefs.kt` — added `startSession()` / `endSession()` / `todayScreenTimeMs` / `addSectionTime()` for per-day screen-time tracking
- `WinCelebration.kt` — shared full-screen confetti overlay composable (auto-dismisses in 3 s, awards +1 star, TTS "You did it!")
- `NavGraph.kt` — all 14 game routes registered (4 original + 10 new stubs)

**HomeScreen (complete rewrite)**
- Animated enchanted-meadow night sky background: stars, fireflies, drifting clouds, swaying flowers
- Animated 🦉 owl mascot with morning/afternoon/evening/bedtime moods, greeting, bobbing, swaying, orbiting butterfly, ZZZ floats, occasional yawn
- 9 floating nav tiles each with independent float animation, pressed-spring scale, gradient fill
- Hidden parent zone: tap the clock 5× within 3 s → opens ParentScreen invisibly

**GamesScreen (rewrite)**
- 14 game tiles total with 1–3 difficulty ⭐ indicators
- Arcade carnival night background: neon glow dots, confetti rain
- Original games: Letters, Counting, Colors, Shapes

**10 New Games**
| Game | Description |
|---|---|
| 🫧 Bubble Pop | Tap 16 wobbling bubbles to pop all — WinCelebration on clear |
| 🐮 Animal Sounds | 12 animals — tap to hear TTS sound + name |
| 🧩 Memory Match | 12-pair emoji flip-match — animated page transition, WinCelebration |
| 🔨 Whack-a-Mole | 30-second timer, moles pop in 12 holes |
| 🎈 Balloon Pop | 12 floating balloons, tap to pop |
| 🎹 Piano | 8 color-coded keys with ToneGenerator DTMF tones |
| 💃 Dance Party | Sweeping disco lights, 8 emoji dancers, beat pulse |
| 👗 Dress Up | Character preview + wardrobe picker (hats, outfits, accessories) |
| 🐟 Feed Animal | Match animal → correct food, 8 rounds, WinCelebration |
| 🎤 Nursery Rhyme | 6 rhymes, line-by-line TTS, animated emoji illustrations |

**ShowsScreen**
- 6 channel shortcut buttons (Cocomelon, Ms. Rachel, Blippi, Bluey, Paw Patrol, Peppa Pig)
- Cozy golden-hour background with drifting clouds, floating popcorn, twinkling stars
- Back button, loading indicator, Home (YouTube Kids) button

**BooksScreen**
- 3 complete storybooks (6 pages each): Butterfly Princess, Unicorn's Rainbow, Bella the Brave Bee
- Animated crossfade page transitions (slide + fade), star progress bar
- "Read to Me" TTS button on every page; WinCelebration on finishing a book
- Enchanted library forest background with golden dust particles

**MusicScreen**
- Spinning vinyl record (tracks rotation speed to play/pause state)
- 24-bar rainbow frequency visualizer
- 4 emoji dancers that bounce to the beat
- Sweeping concert stage light beams, floating music notes when playing
- 7-track playlist with emoji icons

**SleepScreen (full build)**
- Lullabies list (5 tracks, TTS play)
- White noise picker (6 options: Rain, Ocean, Heartbeat, Fan, Forest, Crickets)
- Breathing guide: animated expanding/contracting circle (Inhale 4 s → Hold 2 s → Exhale 4 s)
- Sleep timer: 15 / 30 / 45 / 60 minute presets
- Black screen mode on timer expiry; tap black screen → "Shh 🌙 Sweet dreams JuJu…" → re-locks
- Midnight sky background: 80 twinkling stars, drifting cloud, moon with halo glow, ZZZ floats

**ArtScreen**
- 4 brush types: Normal, Sparkle (glitter trail), Rainbow (hue-shifting), Stamp
- 12 stamp emojis, 10-color palette, variable brush size slider
- Undo (per stroke or stamp), Clear (with confirmation dialog), Save to Gallery
- Canvas overlay stamps rendered as composable Text above the drawing canvas

**ParentScreen**
- Real-time screen time display (today total + per-section breakdown)
- Section visibility toggles for all 9 sections (persisted via Prefs)
- Full PIN change flow: verify current → enter new → confirm (or 5-tap-lock bypass if no PIN set)
- Kiosk mode toggle with ON/OFF state, emergency exit button
- Stars counter with reset button

**AppStoreScreen**
- 15 curated kid-safe apps with age tags, descriptions, Play Store deep-links
- Apps include: YouTube Kids, Khan Academy Kids, Endless Alphabet, Starfall, Toca Kitchen 2, Daniel Tiger, Toca Life World, PBS KIDS, Drawing Academy, Endless Numbers, Paw Patrol, Dino Dan, Duolingo ABC, Sago Mini, Bluey
- Dark starfield background with confetti and star twinkle
