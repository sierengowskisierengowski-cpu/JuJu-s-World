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

    var sessionStart: Long
        get() = prefs.getLong("session_start", System.currentTimeMillis())
        set(v) = prefs.edit().putLong("session_start", v).apply()

    fun getSectionVisible(section: String): Boolean =
        prefs.getBoolean("visible_$section", true)

    fun setSectionVisible(section: String, visible: Boolean) =
        prefs.edit().putBoolean("visible_$section", visible).apply()

    fun addStars(n: Int = 1) { stars += n }
}
