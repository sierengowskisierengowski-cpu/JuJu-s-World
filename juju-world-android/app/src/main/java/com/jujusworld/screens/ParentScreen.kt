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
import androidx.compose.ui.graphics.*
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
            onTapLock = { tapCount++; if (tapCount >= 5 && Prefs.parentPin.isEmpty()) unlocked = true },
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

@Composable
private fun PinGate(
    tapCount: Int, enteredPin: String, error: String,
    onTapLock: () -> Unit, onPinChange: (String) -> Unit,
    onSubmit: () -> Unit, onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827)))),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🔒", fontSize = 64.sp, modifier = Modifier.clickable { onTapLock() })
            Spacer(Modifier.height(8.dp))
            Text("Parent Zone", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Enter your PIN to continue", fontSize = 15.sp, color = Color(0xFF9CA3AF))
            if (Prefs.parentPin.isEmpty())
                Text("Tip: tap the lock 5× to unlock without PIN", fontSize = 11.sp, color = Color(0xFF4B5563))
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(value = enteredPin,
                onValueChange = { if (it.length <= 4) onPinChange(it) },
                label = { Text("4-digit PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8B5CF6), unfocusedBorderColor = Color(0xFF4B5563),
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF8B5CF6), focusedLabelColor = Color(0xFF8B5CF6)))
            if (error.isNotEmpty()) Text(error, color = Color(0xFFEF4444), fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                Text("Unlock", color = Color.White, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack) { Text("← Back to JuJu's World", color = Color(0xFF9CA3AF)) }
        }
    }
}

