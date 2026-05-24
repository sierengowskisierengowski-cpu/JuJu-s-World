package com.jujusworld.screens.games

import android.media.ToneGenerator
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager

@Composable
fun PianoGame(navController: NavController) {
    val keys = listOf(
        Triple("Do 🎵", Color(0xFFEC4899), ToneGenerator.TONE_DTMF_1),
        Triple("Re 🎶", Color(0xFF8B5CF6), ToneGenerator.TONE_DTMF_2),
        Triple("Mi ✨", Color(0xFF06B6D4), ToneGenerator.TONE_DTMF_3),
        Triple("Fa 🌟", Color(0xFF34D399), ToneGenerator.TONE_DTMF_4),
        Triple("Sol 🌈", Color(0xFFF59E0B), ToneGenerator.TONE_DTMF_5),
        Triple("La 🎀", Color(0xFFF43F5E), ToneGenerator.TONE_DTMF_6),
        Triple("Si 💖", Color(0xFFA78BFA), ToneGenerator.TONE_DTMF_7),
        Triple("Do' ⭐", Color(0xFFEC4899), ToneGenerator.TONE_DTMF_8),
    )
    var lastNote by remember { mutableStateOf("Tap a key! 🎹") }
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 90) }
    DisposableEffect(Unit) { onDispose { toneGen.release() } }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF0F0524), Color(0xFF1A0A2E), Color(0xFF2D1B69)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🎹 JuJu's Piano!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text(lastNote, fontSize = 24.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(12.dp))
            Spacer(Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                keys.forEach { (label, color, tone) ->
                    Box(modifier = Modifier
                        .weight(1f).height(200.dp)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.7f))))
                        .border(2.dp, Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                toneGen.startTone(tone, 300)
                                lastNote = label
                            })
                        },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }
        }
    }
}
