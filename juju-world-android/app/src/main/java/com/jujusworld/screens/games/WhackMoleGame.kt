package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.ui.WinCelebration
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun WhackMoleGame(navController: NavController) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var active by remember { mutableIntStateOf(-1) }
    var showWin by remember { mutableStateOf(false) }
    val moles = listOf("🦔","🐭","🐹","🐰","🦊","🐻","🐼","🐨","🐸","🦔","🐭","🐹")

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            active = (0 until 12).random()
            delay((600..1100).random().toLong())
            active = -1
            delay(200)
            timeLeft--
        }
        showWin = true
    }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF14532D), Color(0xFF166534), Color(0xFF4ADE80).copy(alpha = 0.3f)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🔨 Whack-a-Mole!", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("⭐ $score", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                    Text("⏰ $timeLeft s", fontSize = 14.sp, color = Color.White)
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(moles) { i, mole ->
                    val visible = i == active
                    Box(modifier = Modifier.aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (visible) Color(0xFF4ADE80) else Color(0xFF15803D))
                        .clickable(enabled = visible) {
                            SoundManager.playTap()
                            score++
                            active = -1
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (visible) mole else "🕳️", fontSize = 40.sp)
                    }
                }
            }
        }
        WinCelebration(showWin) { showWin = false; score = 0; timeLeft = 30 }
    }
}
