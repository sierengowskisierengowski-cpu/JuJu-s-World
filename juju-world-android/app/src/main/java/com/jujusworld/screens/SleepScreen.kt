package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun SleepScreen(navController: NavController) {
    val lullabies  = listOf("⭐ Twinkle Twinkle","🌙 Brahms Lullaby","🌛 Rock a Bye Baby","🤫 Hush Little Baby","☀️ You Are My Sunshine")
    val noises     = listOf("🌧️ Rain","🌊 Ocean","💓 Heartbeat","🌀 Fan","🌲 Forest","🦗 Crickets")
    val timerMins  = listOf(15, 30, 45, 60)

    var playingLullaby by remember { mutableIntStateOf(-1) }
    var activeNoise    by remember { mutableIntStateOf(-1) }
    var timerSecs      by remember { mutableIntStateOf(0) }
    var timerActive    by remember { mutableStateOf(false) }
    var blackScreen    by remember { mutableStateOf(false) }
    var shhMessage     by remember { mutableStateOf(false) }

    // Breathing guide
    val breathPhase  = listOf("Breathe In... 🌬️","Hold... ✨","Breathe Out... 😌")
    var breathIdx by remember { mutableIntStateOf(0) }
    val breathDurations = listOf(4000, 2000, 4000)

    LaunchedEffect(Unit) {
        while (true) {
            delay(breathDurations[breathIdx].toLong())
            breathIdx = (breathIdx + 1) % 3
        }
    }

    // Timer countdown
    LaunchedEffect(timerActive) {
        if (!timerActive) return@LaunchedEffect
        while (timerSecs > 0) {
            delay(1000)
            timerSecs--
        }
        blackScreen = true
        timerActive = false
    }

    // Shh message auto-hide
    LaunchedEffect(shhMessage) {
        if (shhMessage) {
            delay(2000)
            shhMessage = false
            blackScreen = true
        }
    }

    val inf = rememberInfiniteTransition(label = "sleep")

    // Stars
    val tw1 by inf.animateFloat(0.1f, 0.9f, infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse), label = "tw1")
    val tw2 by inf.animateFloat(0.4f, 1f, infiniteRepeatable(tween(1300, easing = EaseInOutSine), RepeatMode.Reverse, StartOffset(450)), label = "tw2")
    // Moon halo pulse
    val moonHalo by inf.animateFloat(0.5f, 0.9f, infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse), label = "mh")
    // Cloud drift
    val cloudX by inf.animateFloat(-0.2f, 1.1f, infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart), label = "cx")
    // ZZZ float
    val zzzY1 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart), label = "z1")
    val zzzY2 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart, StartOffset(800)), label = "z2")
    // Breathing circle
    val breathScale by animateFloatAsState(
        when (breathIdx) { 0 -> 1.4f; 1 -> 1.4f; else -> 1f },
        tween(breathDurations.getOrElse(breathIdx) { 4000 }), label = "bs"
    )
    val breathColor by animateColorAsState(
        when (breathIdx) { 0 -> Color(0xFF3B82F6); 1 -> Color(0xFF8B5CF6); else -> Color(0xFFF43F5E) },
        tween(breathDurations.getOrElse(breathIdx) { 4000 }), label = "bc"
    )

    if (blackScreen) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)
            .clickable { shhMessage = true; blackScreen = false }) {
            if (shhMessage) {
                Text("Shhh 🌙\nSweet dreams JuJu...",
                    fontSize = 28.sp, color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center))
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Midnight sky background ───────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0C0A1A), Color(0xFF111827)))
        ))
        // Stars
        for (i in 0 until 80) {
            val sx = (i * 89.3f) % 100f; val sy = (i * 43.7f) % 90f
            val sz = (4 + i % 7).sp
            val tw = if (i % 2 == 0) tw1 else tw2
            Text("✦", fontSize = sz, color = Color.White,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (sx * 3.6f).dp, top = (sy * 6f).dp).alpha(tw * 0.8f))
        }
        // Clouds
        Text("☁️", fontSize = 48.sp,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                .padding(start = (cloudX * 400 - 60).dp, top = 80.dp).alpha(0.15f))
        // Moon with halo
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = 40.dp, top = 60.dp)) {
            Box(modifier = Modifier.size(90.dp).scale(moonHalo + 0.15f).clip(CircleShape)
                .background(Color(0x22FDE68A)))
            Text("🌙", fontSize = 56.sp, modifier = Modifier.align(Alignment.Center))
        }
        // ZZZ
        Text("z", fontSize = 20.sp, color = Color(0xFFE9D5FF),
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 10.dp, top = (zzzY1 * 80 + 60).dp)
                .alpha(1f - zzzY1))
        Text("z", fontSize = 14.sp, color = Color(0xFFC4B5FD),
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 28.dp, top = (zzzY2 * 70 + 50).dp)
                .alpha(1f - zzzY2))

        // Goodnight message
        Text("Goodnight JuJu 🌙\nYou are so loved.\nSweet dreams baby girl. 💖",
            fontSize = 16.sp, color = Color(0x88E9D5FF), textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp))

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF818CF8))
                }
                Text("🌙 Sleep Time", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Left column: Breathing + Timer ────────────────────────
                Column(modifier = Modifier.weight(0.45f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Breathing guide
                    Text("Breathing Guide 🌬️", fontSize = 13.sp, color = Color(0xFF818CF8),
                        fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(160.dp).scale(breathScale).clip(CircleShape)
                            .background(breathColor.copy(alpha = 0.25f)))
                        Box(modifier = Modifier.size(120.dp).scale(breathScale).clip(CircleShape)
                            .background(breathColor.copy(alpha = 0.4f)))
                        Text(breathPhase[breathIdx], fontSize = 13.sp, color = Color.White,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(12.dp))
                    }

                    // Sleep timer
                    Text("Sleep Timer 🌙", fontSize = 13.sp, color = Color(0xFF818CF8),
                        fontWeight = FontWeight.Bold)
                    if (timerActive) {
                        Text(
                            "${timerSecs / 60}:${(timerSecs % 60).toString().padStart(2,'0')} ⏳",
                            fontSize = 22.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                        Button(onClick = { timerActive = false; timerSecs = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E))) {
                            Text("Cancel", color = Color.White)
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            timerMins.forEach { m ->
                                Button(onClick = {
                                    timerSecs = m * 60; timerActive = true; SoundManager.playTap()
                                },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x44818CF8)),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)) {
                                    Text("$m\nmin", fontSize = 11.sp, color = Color.White,
                                        textAlign = TextAlign.Center, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }

                // ── Right column: Lullabies + White Noise ─────────────────
                Column(modifier = Modifier.weight(0.55f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    Text("Lullabies 🎵", fontSize = 13.sp, color = Color(0xFF818CF8),
                        fontWeight = FontWeight.Bold)
                    lullabies.forEachIndexed { i, title ->
                        Row(modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (playingLullaby == i) Color(0x44818CF8) else Color(0x22FFFFFF))
                            .clickable {
                                playingLullaby = if (playingLullaby == i) -1 else i
                                SoundManager.playTap()
                                if (playingLullaby == i) SoundManager.speak(title.drop(3))
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (playingLullaby == i) "⏸" else "▶", fontSize = 16.sp, color = Color(0xFF818CF8))
                            Spacer(Modifier.width(8.dp))
                            Text(title, fontSize = 13.sp, color = Color.White, modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text("White Noise 🌿", fontSize = 13.sp, color = Color(0xFF818CF8),
                        fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        noises.forEachIndexed { i, noise ->
                            Box(modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (activeNoise == i) Color(0x66818CF8) else Color(0x22FFFFFF))
                                .border(if (activeNoise == i) 1.dp else 0.dp, Color(0xFF818CF8), RoundedCornerShape(16.dp))
                                .clickable {
                                    activeNoise = if (activeNoise == i) -1 else i
                                    SoundManager.playTap()
                                    if (activeNoise == i) SoundManager.speak(noise)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(noise, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}
