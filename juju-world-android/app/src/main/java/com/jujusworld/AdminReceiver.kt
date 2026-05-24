package com.jujusworld

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * DeviceAdminReceiver for JuJu's World kiosk / lock-task mode.
 *
 * --- ONE-TIME SETUP (run once from your computer) ---
 * With the tablet connected via ADB, run:
 *
 *   adb shell dpm set-device-owner com.jujusworld/.AdminReceiver
 *
 * After that, the "Kiosk Mode" toggle in Parent Zone will call
 * startLockTask() / stopLockTask() with no system overlay.
 * JuJu will be fully in JuJu's World — the Home button, Recents,
 * and the status bar become inaccessible until a parent exits kiosk mode.
 *
 * To remove device owner (e.g. to factory-reset or uninstall):
 *   adb shell dpm remove-active-admin com.jujusworld/.AdminReceiver
 */
class AdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(
            context,
            "JuJu's World is now the device owner! Kiosk mode ready.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(
            context,
            "JuJu's World device admin disabled.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        Toast.makeText(context, "🔒 Kiosk mode ON — JuJu is locked in!", Toast.LENGTH_SHORT).show()
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        Toast.makeText(context, "🔓 Kiosk mode OFF", Toast.LENGTH_SHORT).show()
    }
}
