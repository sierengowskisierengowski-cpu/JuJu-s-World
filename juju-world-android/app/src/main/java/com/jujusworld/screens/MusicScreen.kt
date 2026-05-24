package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import kotlin.math.*

data class Track(val emoji: String, val title: String)

@Composable
fun MusicScreen(navController: NavController) {
    val tracks = listOf(
        Track("⭐","Twinkle Twinkle"),
        Track("🦈","Baby Shark"),
        Track("🚌","Wheels on the Bus"),
        Track("🐄","Old MacDonald"),
        Track("😊","If You're Happy"),
        Track("🔤","ABC Song"),
        Track("🕷️","Itsy Bitsy Spider"),
    )
    var playingIdx  by remember { mutableIntStateOf(0) }
    var isPlaying   by remember { mutableStateOf(false) }

    val inf = rememberInfiniteTransition(label = "music")

    // Vinyl spin
    val vinylRot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(if (isPlaying) 3000 else 20000, easing = LinearEasing)), label = "vr")

    // Visualizer bars — 24 bars
    val barHeights = (0 until 24).map { i ->
        inf.animateFloat(0.2f, 1f,
            infiniteRepeatable(
                tween((200 + i * 30) % 400 + 150, easing = EaseInOutSine),
                RepeatMode.Reverse,
                StartOffset(i * 60)
            ), label = "bar$i"
        ).value * (if (isPlaying) 1f else 0.15f)
    }

    // Dancer bounce
    val dancers = listOf("🐻","🦊","🐸","🦄")
    val dancerBounces = dancers.mapIndexed { i, _ ->
        inf.animateFloat(0f, -18f,
            infiniteRepeatable(tween(300 + i * 50, easing = EaseInOutSine), RepeatMode.Reverse,
                StartOffset(i * 80)), label = "db$i").value * (if (isPlaying) 1f else 0f)
    }

    // Stage light sweep
    val sweep by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "sw")

    // Note float
    val noteY by inf.animateFloat(0f, -60f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart), label = "ny")

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Concert stage background ──────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF030712), Color(0xFF0C0020), Color(0xFF1A0533)))
        ))
        // Sweeping colored light beams
        listOf(Color(0x22EC4899), Color(0x228B5CF6), Color(0x2206B6D4), Color(0x22F59E0B))
            .forEachIndexed { i, col ->
                Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f)
                    .rotate(sweep + i * 90f)
                    .align(Alignment.TopCenter)
                    .background(Brush.verticalGradient(listOf(col, Color.Transparent))))
            }
        // Floating music notes
        if (isPlaying) {
            listOf("🎵","🎶","♪","♫").forEachIndexed { i, n ->
                Text(n, fontSize = 20.sp,
                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                        .padding(start = (50 + i * 70f).dp, top = (noteY + i * 40f).dp)
                        .alpha(if (noteY < -40) 0f else 1f))
            }
        }

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🎵 JuJu's Music", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Left: Vinyl + controls + dancers ─────────────────────
                Column(modifier = Modifier.weight(0.45f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Now playing
                    Text("Now Playing", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    Text(tracks[playingIdx].title, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24), textAlign = TextAlign.Center)

                    // Vinyl record
                    Box(modifier = Modifier.size(120.dp).rotate(vinylRot)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460)))),
                        contentAlignment = Alignment.Center) {
                        Text(tracks[playingIdx].emoji, fontSize = 36.sp)
                    }

                    // Play/Pause
                    Box(modifier = Modifier.size(64.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(if (isPlaying) Color(0xFFEC4899) else Color(0xFF8B5CF6))
                        .clickable {
                            isPlaying = !isPlaying
                            SoundManager.playTap()
                            if (isPlaying) SoundManager.speak("Now playing ${tracks[playingIdx].title}")
                        },
                        contentAlignment = Alignment.Center) {
                        Text(if (isPlaying) "⏸" else "▶", fontSize = 28.sp)
                    }

                    // Prev / Next
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            playingIdx = (playingIdx - 1 + tracks.size) % tracks.size
                            SoundManager.playTap()
                            if (isPlaying) SoundManager.speak(tracks[playingIdx].title)
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0x44FFFFFF))) {
                            Text("⏮", fontSize = 20.sp)
                        }
                        Button(onClick = {
                            playingIdx = (playingIdx + 1) % tracks.size
                            SoundManager.playTap()
                            if (isPlaying) SoundManager.speak(tracks[playingIdx].title)
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0x44FFFFFF))) {
                            Text("⏭", fontSize = 20.sp)
                        }
                    }

                    // Dancing emoji characters
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        dancers.forEachIndexed { i, d ->
                            Text(d, fontSize = 28.sp,
                                modifier = Modifier.offset(y = dancerBounces.getOrElse(i) { 0f }.dp))
                        }
                    }
                }

                // ── Right: Visualizer + playlist ─────────────────────────
                Column(modifier = Modifier.weight(0.55f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Rainbow visualizer
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22FFFFFF)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(6.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            barHeights.forEachIndexed { i, h ->
                                val hue = (i.toFloat() / barHeights.size) * 360f
                                Box(modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(Color.hsv(hue, 0.8f, 1f)))
                            }
                        }
                    }

                    // Playlist
                    Text("Playlist 🎶", fontSize = 14.sp, color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Bold)
                    tracks.forEachIndexed { i, track ->
                        Row(modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (i == playingIdx) Color(0x44EC4899) else Color(0x22FFFFFF))
                            .clickable {
                                playingIdx = i
                                isPlaying = true
                                SoundManager.playTap()
                                SoundManager.speak(track.title)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(track.emoji, fontSize = 20.sp)
                            Text(track.title, fontSize = 14.sp, color = Color.White,
                                fontWeight = if (i == playingIdx) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f))
                            if (i == playingIdx && isPlaying) Text("♪", fontSize = 16.sp, color = Color(0xFFEC4899))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
