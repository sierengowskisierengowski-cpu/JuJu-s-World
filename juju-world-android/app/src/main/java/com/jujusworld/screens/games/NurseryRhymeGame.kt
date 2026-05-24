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

data class Rhyme(val emoji: String, val title: String, val lines: List<String>)

@Composable
fun NurseryRhymeGame(navController: NavController) {
    val rhymes = listOf(
        Rhyme("⭐","Twinkle Twinkle", listOf(
            "Twinkle, twinkle, little star,","How I wonder what you are!",
            "Up above the world so high,","Like a diamond in the sky!")),
        Rhyme("🐑","Baa Baa Black Sheep", listOf(
            "Baa, baa, black sheep,","Have you any wool?",
            "Yes sir, yes sir,","Three bags full!")),
        Rhyme("🎎","Jack and Jill", listOf(
            "Jack and Jill went up the hill","To fetch a pail of water.",
            "Jack fell down and broke his crown,","And Jill came tumbling after.")),
        Rhyme("🌟","Mary Had a Little Lamb", listOf(
            "Mary had a little lamb,","Its fleece was white as snow.",
            "Everywhere that Mary went,","The lamb was sure to go.")),
        Rhyme("🕷️","Itsy Bitsy Spider", listOf(
            "The itsy bitsy spider","Climbed up the water spout.",
            "Down came the rain","And washed the spider out!")),
        Rhyme("🎵","Happy Birthday", listOf(
            "Happy birthday to you,","Happy birthday to you!",
            "Happy birthday dear JuJu,","Happy birthday to you! 🎂")),
    )

    var selected by remember { mutableStateOf<Rhyme?>(null) }
    var lineIdx  by remember { mutableIntStateOf(0) }
    val inf = rememberInfiniteTransition(label = "nr")
    val pulse by inf.animateFloat(0.95f, 1.05f,
        infiniteRepeatable(tween(600, easing = EaseInOutSine), RepeatMode.Reverse), label = "p")

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA)))
    )) {
        if (selected == null) {
            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Text("🎤 Nursery Rhymes!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text("Pick a rhyme to sing! 🎵", fontSize = 18.sp, color = Color(0xFFE9D5FF),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement   = Arrangement.spacedBy(10.dp)
                ) {
                    items(rhymes) { rhyme ->
                        Box(modifier = Modifier.aspectRatio(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0x33FFFFFF))
                            .clickable {
                                selected = rhyme; lineIdx = 0
                                SoundManager.playTap()
                                SoundManager.speak(rhyme.title)
                            },
                            contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(rhyme.emoji, fontSize = 40.sp)
                                Text(rhyme.title, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                    color = Color.White, textAlign = TextAlign.Center, maxLines = 2)
                            }
                        }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(selected!!.emoji, fontSize = 80.sp, modifier = Modifier.scale(pulse))
                Spacer(Modifier.height(16.dp))
                Text(selected!!.title, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24), textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                selected!!.lines.forEachIndexed { i, line ->
                    Text(line, fontSize = 22.sp,
                        color = if (i <= lineIdx) Color.White else Color(0x66FFFFFF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { selected = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))) {
                        Text("← Back", color = Color.White)
                    }
                    Button(onClick = {
                        if (lineIdx < selected!!.lines.size - 1) {
                            lineIdx++
                            SoundManager.playTap()
                            SoundManager.speak(selected!!.lines[lineIdx])
                        } else {
                            lineIdx = 0
                            SoundManager.speak(selected!!.lines[0])
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))) {
                        Text(if (lineIdx < selected!!.lines.size - 1) "Next Line ▶" else "Again! 🔄", color = Color.White)
                    }
                }
            }
        }
    }
}
