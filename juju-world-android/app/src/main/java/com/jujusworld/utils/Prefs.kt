package com.jujusworld.utils

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("juju_prefs", Context.MODE_PRIVATE)
    }

    var parentPin: String
        get() = prefs.getString("parent_pin", "") ?: ""
        set(v) = prefs.edit().putString("parent_pin", v).apply()

    var stars: Int
        get() = prefs.getInt("stars", 0)
        set(v) = prefs.edit().putInt("stars", v).apply()

    fun addStars(n: Int = 1) { stars += n }

    fun getSectionVisible(section: String): Boolean =
        prefs.getBoolean("visible_$section", true)

    fun setSectionVisible(section: String, visible: Boolean) =
        prefs.edit().putBoolean("visible_$section", visible).apply()

    // ── Screen time tracking ─────────────────────────────────────────────────

    /** Call when the app comes to foreground. */
    fun startSession() {
        prefs.edit().putLong("session_start", System.currentTimeMillis()).apply()
    }

    /** Call when the app goes to background or is closed. Accumulates today's total. */
    fun endSession() {
        val start = prefs.getLong("session_start", 0L)
        if (start == 0L) return
        val elapsed = System.currentTimeMillis() - start
        val todayKey = "screen_time_${todayKey()}"
        val prev = prefs.getLong(todayKey, 0L)
        prefs.edit()
            .putLong(todayKey, prev + elapsed)
            .putLong("session_start", 0L)
            .apply()
    }

    /** Today's total on-screen milliseconds. */
    val todayScreenTimeMs: Long
        get() {
            val base = prefs.getLong("screen_time_${todayKey()}", 0L)
            val sessionStart = prefs.getLong("session_start", 0L)
            val inProgress = if (sessionStart > 0L) System.currentTimeMillis() - sessionStart else 0L
            return base + inProgress
        }

    /** Per-section time in ms. */
    fun getSectionTimeMs(section: String): Long =
        prefs.getLong("section_time_$section", 0L)

    fun addSectionTime(section: String, ms: Long) {
        val prev = prefs.getLong("section_time_$section", 0L)
        prefs.edit().putLong("section_time_$section", prev + ms).apply()
    }

    private fun todayKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "${cal.get(java.util.Calendar.YEAR)}_${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
    }
}
