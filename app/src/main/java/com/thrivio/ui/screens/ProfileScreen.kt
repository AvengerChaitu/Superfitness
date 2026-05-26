package com.thrivio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivio.gamification.GamificationEngine
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.theme.*

@Composable
fun ProfileScreen(viewModel: DashboardViewModel, onPremiumClick: () -> Unit = {}) {
    val stats by viewModel.stats.collectAsState()
    val xpProgress by viewModel.xpProgress.collectAsState()

    val nextLevelXp = GamificationEngine.totalXpForLevel(stats.currentLevel).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(XpOrange.copy(alpha = 0.15f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text("🐼", fontSize = 44.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("Thrivio User", fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

        // XP/Level card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
            border = BorderStroke(1.dp, BorderOutlineLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Level ${stats.currentLevel}", fontWeight = FontWeight.Black, fontSize = 24.sp, color = XpOrange)
                        Text("${stats.totalXp} / $nextLevelXp XP", color = Color.Gray, fontSize = 14.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(XpOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${stats.currentLevel}", fontWeight = FontWeight.Black, fontSize = 28.sp, color = XpOrange)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { xpProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = XpOrange,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }
        }

        // Streak
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
            border = BorderStroke(1.dp, BorderOutlineLight)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFFF5722), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("${stats.streakDays} day streak", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Best: ${stats.longestStreak} days", color = Color.Gray, fontSize = 13.sp)
                    }
                }
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
            }
        }

        // Today's stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
            border = BorderStroke(1.dp, BorderOutlineLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                StatRow(Icons.AutoMirrored.Filled.DirectionsRun, "Steps", "${stats.stepsToday}")
                StatRow(Icons.Default.Restaurant, "Meals logged", "${stats.mealsLoggedToday}")
                StatRow(Icons.Default.FitnessCenter, "Workouts", "${stats.workoutsCompletedToday}")
                StatRow(Icons.Default.SelfImprovement, "Meditation", "${stats.meditationMinutesToday} min")
            }
        }

        // Premium CTA
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onPremiumClick() },
            colors = CardDefaults.cardColors(containerColor = XpOrange.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, XpOrange)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, null, tint = XpOrange, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Unlock Premium", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = XpOrange)
                        Text("Streak freezes, custom avatars & leagues", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Icon(Icons.Default.ChevronRight, null, tint = XpOrange)
            }
        }
    }
}

@Composable
fun StatRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.Gray, fontSize = 14.sp)
        }
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
