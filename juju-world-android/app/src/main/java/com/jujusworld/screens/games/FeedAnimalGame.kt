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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.ui.WinCelebration
import com.jujusworld.utils.SoundManager

private val animalFoods = mapOf(
    "🐟" to "🐱", "🥕" to "🐰", "🌿" to "🐢", "🍎" to "🐴",
    "🍌" to "🐒", "🌰" to "🐿️", "🦴" to "🐶", "🌸" to "🦋"
)

@Composable
fun FeedAnimalGame(navController: NavController) {
    val pairs = animalFoods.entries.toList().shuffled()
    var score by remember { mutableIntStateOf(0) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var showWin by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }

    val current = pairs[currentIdx % pairs.size]
    val options = remember(currentIdx) {
        val correct = current.value
        val others  = animalFoods.values.filter { it != correct }.shuffled().take(3)
        (listOf(correct) + others).shuffled()
    }

    val inf = rememberInfiniteTransition(label = "fa")
    val bounce by inf.animateFloat(0f, -10f,
        infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse), label = "b")

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF0C4A6E), Color(0xFF0369A1), Color(0xFF38BDF8).copy(alpha = 0.4f)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🐟 Feed the Animal!", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, modifier = Modifier.weight(1f))
                Text("⭐ $score", fontSize = 22.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }
            Text("What does the ${current.value} eat? 🤔",
                fontSize = 20.sp, color = Color.White, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp))
            Text(current.value, fontSize = 96.sp,
                modifier = Modifier.offset(y = bounce.dp))
            if (feedback.isNotEmpty())
                Text(feedback, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = if (feedback.startsWith("✅")) Color(0xFF34D399) else Color(0xFFF43F5E),
                    modifier = Modifier.padding(8.dp))
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                items(options) { option ->
                    Box(modifier = Modifier.aspectRatio(2f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x33FFFFFF))
                        .clickable {
                            if (option == current.key) {
                                SoundManager.playSuccess()
                                feedback = "✅ Yes! ${current.value} loves ${current.key}!"
                                score++
                                if (score >= 8) { showWin = true }
                                else { currentIdx++ }
                            } else {
                                SoundManager.playTap()
                                feedback = "❌ Try again!"
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(option, fontSize = 48.sp)
                    }
                }
            }
        }
        WinCelebration(showWin) { showWin = false; score = 0; currentIdx = 0; feedback = "" }
    }
}
