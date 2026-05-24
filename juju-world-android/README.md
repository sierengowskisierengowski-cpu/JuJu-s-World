# JuJu's World — Android App

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
