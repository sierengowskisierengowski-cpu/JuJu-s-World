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
fun LettersGame(navController: NavController) {
    val context = LocalContext.current
    val letters = ('A'..'Z').toList()

    var targetLetter by remember { mutableStateOf(letters.random()) }
    var choices by remember { mutableStateOf(generateLetterChoices(targetLetter, letters)) }
    var result by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }

    val celebScale by animateFloatAsState(
        if (result == "correct") 1.3f else 1f,
        spring(dampingRatio = 0.4f), label = "cs"
    )

    fun nextRound() {
        targetLetter = letters.random()
        choices = generateLetterChoices(targetLetter, letters)
        result = ""
    }

    LaunchedEffect(result) {
        if (result == "correct") {
            delay(1500)
            nextRound()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF581C87), Color(0xFF1E1B4B)))
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
                Text("🔤  Letters Game", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.weight(1f))
                Text("⭐ $score", fontSize = 20.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            Text("Which letter is this?", fontSize = 20.sp, color = Color(0xFFE9D5FF))
            Spacer(Modifier.height(20.dp))

            // Target letter display
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(celebScale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFFEC4899), Color(0xFF9333EA))))
                    .border(4.dp, Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(targetLetter.toString(), fontSize = 72.sp, fontWeight = FontWeight.Bold,
                        color = Color.White)
                    Text(targetLetter.lowercaseChar().toString(), fontSize = 36.sp, color = Color(0xFFE9D5FF))
                }
            }

            Spacer(Modifier.height(12.dp))

            when (result) {
                "correct" -> Text("🎉 You got it! Amazing!", fontSize = 24.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF34D399), textAlign = TextAlign.Center)
                "wrong"   -> Text("Try again! You can do it! 💪", fontSize = 20.sp,
                    color = Color(0xFFFBBF24), textAlign = TextAlign.Center)
                else      -> Text("Tap the matching letter! 👇", fontSize = 18.sp,
                    color = Color(0xFFA78BFA))
            }

            Spacer(Modifier.height(24.dp))

            // Choice grid
            LazyGrid2(choices) { choice ->
                val isTarget = choice == targetLetter
                val bg = when {
                    result == "correct" && isTarget -> Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                    result == "wrong"   && isTarget -> Brush.verticalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                    else                            -> Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF6366F1)))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.4f)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .clickable(enabled = result.isEmpty()) {
                            if (choice == targetLetter) {
                                result = "correct"
                                score++
                                Prefs.addStars()
                                SoundManager.speak("Yes! ${targetLetter}!")
                            } else {
                                result = "wrong"
                                SoundManager.speak("Try again!")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(choice.toString(), fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(choice.lowercaseChar().toString(), fontSize = 22.sp, color = Color(0xFFE9D5FF))
                    }
                }
            }

            if (result == "wrong") {
                Spacer(Modifier.height(16.dp))
                Button(onClick = { result = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))) {
                    Text("Try Again 💪", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LazyGrid2(items: List<Char>, content: @Composable RowScope.(Char) -> Unit) {
    val rows = items.chunked(2)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { item -> content(item) }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

fun generateLetterChoices(target: Char, all: List<Char>): List<Char> {
    val wrong = all.filter { it != target }.shuffled().take(3)
    return (wrong + target).shuffled()
}
