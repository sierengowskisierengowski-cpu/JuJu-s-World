package com.jujusworld.screens.games

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
import com.jujusworld.utils.SoundManager

@Composable
fun DressUpGame(navController: NavController) {
    val hats   = listOf("👑","🎀","🪭","🎩","💐","🌸","⭐","🦋")
    val tops   = listOf("👗","🩱","👚","🧥","🥻","🩴","🎽","👘")
    val extras = listOf("💎","💍","📿","🌟","✨","🪄","🌈","🎊")

    var selectedHat   by remember { mutableStateOf(hats.first()) }
    var selectedTop   by remember { mutableStateOf(tops.first()) }
    var selectedExtra by remember { mutableStateOf(extras.first()) }

    Box(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF831843), Color(0xFFBE185D), Color(0xFFF9A8D4)))
    )) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("👗 Dress Up!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Character preview
                Box(modifier = Modifier.width(180.dp).fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x44FFFFFF)),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(selectedHat,   fontSize = 56.sp)
                        Text(selectedTop,   fontSize = 72.sp)
                        Text(selectedExtra, fontSize = 40.sp)
                        Text("JuJu ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                // Wardrobe picker
                Column(modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    WardrobeRow("Hats / Crowns", hats, selectedHat) { selectedHat = it; SoundManager.playTap() }
                    WardrobeRow("Outfits", tops, selectedTop) { selectedTop = it; SoundManager.playTap() }
                    WardrobeRow("Accessories", extras, selectedExtra) { selectedExtra = it; SoundManager.playTap() }
                }
            }
        }
    }
}

@Composable
fun WardrobeRow(label: String, items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(label, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4),
            modifier = Modifier.height(90.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement   = Arrangement.spacedBy(6.dp)) {
            items(items) { item ->
                Box(modifier = Modifier.aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (item == selected) Color(0x66FFFFFF) else Color(0x22FFFFFF))
                    .border(if (item == selected) 2.dp else 0.dp, Color(0xFFFBBF24), RoundedCornerShape(12.dp))
                    .clickable { onSelect(item) },
                    contentAlignment = Alignment.Center) {
                    Text(item, fontSize = 24.sp)
                }
            }
        }
    }
}
