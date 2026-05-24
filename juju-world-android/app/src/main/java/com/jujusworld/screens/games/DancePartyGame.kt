package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager

@Composable
fun DancePartyGame(navController: NavController) {
    val dancers = listOf("🦄","🐻","🦊","🐸","🌈","⭐","🦋","💃")
    val inf = rememberInfiniteTransition(label = "dp")

    val rotations = dancers.mapIndexed { i, _ ->
        inf.animateFloat(-20f, 20f,
            infiniteRepeatable(tween(400 + i * 50, easing = EaseInOutSine), RepeatMode.Reverse,
                StartOffset(i * 120)), label = "r$i").value
    }
    val scales = dancers.mapIndexed { i, _ ->
        inf.animateFloat(0.85f, 1.15f,
            infiniteRepeatable(tween(350 + i * 40, easing = EaseInOutSine), RepeatMode.Reverse,
                StartOffset(i * 90)), label = "s$i").value
    }
    val lightSweep by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "ls")
    val beatPulse by inf.animateFloat(0.95f, 1.05f,
        infiniteRepeatable(tween(400, easing = EaseInOutSine), RepeatMode.Reverse), label = "bp")

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF0C0014), Color(0xFF1A0533), Color(0xFF2D0A5C)))
    )) {
        // Sweeping light beams
        listOf(Color(0x33EC4899), Color(0x338B5CF6), Color(0x3306B6D4), Color(0x33F59E0B))
            .forEachIndexed { i, col ->
                Box(modifier = Modifier.fillMaxSize().rotate(lightSweep + i * 90f)
                    .background(Brush.verticalGradient(listOf(col, Color.Transparent))))
            }

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("💃 Dance Party!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("🎵 Get up and dance with JuJu! 🎵",
                fontSize = 18.sp, color = Color(0xFFFBBF24), textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().scale(beatPulse))
            Spacer(Modifier.height(16.dp))
            // Dance floor
            Box(modifier = Modifier.fillMaxWidth().weight(1f)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x22FFFFFF))
            ) {
                // Dancers arranged in two rows
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()) {
                        dancers.take(4).forEachIndexed { i, d ->
                            Text(d, fontSize = 56.sp,
                                modifier = Modifier.rotate(rotations.getOrElse(i){0f}).scale(scales.getOrElse(i){1f}))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()) {
                        dancers.drop(4).forEachIndexed { i, d ->
                            Text(d, fontSize = 56.sp,
                                modifier = Modifier.rotate(rotations.getOrElse(i+4){0f}).scale(scales.getOrElse(i+4){1f}))
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("🌟 ✨ 🌟 ✨ 🌟", fontSize = 28.sp,
                modifier = Modifier.scale(beatPulse))
            Spacer(Modifier.height(16.dp))
        }
    }
}