@Composable
private fun ParentDashboard(context: Context, navController: NavController) {
    val sections = listOf("Shows","Games","Books","Music","Art","Camera","Browser","App Store","Sleep")
    var sectionVis by remember {
        mutableStateOf(sections.associateWith { Prefs.getSectionVisible(it) })
    }

    // PIN change flow
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput     by remember { mutableStateOf("") }
    var confirmInput    by remember { mutableStateOf("") }
    var pinStep         by remember { mutableIntStateOf(0) } // 0=idle, 1=current, 2=new, 3=confirm
    var pinMsg          by remember { mutableStateOf("") }

    // Kiosk
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val isDeviceOwner = dpm.isDeviceOwnerApp(context.packageName)
    var kioskActive by remember {
        mutableStateOf(activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE)
    }

    // Screen time
    val todayMs = Prefs.todayScreenTimeMs
    val todayMin = todayMs / 60_000
    val todayHr  = todayMin / 60
    val todayMinRem = todayMin % 60

    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827))))) {

        // Subtle star field
        for (i in 0 until 30) {
            val sx = (i * 97f) % 100f; val sy = (i * 63f) % 100f
            androidx.compose.material3.Text("✦", fontSize = (4 + i % 5).sp,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (sx * 3.6f).dp, top = (sy * 6f).dp))
        }

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()
            .verticalScroll(rememberScrollState())) {

            // Header
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🔧  Parent Zone", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // ── Screen Time ──────────────────────────────────────────────────
            PCard(Color(0xFF1E1B4B)) {
                Text("📊 Screen Time Today", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                Spacer(Modifier.height(6.dp))
                Text(if (todayHr > 0) "${todayHr}h ${todayMinRem}m" else "${todayMin}m",
                    fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("By section:", fontSize = 13.sp, color = Color(0xFF9CA3AF))
                sections.forEach { sec ->
                    val sMs = Prefs.getSectionTimeMs(sec)
                    if (sMs > 0) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text(sec, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
                            Text("${sMs / 60_000}m", fontSize = 13.sp, color = Color(0xFF9CA3AF))
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            // ── Kiosk mode ───────────────────────────────────────────────────
            PCard(if (kioskActive) Color(0xFF14532D) else Color(0xFF1C1917)) {
                Text(if (isDeviceOwner) "🔒 Kiosk Mode (Active ✓)" else "🔒 Kiosk Mode",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (!isDeviceOwner)
                    Text("Enable with:\nadb shell dpm set-device-owner com.jujusworld/.AdminReceiver",
                        fontSize = 11.sp, color = Color(0xFF9CA3AF), lineHeight = 15.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (kioskActive) "ON — JuJu is locked in 🔐" else "OFF",
                        fontSize = 15.sp, color = if (kioskActive) Color(0xFF34D399) else Color(0xFF9CA3AF),
                        modifier = Modifier.weight(1f))
                    Switch(checked = kioskActive, enabled = isDeviceOwner,
                        onCheckedChange = { on ->
                            kioskActive = on; SoundManager.playTap()
                            if (on) (context as? Activity)?.startLockTask()
                            else    (context as? Activity)?.stopLockTask()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF34D399),
                            checkedTrackColor = Color(0xFF065F46),
                            uncheckedThumbColor = Color(0xFF6B7280)))
                }
                if (kioskActive) {
                    Spacer(Modifier.height(6.dp))
                    Button(onClick = { (context as? Activity)?.stopLockTask(); kioskActive = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E))) {
                        Text("🚨 Emergency Exit", color = Color.White)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            // ── Section visibility ───────────────────────────────────────────
            PSectionLabel("Section Visibility")
            sections.forEach { section ->
                val visible = sectionVis[section] ?: true
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x22FFFFFF))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(section, fontSize = 15.sp, color = Color.White, modifier = Modifier.weight(1f))
                    Switch(checked = visible,
                        onCheckedChange = { checked ->
                            sectionVis = sectionVis + (section to checked)
                            Prefs.setSectionVisible(section, checked)
                            SoundManager.playTap()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFEC4899)))
                }
            }
            Spacer(Modifier.height(10.dp))

            // ── PIN change ───────────────────────────────────────────────────
            PSectionLabel("Change PIN")
            PCard(Color(0xFF111827)) {
                when (pinStep) {
                    0 -> {
                        Button(onClick = {
                            pinStep = if (Prefs.parentPin.isEmpty()) 2 else 1
                            pinMsg = ""; currentPinInput = ""
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                            modifier = Modifier.fillMaxWidth()) {
                            Text("Change PIN 🔑", color = Color.White)
                        }
                    }
                    1 -> PinField("Current PIN", currentPinInput, { if (it.length <= 4) currentPinInput = it },
                        "Verify",
                        { if (currentPinInput == Prefs.parentPin) { pinStep = 2; pinMsg = "" }
                          else { pinMsg = "Wrong PIN"; currentPinInput = "" } })
                    2 -> PinField("New PIN (4 digits)", newPinInput, { if (it.length <= 4) newPinInput = it },
                        "Next", { if (newPinInput.length == 4) { pinStep = 3; pinMsg = "" }
                                  else pinMsg = "Must be 4 digits" })
                    3 -> PinField("Confirm new PIN", confirmInput, { if (it.length <= 4) confirmInput = it },
                        "Save",
                        { if (confirmInput == newPinInput) {
                            Prefs.parentPin = newPinInput
                            pinMsg = "✓ PIN saved!"; pinStep = 0
                            newPinInput = ""; confirmInput = ""
                            SoundManager.playSuccess()
                          } else { pinMsg = "PINs don't match"; confirmInput = "" }
                        })
                }
                if (pinMsg.isNotEmpty())
                    Text(pinMsg, fontSize = 13.sp,
                        color = if (pinMsg.startsWith("✓")) Color(0xFF34D399) else Color(0xFFF43F5E),
                        modifier = Modifier.padding(top = 6.dp))
            }

            // ── Stats ────────────────────────────────────────────────────────
            Spacer(Modifier.height(10.dp))
            PCard(Color(0xFF1E1B4B)) {
                Text("JuJu's Progress ⭐", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                Spacer(Modifier.height(6.dp))
                Text("Stars earned: ${Prefs.stars} ⭐", fontSize = 15.sp, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Button(onClick = { Prefs.stars = 0; SoundManager.playTap() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x44FFFFFF)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("Reset Stars", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PinField(label: String, value: String, onValueChange: (String) -> Unit,
                     buttonLabel: String, onSubmit: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = value, onValueChange = onValueChange,
            label = { Text(label) }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B5CF6), unfocusedBorderColor = Color(0xFF4B5563),
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedLabelColor = Color(0xFF8B5CF6)))
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
            Text(buttonLabel, color = Color.White)
        }
    }
}

@Composable
private fun PCard(bg: Color, content: @Composable ColumnScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(16.dp)).background(bg).padding(16.dp)) {
        Column(content = content)
    }
}

@Composable
private fun PSectionLabel(text: String) {
    Text("  $text", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
}
