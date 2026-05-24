package com.jujusworld.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.ui.WinCelebration
import com.jujusworld.utils.SoundManager

data class Book(
    val emoji: String, val title: String, val color1: Color, val color2: Color,
    val pages: List<Pair<String, String>> // emoji illustration + text
)

@Composable
fun BooksScreen(navController: NavController) {
    val books = listOf(
        Book("🦋","The Butterfly Princess", Color(0xFFEC4899), Color(0xFF9B59B6), listOf(
            "🦋" to "Once upon a time, there was a tiny caterpillar named Bella.",
            "🍃" to "Bella loved to munch on the greenest leaves in the garden.",
            "💤" to "One day, Bella wrapped herself in a cozy cocoon and went to sleep.",
            "✨" to "When she woke up... she had the most beautiful wings!",
            "👑" to "Bella spread her wings and flew across the magical garden.",
            "🌸" to "She was the most beautiful butterfly princess of all! The End. 💖"
        )),
        Book("🦄","Unicorn's Rainbow", Color(0xFF818CF8), Color(0xFF06B6D4), listOf(
            "🌧️" to "Luna the unicorn was sad. The rainbow had disappeared!",
            "🌟" to "She galloped across the hills, searching everywhere.",
            "🐸" to "\"Have you seen my rainbow?\" she asked the frogs by the pond.",
            "🐦" to "The little bird said: \"Follow me! I know where it went!\"",
            "🌈" to "The rainbow was hiding behind the biggest cloud!",
            "🦄" to "Luna touched it with her horn and it lit up the whole sky! The End. 🌈"
        )),
        Book("🐝","Bella the Brave Bee", Color(0xFFF59E0B), Color(0xFF10B981), listOf(
            "🌺" to "Bella was the smallest bee in the hive, but the bravest.",
            "🌂" to "One stormy day, the flowers needed saving!",
            "💨" to "The wind blew all the petals away from the garden.",
            "🐝" to "Brave Bella flew through the storm, carrying them back one by one.",
            "🌸" to "All the flowers bloomed again, more beautiful than ever.",
            "🏆" to "The Queen Bee gave Bella a golden crown. The bravest bee of all! The End. ⭐"
        )),
    )

    var openBook   by remember { mutableStateOf<Book?>(null) }
    var pageIndex  by remember { mutableIntStateOf(0) }
    var showWin    by remember { mutableStateOf(false) }

    val inf = rememberInfiniteTransition(label = "books")
    // Enchanted library forest background
    val dustY by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "dy")
    val dustY2 by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(5500, easing = LinearEasing), RepeatMode.Restart, StartOffset(2000)), label = "dy2")
    val glow by inf.animateFloat(0.6f, 1f, infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse), label = "gl")

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Enchanted library forest background ──────────────────────────
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF052E16), Color(0xFF14532D), Color(0xFF78350F).copy(alpha = 0.6f)))
        ))
        // Golden dust particles
        for (i in 0 until 14) {
            val x = (i * 127f) % 100f
            Text("✨", fontSize = 10.sp,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (x * 3.6f).dp, top = (dustY * 600 + i * 40).dp)
                    .alpha((1f - dustY) * 0.8f))
        }
        for (i in 0 until 8) {
            val x = (i * 97f + 50f) % 100f
            Text("⭐", fontSize = 8.sp,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
                    .padding(start = (x * 3.6f).dp, top = (dustY2 * 600 + i * 60).dp)
                    .alpha((1f - dustY2) * 0.6f))
        }

        if (openBook == null) {
            // ── Book shelf ────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFFBBF24))
                    }
                    Column {
                        Text("📚 JuJu's Books", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Choose a story! 📖", fontSize = 14.sp, color = Color(0xFF86EFAC))
                    }
                }
                books.forEach { book ->
                    BookCard(book, glow) {
                        SoundManager.playTap()
                        SoundManager.speak("${book.title}. A story for JuJu.")
                        openBook = book; pageIndex = 0
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        } else {
            // ── Reading view ──────────────────────────────────────────────
            val book = openBook!!
            val page = book.pages[pageIndex]
            val totalPages = book.pages.size

            Column(modifier = Modifier.fillMaxSize().systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { openBook = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFFFBBF24))
                    }
                    Text(book.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        modifier = Modifier.weight(1f))
                    // TTS button
                    Button(onClick = { SoundManager.speak(page.second) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("🔊 Read to Me", color = Color.White, fontSize = 13.sp)
                    }
                }

                // Star progress bar
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Page ${pageIndex + 1} of $totalPages ✨", fontSize = 14.sp, color = Color(0xFFFBBF24))
                    Spacer(Modifier.width(8.dp))
                    (0 until totalPages).forEach { i ->
                        Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp))
                            .background(if (i <= pageIndex) Color(0xFFFBBF24) else Color(0x44FFFFFF)))
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Page content — animated crossfade on page change
                AnimatedContent(pageIndex, transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                }, label = "page") { idx ->
                    val p = book.pages[idx]
                    Box(modifier = Modifier.fillMaxWidth().weight(1f)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.padding(24.dp)) {
                            Text(p.first, fontSize = 96.sp)
                            Text(p.second, fontSize = 20.sp, color = Color.White,
                                textAlign = TextAlign.Center, lineHeight = 28.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Navigation
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)) {
                    if (pageIndex > 0) {
                        Button(onClick = {
                            SoundManager.playTap(); pageIndex--
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))) {
                            Text("◀ Back", color = Color.White, fontSize = 16.sp)
                        }
                    }
                    if (pageIndex < totalPages - 1) {
                        Button(onClick = {
                            SoundManager.playTap(); pageIndex++
                            SoundManager.speak(book.pages[pageIndex].second)
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))) {
                            Text("Next ▶", color = Color.White, fontSize = 16.sp)
                        }
                    } else {
                        Button(onClick = { showWin = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))) {
                            Text("The End! 🎉", color = Color(0xFF78350F), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        WinCelebration(showWin) { showWin = false; openBook = null }
    }
}

@Composable
private fun BookCard(book: Book, glow: Float, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(100.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(Brush.linearGradient(listOf(book.color1, book.color2)))
        .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(book.emoji, fontSize = 52.sp)
            Column {
                Text(book.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${book.pages.size} pages · Tap to read ▶", fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f))
            }
            Spacer(Modifier.weight(1f))
            Text("✨", fontSize = 24.sp, modifier = Modifier.alpha(glow))
        }
    }
}
