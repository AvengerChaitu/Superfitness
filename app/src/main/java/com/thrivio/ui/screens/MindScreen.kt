package com.thrivio.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.MoodEntity
import com.thrivio.gamification.GamificationEngine
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MindScreen(viewModel: DashboardViewModel) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var breathingActive by remember { mutableStateOf(false) }
    var breathingText by remember { mutableStateOf("TAP TO BREATHE") }
    val infiniteTransition = rememberInfiniteTransition()
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (breathingActive) 1.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    var selectedMood by remember { mutableStateOf(-1) }
    var moodNote by remember { mutableStateOf("") }
    var meditationMinutes by remember { mutableStateOf(0) }

    val moods = listOf(
        "😤" to "Stressed",
        "😐" to "Okay",
        "🙂" to "Good",
        "😊" to "Happy",
        "🔥" to "Motivated"
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("MIND & BODY", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)

            // Mood tracker
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                border = BorderStroke(1.dp, BorderOutlineLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("How are you feeling?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        moods.forEachIndexed { index, (emoji, _) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    emoji,
                                    fontSize = 36.sp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedMood == index) MindPurple.copy(alpha = 0.2f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            selectedMood = index
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                    if (selectedMood >= 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = moodNote,
                            onValueChange = { moodNote = it },
                            label = { Text("Add a note (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val ctx = androidx.compose.ui.platform.LocalContext.current
                        Button(
                            onClick = {
                                val date = java.time.LocalDate.now().toString()
                                scope.launch {
                                    AppDatabase.getInstance(ctx)
                                        .moodDao().upsertMood(MoodEntity(date = date, mood = selectedMood, note = moodNote))
                                    viewModel.awardXp(GamificationEngine.XpEvents.LOG_MOOD)
                                }
                                selectedMood = -1
                                moodNote = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MindPurple)
                        ) {
                            Text("Log Mood +10 XP", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Meditation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MindPurple.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, MindPurple.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("MEDITATION", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MindPurple)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(breathScale)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(MindPurple, Color(0xFF7C4DFF)))
                            )
                            .clickable {
                                if (!breathingActive) {
                                    breathingActive = true
                                    breathingText = "INHALE..."
                                    scope.launch {
                                        delay(2000)
                                        breathingText = "EXHALE..."
                                        delay(2000)
                                        breathingActive = false
                                        breathingText = "TAP TO BREATHE"
                                        meditationMinutes += 1
                                        if (meditationMinutes % 5 == 0) {
                                            viewModel.logMeditation(5)
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            breathingText,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Today: $meditationMinutes min", fontWeight = FontWeight.Bold)
                    Text("Tap to start a 4s breathing cycle", color = Color.Gray, fontSize = 12.sp)
                }
            }

            // Quick tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                border = BorderStroke(1.dp, BorderOutlineLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Tip", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Take 3 deep breaths before starting any task. This activates your parasympathetic nervous system and reduces cortisol.",
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
