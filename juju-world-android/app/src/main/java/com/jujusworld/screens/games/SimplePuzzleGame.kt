package com.jujusworld.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
fun SimplePuzzleGame(navController: NavController) {
    val emojis = listOf("🦋","🌈","⭐","🌸","🦄","🎀","🌟","💖","🌺","🎊","✨","🌙")
    var cards by remember {
        mutableStateOf((emojis + emojis).shuffled().mapIndexed { i, e ->
            Triple(i, e, false) // id, emoji, matched
        })
    }
    var flipped by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showWin by remember { mutableStateOf(false) }

    LaunchedEffect(flipped) {
        if (flipped.size == 2) {
            val a = cards[flipped[0]]; val b = cards[flipped[1]]
            if (a.second == b.second) {
                SoundManager.playSuccess()
                cards = cards.map { if (it.first == a.first || it.first == b.first) Triple(it.first, it.second, true) else it }
                flipped = emptyList()
                if (cards.all { it.third }) showWin = true
            } else {
                kotlinx.coroutines.delay(900)
                flipped = emptyList()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF4C0519), Color(0xFF881337), Color(0xFFBE185D)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🧩 Memory Match!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("Match the pairs! 🌟", fontSize = 16.sp, color = Color.White,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                contentPadding        = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(cards) { idx, (id, emoji, matched) ->
                    val revealed = matched || idx in flipped
                    Box(modifier = Modifier.aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (matched) Color(0xFF34D399).copy(alpha = 0.5f)
                            else if (revealed) Color(0xFF4C0519) else Color(0xFF6B0F2A))
                        .border(2.dp, if (matched) Color(0xFF34D399) else Color(0xFFFF6B9D), RoundedCornerShape(14.dp))
                        .clickable(enabled = !matched && idx !in flipped && flipped.size < 2) {
                            SoundManager.playTap()
                            flipped = flipped + idx
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (revealed) emoji else "❓", fontSize = 30.sp)
                    }
                }
            }
        }
        WinCelebration(showWin) {
            showWin = false
            cards = (emojis + emojis).shuffled().mapIndexed { i, e -> Triple(i, e, false) }
            flipped = emptyList()
        }
    }
}
