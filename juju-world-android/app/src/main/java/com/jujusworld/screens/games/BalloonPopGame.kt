package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.ui.WinCelebration
import com.jujusworld.utils.SoundManager

data class BalloonItem(val id: Int, val x: Float, val color: Color, var popped: Boolean = false)

@Composable
fun BalloonPopGame(navController: NavController) {
    val colors = listOf(Color(0xFFEC4899),Color(0xFF8B5CF6),Color(0xFF06B6D4),Color(0xFFF59E0B),Color(0xFF34D399),Color(0xFFF43F5E))
    var balloons by remember {
        mutableStateOf((0 until 12).map { i -> BalloonItem(i, 8f + (i % 4) * 23f, colors[i % colors.size]) })
    }
    var score by remember { mutableIntStateOf(0) }
    var showWin by remember { mutableStateOf(false) }

    val inf = rememberInfiniteTransition(label = "bal")
    val floatY by inf.animateFloat(0f, -20f,
        infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse), label = "fy")

    LaunchedEffect(score) { if (score >= 12) { kotlinx.coroutines.delay(300); showWin = true } }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF4C1D95), Color(0xFF7C3AED)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🎈 Balloon Pop!", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.weight(1f))
                Text("$score / 12", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.fillMaxSize()) {
                balloons.filter { !it.popped }.forEach { b ->
                    Text("🎈", fontSize = 52.sp,
                        modifier = Modifier
                            .fillMaxSize().wrapContentSize(Alignment.TopStart)
                            .padding(start = b.x.dp, top = (100f + floatY + b.id * 40f).dp)
                            .clickable {
                                SoundManager.playTap()
                                balloons = balloons.map { if (it.id == b.id) it.copy(popped = true) else it }
                                score++
                            })
                }
            }
        }
        WinCelebration(showWin) { showWin = false; score = 0; balloons = balloons.map { it.copy(popped = false) } }
    }
}
