package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

data class Song(val emoji: String, val title: String, val artist: String, val assetPath: String)

@Composable
fun MusicScreen(navController: NavController) {
    val context = LocalContext.current
    var playingIdx by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }

    val songs = listOf(
        Song("🌙", "Twinkle Twinkle",     "JuJu's Lullabies", "audio/lullabies/twinkle.mp3"),
        Song("⭐", "Star Light Star Bright","JuJu's Lullabies", "audio/lullabies/starlightbright.mp3"),
        Song("🌊", "Ocean Waves",           "White Noise",       "audio/lullabies/ocean.mp3"),
        Song("🌧", "Gentle Rain",           "White Noise",       "audio/lullabies/rain.mp3"),
        Song("🐑", "Baa Baa Black Sheep",   "Nursery Rhymes",    "audio/lullabies/baabaa.mp3"),
        Song("🎵", "Hush Little Baby",      "JuJu's Lullabies", "audio/lullabies/hush.mp3"),
        Song("🌸", "Lavender's Blue",       "JuJu's Lullabies", "audio/lullabies/lavender.mp3"),
        Song("🔔", "Jingle Bells (Soft)",   "Fun Songs",         "audio/lullabies/jingle.mp3"),
    )

    val inf = rememberInfiniteTransition(label = "disc")
    val rotation by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "rot")

    DisposableEffect(Unit) {
        onDispose { SoundManager.stopBgMusic() }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF7C2D12), Color(0xFF1E1B4B)))
        )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🎵  Music", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Now playing
            if (playingIdx >= 0) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x44FFFFFF))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(songs[playingIdx].emoji, fontSize = 48.sp,
                            modifier = if (isPlaying) Modifier.rotate(rotation) else Modifier)
                        Column(Modifier.weight(1f)) {
                            Text("Now Playing", fontSize = 12.sp, color = Color(0xFFFBBF24))
                            Text(songs[playingIdx].title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(songs[playingIdx].artist, fontSize = 13.sp, color = Color(0xFFE9D5FF))
                        }
                        IconButton(onClick = {
                            isPlaying = !isPlaying
                            if (!isPlaying) SoundManager.stopBgMusic()
                            else SoundManager.playBgMusic(context, songs[playingIdx].assetPath)
                        }) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                "Play/Pause", tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                itemsIndexed(songs) { idx, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (playingIdx == idx) Color(0x66EC4899) else Color(0x22FFFFFF)
                            )
                            .clickable {
                                playingIdx = idx
                                isPlaying = true
                                SoundManager.stopBgMusic()
                                SoundManager.playBgMusic(context, song.assetPath)
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(song.emoji, fontSize = 32.sp)
                        Column(Modifier.weight(1f)) {
                            Text(song.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(song.artist, fontSize = 12.sp, color = Color(0xFFE9D5FF))
                        }
                        if (playingIdx == idx && isPlaying) {
                            Text("▶", fontSize = 18.sp, color = Color(0xFFEC4899))
                        }
                    }
                }
            }
        }
    }
}
