package com.jujusworld.screens

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.AdminReceiver
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager

@Composable
fun ParentScreen(navController: NavController) {
    val context = LocalContext.current
    var unlocked by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var tapCount by remember { mutableIntStateOf(0) }

    if (!unlocked) {
        PinGate(
            tapCount = tapCount,
            enteredPin = enteredPin,
            error = error,
            onTapLock = {
                tapCount++
                if (tapCount >= 5 && Prefs.parentPin.isEmpty()) unlocked = true
            },
            onPinChange = { enteredPin = it },
            onSubmit = {
                val saved = Prefs.parentPin
                if (saved.isEmpty() || enteredPin == saved) { unlocked = true; error = "" }
                else { error = "Wrong PIN. Try again."; enteredPin = "" }
            },
            onBack = { navController.popBackStack() }
        )
        return
    }

    ParentDashboard(context = context, navController = navController)
}

// ─────────────────────────────────────────────────────────────────────────────
// PIN gate
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PinGate(
    tapCount: Int,
    enteredPin: String,
    error: String,
    onTapLock: () -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🔒", fontSize = 64.sp,
                modifier = Modifier.clickable { onTapLock() })
            Spacer(Modifier.height(8.dp))
            Text("Parent Zone", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Enter your PIN to continue", fontSize = 15.sp, color = Color(0xFF9CA3AF))
            if (Prefs.parentPin.isEmpty())
                Text("Tip: tap the lock 5× to unlock without PIN", fontSize = 11.sp, color = Color(0xFF4B5563))
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = enteredPin,
                onValueChange = { if (it.length <= 4) onPinChange(it) },
                label = { Text("4-digit PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color(0xFF4B5563),
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White,
                    cursorColor          = Color(0xFF8B5CF6),
                    focusedLabelColor    = Color(0xFF8B5CF6)
                )
            )
            if (error.isNotEmpty()) Text(error, color = Color(0xFFEF4444), fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                Text("Unlock", color = Color.White, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack) {
                Text("← Back to JuJu's World", color = Color(0xFF9CA3AF))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Unlocked dashboard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ParentDashboard(context: Context, navController: NavController) {
    var sections by remember {
        mutableStateOf(
            listOf("Shows", "Games", "Books", "Music", "Art", "Camera", "Browser", "App Store")
                .associateWith { Prefs.getSectionVisible(it) }
        )
    }
    var newPin by remember { mutableStateOf("") }
    var pinSaved by remember { mutableStateOf(false) }

    // Kiosk / lock-task state
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, AdminReceiver::class.java)
    val isDeviceOwner = dpm.isDeviceOwnerApp(context.packageName)

    var kioskActive by remember {
        mutableStateOf(activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE)
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827))))) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🔧  Parent Zone", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // ── Stats card ──────────────────────────────────────────────────
            Card16(Color(0xFF1E1B4B)) {
                Text("JuJu's Stats 📊", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                Spacer(Modifier.height(6.dp))
                Text("⭐ Stars Earned: ${Prefs.stars}", fontSize = 15.sp, color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            // ── Kiosk mode card ─────────────────────────────────────────────
            Card16(if (kioskActive) Color(0xFF14532D) else Color(0xFF1C1917)) {
                Text(
                    if (isDeviceOwner) "🔒 Kiosk Mode (Device Owner ✓)" else "🔒 Kiosk Mode",
                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (isDeviceOwner)
                        "When ON, JuJu cannot leave the app. Home and Recents are locked."
                    else
                        "Enable device owner first:\nadb shell dpm set-device-owner com.jujusworld/.AdminReceiver",
                    fontSize = 12.sp, color = Color(0xFF9CA3AF), lineHeight = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (kioskActive) "ON — JuJu is locked in" else "OFF",
                        fontSize = 15.sp, color = if (kioskActive) Color(0xFF34D399) else Color(0xFF9CA3AF),
                        modifier = Modifier.weight(1f))
                    Switch(
                        checked = kioskActive,
                        enabled = isDeviceOwner,
                        onCheckedChange = { on ->
                            kioskActive = on
                            SoundManager.playTap()
                            if (on) {
                                (context as? Activity)?.startLockTask()
                            } else {
                                (context as? Activity)?.stopLockTask()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor  = Color(0xFF34D399),
                            checkedTrackColor  = Color(0xFF065F46),
                            uncheckedThumbColor = Color(0xFF6B7280)
                        )
                    )
                }
                if (!isDeviceOwner) {
                    Spacer(Modifier.height(6.dp))
                    Text("⚠ Run the ADB command once to enable full kiosk mode.",
                        fontSize = 11.sp, color = Color(0xFFF59E0B))
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Section visibility ──────────────────────────────────────────
            SectionLabel("Section Visibility")
            sections.forEach { (section, visible) ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(section, fontSize = 15.sp, color = Color.White, modifier = Modifier.weight(1f))
                    Switch(
                        checked = visible,
                        onCheckedChange = { checked ->
                            sections = sections + (section to checked)
                            Prefs.setSectionVisible(section, checked)
                            SoundManager.playTap()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFEC4899))
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Change PIN ──────────────────────────────────────────────────
            SectionLabel("Change PIN")
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text("New 4-digit PIN") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8B5CF6), unfocusedBorderColor = Color(0xFF4B5563),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF8B5CF6)
                    )
                )
                Button(onClick = {
                    if (newPin.length == 4) {
                        Prefs.parentPin = newPin; pinSaved = true; newPin = ""
                        SoundManager.playSuccess()
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                    Text("Save")
                }
            }
            if (pinSaved) {
                Text("  ✓ PIN saved!", fontSize = 13.sp, color = Color(0xFF34D399),
                    modifier = Modifier.padding(horizontal = 16.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Card16(bg: Color, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp)).background(bg).padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        "  $text", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
