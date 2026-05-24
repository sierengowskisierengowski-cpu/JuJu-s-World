package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.jujusworld.ui.WinCelebration
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

data class Bubble(val id: Int, val x: Float, val y: Float, val size: Float, val color: Color, var popped: Boolean = false)

@Composable
fun BubblePopGame(navController: NavController) {
    var score by remember { mutableIntStateOf(0) }
    var showWin by remember { mutableStateOf(false) }
    val bubbleColors = listOf(
        Color(0xFF06B6D4), Color(0xFFEC4899), Color(0xFF8B5CF6),
        Color(0xFF34D399), Color(0xFFF59E0B), Color(0xFFF43F5E)
    )
    var bubbles by remember {
        mutableStateOf((0 until 16).map { i ->
            Bubble(i, ((i % 4) * 22f + 8f), ((i / 4) * 22f + 20f),
                (48f + (i % 3) * 16f), bubbleColors[i % bubbleColors.size])
        })
    }

    val inf = rememberInfiniteTransition(label = "bb")
    val wobble by inf.animateFloat(-3f, 3f,
        infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse), label = "w")

    LaunchedEffect(score) {
        if (score >= 16) {
            delay(300)
            showWin = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF0C4A6E), Color(0xFF0284C7), Color(0xFF38BDF8)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🫧 Bubble Pop!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                Text("Score: $score", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }
            Text("Pop all the bubbles! 🫧", fontSize = 16.sp, color = Color.White,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                bubbles.forEach { bubble ->
                    if (!bubble.popped) {
                        Box(modifier = Modifier
                            .fillMaxSize().wrapContentSize(Alignment.TopStart)
                            .padding(start = bubble.x.dp, top = (bubble.y + wobble).dp)
                            .size(bubble.size.dp)
                            .clip(CircleShape)
                            .background(bubble.color.copy(alpha = 0.7f))
                            .clickable {
                                SoundManager.playTap()
                                bubbles = bubbles.map { if (it.id == bubble.id) it.copy(popped = true) else it }
                                score++
                            })
                    }
                }
            }
        }
        WinCelebration(showWin) {
            showWin = false
            score = 0
            bubbles = bubbles.map { it.copy(popped = false) }
        }
    }
}
