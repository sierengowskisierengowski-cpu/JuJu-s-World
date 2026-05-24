package com.jujusworld.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.navigation.Routes

data class GameItem(val emoji: String, val title: String, val desc: String, val route: String, val color1: Color, val color2: Color)

@Composable
fun GamesScreen(navController: NavController) {
    val games = listOf(
        GameItem("🔤", "Letters", "Learn the ABC!", Routes.LETTERS, Color(0xFFEC4899), Color(0xFF9333EA)),
        GameItem("🔢", "Counting", "Count with JuJu!", Routes.COUNTING, Color(0xFF14B8A6), Color(0xFF0EA5E9)),
        GameItem("🌈", "Colors", "Know your colors!", Routes.COLORS, Color(0xFFF97316), Color(0xFFEF4444)),
        GameItem("⭐", "Shapes", "Match the shapes!", Routes.SHAPES, Color(0xFF8B5CF6), Color(0xFF6366F1)),
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF134E4A), Color(0xFF1E1B4B)))
        )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🎮  Games", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Text(
                "Pick a game to play!",
                fontSize = 18.sp, color = Color(0xFF99F6E4),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(games) { game ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1.3f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.verticalGradient(listOf(game.color1, game.color2)))
                            .clickable { navController.navigate(game.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Text(game.emoji, fontSize = 56.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(game.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(game.desc, fontSize = 13.sp, color = Color(0xFFE0F2FE), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
