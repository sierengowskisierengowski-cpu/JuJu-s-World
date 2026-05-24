package com.jujusworld.utils

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Centralised sound system for JuJu's World.
 *
 * Sound effects use ToneGenerator (no audio files needed).
 * Music / lullabies use MediaPlayer with assets.
 * Greetings use TTS with MP3 asset fallback.
 */
object SoundManager {
    private var tts: TextToSpeech? = null
    private var bgPlayer: MediaPlayer? = null
    private var ttsReady = false

    // ToneGenerator: stream MUSIC at 70% volume
    private val toneHigh by lazy { ToneGenerator(AudioManager.STREAM_MUSIC, 70) }
    private val toneMed  by lazy { ToneGenerator(AudioManager.STREAM_MUSIC, 50) }

    fun init(context: Context) {
        tts = TextToSpeech(context) { status ->
            ttsReady = (status == TextToSpeech.SUCCESS)
            if (ttsReady) {
                tts?.language    = Locale.US
                tts?.setSpeechRate(0.85f)
                tts?.setPitch(1.1f)
            }
        }
    }

    // ── Sound effects ───────────────────────────────────────────────────────

    /** Short happy chime — play on every tile tap. */
    fun playTap() = try { toneHigh.startTone(ToneGenerator.TONE_PROP_BEEP, 80) } catch (_: Exception) {}

    /** Higher chime — correct answer / celebration. */
    fun playSuccess() = try { toneHigh.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 250) } catch (_: Exception) {}

    /** Low blip — wrong answer / error. */
    fun playError() = try { toneMed.startTone(ToneGenerator.TONE_PROP_NACK, 200) } catch (_: Exception) {}

    /** Camera shutter sound via AudioManager. */
    fun playShutter(context: Context) {
        try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
        } catch (_: Exception) {}
    }

    /** Page turn / whoosh. */
    fun playWhoosh() = try { toneMed.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 120) } catch (_: Exception) {}

    /** Star / sparkle pop. */
    fun playStar() = try { toneHigh.startTone(ToneGenerator.TONE_PROP_ACK, 60) } catch (_: Exception) {}

    // ── TTS ─────────────────────────────────────────────────────────────────

    fun speak(text: String) {
        if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "juju")
    }

    fun speakGreeting(context: Context) {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val (file, text) = when {
            hour in 5..11  -> "audio/greetings/good_morning.mp3"   to "Good morning JuJu! Rise and shine, beautiful!"
            hour in 12..16 -> "audio/greetings/good_afternoon.mp3" to "Good afternoon JuJu! Hope you're having a magical day!"
            hour in 17..20 -> "audio/greetings/good_evening.mp3"   to "Good evening JuJu! You were so amazing today!"
            else           -> "audio/greetings/goodnight.mp3"       to "Goodnight JuJu! Sweet dreams, my precious girl!"
        }
        tryPlayAssetMp3(context, file) { speak(text) }
    }

    // ── MediaPlayer helpers ──────────────────────────────────────────────────

    fun tryPlayAssetMp3(context: Context, assetPath: String, fallback: () -> Unit = {}) {
        try {
            val afd = context.assets.openFd(assetPath)
            val mp  = MediaPlayer()
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.setOnCompletionListener { it.release() }
            mp.prepare(); mp.start()
        } catch (_: Exception) { fallback() }
    }

    fun playBgMusic(context: Context, assetPath: String, loop: Boolean = true) {
        stopBgMusic()
        try {
            val afd = context.assets.openFd(assetPath)
            bgPlayer = MediaPlayer()
            bgPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            bgPlayer?.isLooping = loop
            bgPlayer?.setVolume(0.6f, 0.6f)
            bgPlayer?.prepare(); bgPlayer?.start()
        } catch (_: Exception) { }
    }

    fun stopBgMusic() { bgPlayer?.stop(); bgPlayer?.release(); bgPlayer = null }
    fun setBgVolume(vol: Float) { bgPlayer?.setVolume(vol, vol) }

    fun release() {
        tts?.stop(); tts?.shutdown()
        bgPlayer?.release()
        try { toneHigh.release() } catch (_: Exception) {}
        try { toneMed.release()  } catch (_: Exception) {}
    }
}
