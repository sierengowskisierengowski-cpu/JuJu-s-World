package com.jujusworld.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jujusworld.utils.SoundManager

data class KidApp(
    val emoji: String,
    val name: String,
    val ageTag: String,
    val desc: String,
    val packageId: String,
    val gradStart: Color,
    val gradEnd: Color
)

@Composable
fun AppStoreScreen(navController: NavController) {
    val context = LocalContext.current

    val apps = listOf(
        KidApp("▶", "YouTube Kids",       "Ages 2+", "Safe videos for little ones",      "com.google.android.apps.youtube.kids", Color(0xFFFF0000), Color(0xFFCC0000)),
        KidApp("🧠", "Khan Academy Kids", "Ages 2-8","Math, reading & more",              "org.khanacademy.kids",                  Color(0xFF14B8A6), Color(0xFF0EA5E9)),
        KidApp("🔤", "Endless Alphabet",  "Ages 3+", "Vocabulary through play",           "com.originator.endlessalphabet",        Color(0xFFF97316), Color(0xFFEF4444)),
        KidApp("⭐", "Starfall",          "Ages 3-8","Phonics & reading",                 "com.starfall.app",                      Color(0xFF8B5CF6), Color(0xFF6366F1)),
        KidApp("🍽", "Toca Kitchen 2",   "Ages 3+", "Cook and experiment with food",      "com.tocaboca.kitchen2",                 Color(0xFFEC4899), Color(0xFF9333EA)),
        KidApp("🐯", "Daniel Tiger's Neighborhood","Ages 3-6","Social-emotional learning","com.pbs.dtiger",                        Color(0xFFF59E0B), Color(0xFFF97316)),
        KidApp("🪆", "Toca Life World",  "Ages 4+", "Creative open-world play",          "com.tocaboca.tocalifeworld",             Color(0xFF22D3EE), Color(0xFF3B82F6)),
        KidApp("📺", "PBS KIDS Video",   "Ages 2+", "Full episodes, always free",         "org.pbskids.video",                     Color(0xFF10B981), Color(0xFF059669)),
        KidApp("🎨", "Drawing Academy",  "Ages 3+", "Draw animals, shapes & more",       "com.fun.kids.drawing.games",            Color(0xFFFBBF24), Color(0xFFF97316)),
        KidApp("🔢", "Endless Numbers",  "Ages 3+", "Number concepts through play",      "com.originator.endlessnumbers",         Color(0xFF4F46E5), Color(0xFF7C3AED)),
        KidApp("🐾", "Paw Patrol",       "Ages 3+", "Go on adventures with the pups",    "com.nickelodeon.pawpatrol",             Color(0xFF2563EB), Color(0xFF1D4ED8)),
        KidApp("🦕", "Dino Dan",         "Ages 4+", "Explore amazing dinosaurs",         "com.dino.dan.dinotracker",              Color(0xFF16A34A), Color(0xFF15803D)),
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF1E3A5F), Color(0xFF1E1B4B)))
        )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            // Header
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Column {
                    Text("🏪  JuJu's App Store", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Hand-picked apps just for you!", fontSize = 14.sp, color = Color(0xFF93C5FD))
                }
            }

            // Featured banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("⭐", fontSize = 40.sp)
                    Column {
                        Text("All apps are JuJu-approved! 💖", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Tap any app to open it in the Play Store", fontSize = 13.sp, color = Color(0xFFE9D5FF))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // App grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(apps) { app ->
                    AppCard(app) {
                        SoundManager.playTap()
                        // Try market:// URI first; fall back to browser Play Store URL
                        val marketUri = Uri.parse("market://details?id=${app.packageId}")
                        val webUri    = Uri.parse("https://play.google.com/store/apps/details?id=${app.packageId}")
                        val intent = Intent(Intent.ACTION_VIEW, marketUri).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppCard(app: KidApp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F2749))
            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Coloured icon box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.verticalGradient(listOf(app.gradStart, app.gradEnd))),
                contentAlignment = Alignment.Center
            ) {
                Text(app.emoji, fontSize = 28.sp)
            }
            Text(app.name, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color.White, textAlign = TextAlign.Center, maxLines = 2, lineHeight = 14.sp)
            Box(
                modifier = Modifier
                    .background(Color(0xFF1E3A5F), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(app.ageTag, fontSize = 9.sp, color = Color(0xFF93C5FD))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF22C55E))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Get It ▶", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
