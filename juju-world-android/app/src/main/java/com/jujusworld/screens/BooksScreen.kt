package com.jujusworld.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager

data class StoryPage(val scene: String, val text: String, val bg: List<Color>)
data class StoryBook(val title: String, val emoji: String, val pages: List<StoryPage>)

@Composable
fun BooksScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedBook by remember { mutableIntStateOf(-1) }
    var pageIndex by remember { mutableIntStateOf(0) }

    val books = listOf(
        StoryBook("The Magic Butterfly", "🦋", listOf(
            StoryPage("🌸🦋🌸", "Once upon a time, a tiny butterfly was born from a shiny cocoon in an enchanted garden.",
                listOf(Color(0xFFFCE7F3), Color(0xFFFBCFE8))),
            StoryPage("🌈🦋✨", "She spread her beautiful wings — pink, purple, and gold — and flew up toward the rainbow.",
                listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))),
            StoryPage("🌷🦋💫", "Every flower she touched began to glow and sing a happy little song.",
                listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))),
            StoryPage("🏡🦋🌟", "At sunset, she found her magical home — a palace made of petals, just for her.",
                listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))),
            StoryPage("🦋💖🌙", "And every night she would sleep under the stars, dreaming of tomorrow's adventures.\n\n✨ The End ✨",
                listOf(Color(0xFF1E1B4B), Color(0xFF2D1B69))),
        )),
        StoryBook("Princess JuJu", "👑", listOf(
            StoryPage("🏰👑✨", "In a sparkling castle on a cloud, there lived a princess named JuJu.",
                listOf(Color(0xFFFCE7F3), Color(0xFFFBCFE8))),
            StoryPage("🦄👑🌸", "Her best friend was a silver unicorn who could grant one wish every day.",
                listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))),
            StoryPage("🍰👑🎉", "Today JuJu wished for a birthday cake for all her friends in the kingdom.",
                listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))),
            StoryPage("💫👑🌈", "A giant rainbow cake appeared! Everyone danced and laughed until the moon came up.",
                listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))),
            StoryPage("😴👑⭐", "JuJu hugged her unicorn and whispered: \"Every day in my world is magical.\"\n\n✨ The End ✨",
                listOf(Color(0xFF1E1B4B), Color(0xFF2D1B69))),
        )),
        StoryBook("The Rainbow Garden", "🌈", listOf(
            StoryPage("🌻🌈🌱", "A little seedling pushed through the soil one rainy morning.",
                listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))),
            StoryPage("🌧🌈🌷", "Rain came down in every color — red, orange, yellow, green, blue, purple!",
                listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC))),
            StoryPage("🦋🌈🌸", "Butterflies and bees arrived to drink from the rainbow puddles.",
                listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))),
            StoryPage("🌺🌈💐", "By afternoon, the garden was the most beautiful place in all the land.",
                listOf(Color(0xFFFCE7F3), Color(0xFFFBCFE8))),
            StoryPage("🌙🌈⭐", "And the rainbow stayed all night, keeping the garden warm and bright.\n\n✨ The End ✨",
                listOf(Color(0xFF1E1B4B), Color(0xFF2D1B69))),
        ))
    )

    if (selectedBook >= 0) {
        val book = books[selectedBook]
        val page = book.pages[pageIndex]
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(page.bg))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (pageIndex == 0) { selectedBook = -1; pageIndex = 0 }
                        else pageIndex--
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = if (page.bg[0].red < 0.3f) Color.White else Color(0xFF4C1D95))
                    }
                    Text("${pageIndex + 1} / ${book.pages.size}",
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                        color = if (page.bg[0].red < 0.3f) Color.White else Color(0xFF4C1D95))
                    IconButton(
                        onClick = {
                            if (pageIndex < book.pages.size - 1) pageIndex++
                            else { selectedBook = -1; pageIndex = 0 }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next",
                            tint = if (page.bg[0].red < 0.3f) Color.White else Color(0xFF4C1D95))
                    }
                }

                Spacer(Modifier.weight(1f))
                Text(page.scene, fontSize = 80.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                Text(page.text, fontSize = 22.sp, textAlign = TextAlign.Center, lineHeight = 32.sp,
                    color = if (page.bg[0].red < 0.3f) Color.White else Color(0xFF1E1B4B))
                Spacer(Modifier.weight(1f))

                // Read to Me
                Button(
                    onClick = { SoundManager.speak(page.text) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                ) {
                    Text("🔊 Read to Me", fontSize = 16.sp, color = Color.White)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        return
    }

    // Book shelf
    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF1E1B4B))))) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("📚  Books", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("Tap a book to start reading! 📖", fontSize = 16.sp, color = Color(0xFFBAE6FD),
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(20.dp))
            books.forEachIndexed { idx, book ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(book.pages.first().bg))
                        .clickable { selectedBook = idx; pageIndex = 0 }
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(book.emoji, fontSize = 52.sp)
                        Column {
                            Text(book.title, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B))
                            Text("${book.pages.size} pages  ·  Tap to read", fontSize = 14.sp,
                                color = Color(0xFF4C1D95))
                        }
                    }
                }
            }
        }
    }
}
