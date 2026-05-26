package com.thrivio.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivio.gamification.GamificationEngine
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val stats by viewModel.stats.collectAsState()
    val steps by viewModel.todaySteps.collectAsState()
    val workouts by viewModel.recentWorkouts.collectAsState()
    val xpProgress by viewModel.xpProgress.collectAsState()

    var waterLoggedCups by remember { mutableStateOf(6) }
    var breathingActive by remember { mutableStateOf(false) }
    var breathingText by remember { mutableStateOf("TAP TO BREATHE") }
    var breathScale by remember { mutableStateOf(1f) }

    val stepGoal = 8000
    val sleepDuration = 6.1f
    val sleepGoal = 7f
    val calorieGoal = 2000

    val caloriesBurned = (steps * 0.05f).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(XpOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🐼", fontSize = 30.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Good morning,", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Champion!", fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(SurfaceCardLight, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderOutlineLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.OfflineBolt, "XP", tint = XpOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${stats.totalXp} XP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Row(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(SurfaceCardLight, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderOutlineLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalFireDepartment, "Streak", tint = Color(0xFFFF5722), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${stats.streakDays} DAYS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // STREAK HERO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(colors = listOf(XpOrange, Color(0xFFFF7A00))))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.syncStepData(steps + 500)
                }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${stats.streakDays}-DAY STREAK!", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
                    Text("Aero is cheering! Keep walking to earn XP.", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tap to add 500 steps", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lv.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${stats.currentLevel}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // XP PROGRESS
        LinearProgressIndicator(
            progress = { xpProgress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = XpOrange,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        // GOALS RINGS
        Text(text = "DAILY GOALS", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(20.dp))
                .background(SurfaceCardLight, RoundedCornerShape(20.dp))
                .border(1.dp, BorderOutlineLight, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val strokeWidth = 10.dp.toPx()
                val gap = 6.dp.toPx()
                val ringSize = 140.dp.toPx()

                val r1Percent = (steps.toFloat() / stepGoal).coerceIn(0f, 1f)
                drawArc(color = Color.LightGray.copy(alpha = 0.3f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
                drawArc(color = FitnessBlue, startAngle = -90f, sweepAngle = r1Percent * 360f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round))

                val r2Percent = (caloriesBurned.toFloat() / calorieGoal).coerceIn(0f, 1f)
                val r2Diameter = ringSize - (strokeWidth * 2) - (gap * 2)
                val r2Offset = strokeWidth + gap
                drawArc(color = Color.LightGray.copy(alpha = 0.3f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round), topLeft = Offset(r2Offset, r2Offset), size = Size(r2Diameter, r2Diameter))
                drawArc(color = Color(0xFFFF5722), startAngle = -90f, sweepAngle = r2Percent * 360f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round), topLeft = Offset(r2Offset, r2Offset), size = Size(r2Diameter, r2Diameter))

                val r3Percent = (sleepDuration / sleepGoal).coerceIn(0f, 1f)
                val r3Diameter = r2Diameter - (strokeWidth * 2) - (gap * 2)
                val r3Offset = r2Offset + strokeWidth + gap
                drawArc(color = Color.LightGray.copy(alpha = 0.3f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round), topLeft = Offset(r3Offset, r3Offset), size = Size(r3Diameter, r3Diameter))
                drawArc(color = MindPurple, startAngle = -90f, sweepAngle = r3Percent * 360f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round), topLeft = Offset(r3Offset, r3Offset), size = Size(r3Diameter, r3Diameter))
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardScreenGoalLegendRow("Steps", "$steps / $stepGoal", FitnessBlue, Icons.AutoMirrored.Filled.DirectionsRun)
                DashboardScreenGoalLegendRow("Kcal", "$caloriesBurned / $calorieGoal", Color(0xFFFF5722), Icons.Default.LocalFireDepartment)
                DashboardScreenGoalLegendRow("Water", "$waterLoggedCups / 8 cups", com.thrivio.ui.theme.WaterBlue, Icons.Default.LocalActivity)
                DashboardScreenGoalLegendRow("Sleep", "6h 5m / ${sleepGoal.toInt()}h", MindPurple, Icons.Default.NightsStay)
            }
        }

        // TODAY AT A GLANCE
        Text(text = "TODAY AT A GLANCE", style = MaterialTheme.typography.titleMedium)

        if (workouts.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(SurfaceCardLight, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderOutlineLight, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FitnessCenter, "Workout", tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("No workout today", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Head to the Workout tab to log one", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            workouts.take(1).forEach { w ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(SurfaceCardLight, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderOutlineLight, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(XpOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FitnessCenter, "Workout", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(w.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${w.durationSeconds / 60} min · ${w.caloriesBurned} cal", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                    Box(
                        modifier = Modifier.size(30.dp).clip(CircleShape).background(MascotGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // WATER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(SurfaceCardLight, RoundedCornerShape(16.dp))
                .border(1.dp, BorderOutlineLight, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(WaterBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💧", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Hydration Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("$waterLoggedCups of 8 cups drank", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (waterLoggedCups > 0) { waterLoggedCups--; haptic.performHapticFeedback(HapticFeedbackType.LongPress) } }) {
                    Icon(Icons.Default.RemoveCircle, "Minus", tint = Color.LightGray)
                }
                Text("$waterLoggedCups", fontWeight = FontWeight.Black, fontSize = 18.sp)
                IconButton(onClick = { waterLoggedCups++; viewModel.awardXp(5); haptic.performHapticFeedback(HapticFeedbackType.LongPress) }) {
                    Icon(Icons.Default.AddCircle, "Plus", tint = WaterBlue)
                }
            }
        }

        // BREATHING
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MindPurple.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, MindPurple.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MINDFUL BREATHING", color = MindPurple, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .size((100 * breathScale).dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MindPurple)
                        .clickable {
                            if (!breathingActive) {
                                breathingActive = true
                                scope.launch {
                                    while (breathingActive) {
                                        breathingText = "INHALE..."; breathScale = 1.4f; haptic.performHapticFeedback(HapticFeedbackType.LongPress); delay(4000)
                                        breathingText = "HOLD..."; haptic.performHapticFeedback(HapticFeedbackType.LongPress); delay(4000)
                                        breathingText = "EXHALE..."; breathScale = 1f; haptic.performHapticFeedback(HapticFeedbackType.LongPress); delay(4000)
                                    }
                                }
                            } else {
                                breathingActive = false; breathingText = "TAP TO BREATHE"; breathScale = 1f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(breathingText, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Aligns your heartbeat & counts towards mindful minutes.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }

}

@Composable
fun DashboardScreenGoalLegendRow(
    title: String,
    progress: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(progress, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
