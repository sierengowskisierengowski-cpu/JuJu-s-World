package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.Prefs
import com.jujusworld.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun CountingGame(navController: NavController) {
    val context = LocalContext.current
    val countEmojis = listOf("⭐", "🦋", "🌸", "🍭", "🎈", "🌟", "🍎", "🐝")

    var targetCount by remember { mutableIntStateOf((1..10).random()) }
    var emoji by remember { mutableStateOf(countEmojis.random()) }
    var choices by remember { mutableStateOf(generateCountChoices(targetCount)) }
    var result by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }

    val celebScale by animateFloatAsState(
        if (result == "correct") 1.2f else 1f,
        spring(dampingRatio = 0.4f), label = "cs"
    )

    fun nextRound() {
        targetCount = (1..10).random()
        emoji = countEmojis.random()
        choices = generateCountChoices(targetCount)
        result = ""
    }

    LaunchedEffect(result) {
        if (result == "correct") { delay(1500); nextRound() }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF0C4A6E), Color(0xFF1E1B4B)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🔢  Counting Game", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.weight(1f))
                Text("⭐ $score", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }

            Text("How many $emoji can you count?", fontSize = 18.sp, color = Color(0xFFBAE6FD),
                textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 20.dp))

            Spacer(Modifier.height(16.dp))

            // Object display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x33FFFFFF))
                    .padding(20.dp)
                    .scale(celebScale)
            ) {
                val rows = (0 until targetCount).chunked(5)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    rows.forEach { row ->
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            row.forEach { Text(emoji, fontSize = 36.sp, modifier = Modifier.padding(4.dp)) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            when (result) {
                "correct" -> Text("🎉 $targetCount! You're so smart!", fontSize = 22.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF34D399), textAlign = TextAlign.Center)
                "wrong"   -> Text("Not quite! Count the $emoji again! 👀", fontSize = 18.sp,
                    color = Color(0xFFFBBF24), textAlign = TextAlign.Center)
                else      -> Text("Pick the right number! 🔢", fontSize = 18.sp, color = Color(0xFF7DD3FC))
            }

            Spacer(Modifier.height(16.dp))

            // Number choice buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                choices.forEach { choice ->
                    val isTarget = choice == targetCount
                    val bg = when {
                        result == "correct" && isTarget -> Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                        result == "wrong" && !isTarget && choice == targetCount -> Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .clickable(enabled = result.isEmpty()) {
                                if (choice == targetCount) {
                                    result = "correct"; score++; Prefs.addStars()
                                    SoundManager.speak("$targetCount! That's right!")
                                } else {
                                    result = "wrong"
                                    SoundManager.speak("Try again, count carefully!")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(choice.toString(), fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (result == "wrong") {
                Spacer(Modifier.height(16.dp))
                Button(onClick = { result = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))) {
                    Text("Try Again 💪", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

fun generateCountChoices(target: Int): List<Int> {
    val pool = (1..10).filter { it != target }.shuffled().take(3)
    return (pool + target).shuffled()
}
