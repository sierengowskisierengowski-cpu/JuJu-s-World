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
import com.jujusworld.utils.SoundManager

data class Animal(val emoji: String, val name: String, val sound: String, val color: Color)

@Composable
fun AnimalSoundsGame(navController: NavController) {
    val animals = listOf(
        Animal("🐮","Cow","Moo!", Color(0xFF78350F)),
        Animal("🐷","Pig","Oink!", Color(0xFFFDA4AF)),
        Animal("🐔","Chicken","Cluck!", Color(0xFFFBBF24)),
        Animal("🐶","Dog","Woof!", Color(0xFFD97706)),
        Animal("🐱","Cat","Meow!", Color(0xFF818CF8)),
        Animal("🐸","Frog","Ribbit!", Color(0xFF4ADE80)),
        Animal("🦁","Lion","Roar!", Color(0xFFF59E0B)),
        Animal("🐘","Elephant","Trumpet!", Color(0xFF94A3B8)),
        Animal("🦆","Duck","Quack!", Color(0xFF34D399)),
        Animal("🐝","Bee","Buzz!", Color(0xFFFDE047)),
        Animal("🦄","Unicorn","Neigh!", Color(0xFFE879F9)),
        Animal("🐬","Dolphin","Click!", Color(0xFF38BDF8)),
    )
    var lastTapped by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF052E16), Color(0xFF14532D), Color(0xFF166534)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("🐮 Animal Sounds!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            lastTapped?.let {
                Text("$it", fontSize = 36.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(8.dp))
            } ?: Text("Tap an animal to hear its sound! 🎵",
                fontSize = 18.sp, color = Color.White, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                contentPadding        = PaddingValues(bottom = 16.dp)
            ) {
                items(animals) { animal ->
                    AnimalCard(animal) {
                        SoundManager.playTap()
                        SoundManager.speak("The ${animal.name} says ${animal.sound}")
                        lastTapped = "${animal.emoji} ${animal.name} says \"${animal.sound}\""
                    }
                }
            }
        }
    }
}

@Composable
fun AnimalCard(animal: Animal, onClick: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "ac")
    val sc by inf.animateFloat(0.97f, 1.03f,
        infiniteRepeatable(tween(1000, easing = EaseInOutSine), RepeatMode.Reverse), label = "s")
    Box(modifier = Modifier.aspectRatio(1f).scale(sc)
        .clip(RoundedCornerShape(20.dp))
        .background(animal.color.copy(alpha = 0.25f))
        .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(animal.emoji, fontSize = 44.sp)
            Text(animal.name, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                color = Color.White, textAlign = TextAlign.Center)
        }
    }
}
