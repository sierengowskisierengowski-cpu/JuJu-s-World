package com.jujusworld.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun SleepScreen(navController: NavController) {
    val context = LocalContext.current
    var breathing by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("Breathe In") }

    val inf = rememberInfiniteTransition(label = "stars")
    val starOpacity by inf.animateFloat(0.4f, 1f,
        infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse), label = "so")
    val moonFloat by inf.animateFloat(0f, -8f,
        infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse), label = "mf")

    val breatheScale by animateFloatAsState(
        if (phase == "Breathe In") 1.5f else 1f,
        tween(4000), label = "bs"
    )

    LaunchedEffect(breathing) {
        if (breathing) {
            while (breathing) {
                phase = "Breathe In"
                delay(4000)
                phase = "Hold"
                delay(2000)
                phase = "Breathe Out"
                delay(4000)
                phase = "Rest"
                delay(2000)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { SoundManager.stopBgMusic() }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E)))
        )
    ) {
        // Stars
        val stars = remember { List(60) { Triple((it * 137.5) % 100, (it * 83.7) % 100, (it % 3 + 1) * 6) } }
        stars.forEach { (x, y, size) ->
            Text("✦", fontSize = size.dp.value.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .padding(start = (x * 3.8).dp, top = (y * 6.5).dp)
                    .alpha(starOpacity))
        }

        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🌙  Sleep Time", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.weight(1f))

            Text("🌙", fontSize = 80.sp, modifier = Modifier.offset(y = moonFloat.dp))
            Spacer(Modifier.height(12.dp))
            Text("Sweet Dreams, JuJu", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = Color.White, textAlign = TextAlign.Center)
            Text("Time to rest your sparkly eyes ✨", fontSize = 16.sp, color = Color(0xFFA78BFA),
                textAlign = TextAlign.Center)

            Spacer(Modifier.height(32.dp))

            // Breathing guide
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                Box(modifier = Modifier.size(140.dp).scale(breatheScale)
                    .background(Color(0x334F46E5), shape = androidx.compose.foundation.shape.CircleShape))
                Box(modifier = Modifier.size(100.dp).scale(breatheScale * 0.9f)
                    .background(Color(0x668B5CF6), shape = androidx.compose.foundation.shape.CircleShape))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌬", fontSize = 28.sp)
                    if (breathing) Text(phase, fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { breathing = !breathing },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (breathing) Color(0xFF7C3AED) else Color(0xFF4F46E5)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(if (breathing) "⏹ Stop Breathing Guide" else "🫁 Start Breathing Guide",
                    fontSize = 16.sp, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            // Lullaby buttons
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { SoundManager.playBgMusic(context, "audio/lullabies/twinkle.mp3") },
                    border = BorderStroke(1.dp, Color(0xFF8B5CF6))
                ) { Text("🎵 Lullaby", color = Color.White) }
                OutlinedButton(
                    onClick = { SoundManager.playBgMusic(context, "audio/lullabies/ocean.mp3") },
                    border = BorderStroke(1.dp, Color(0xFF8B5CF6))
                ) { Text("🌊 Ocean", color = Color.White) }
                OutlinedButton(
                    onClick = { SoundManager.stopBgMusic() },
                    border = BorderStroke(1.dp, Color(0xFF6B7280))
                ) { Text("🔇 Stop", color = Color.White) }
            }

            Spacer(Modifier.weight(1f))

            Text("⭐ Goodnight ⭐", fontSize = 14.sp, color = Color(0xFF6D28D9),
                modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}
