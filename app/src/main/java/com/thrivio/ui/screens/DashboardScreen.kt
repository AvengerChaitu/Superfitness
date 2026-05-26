package com.thrivio.ui.screens

import androidx.compose.animation.*
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
import com.thrivio.ui.components.TactileButton
import com.thrivio.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // State Variables
    var userSteps by remember { mutableStateOf(5600) }
    var userStreak by remember { mutableStateOf(7) }
    var userXp by remember { mutableStateOf(1240) }
    var waterLoggedCups by remember { mutableStateOf(6) }
    var breathingActive by remember { mutableStateOf(false) }
    var breathingText by remember { mutableStateOf("TAP TO BREATHE") }
    var breathScale by remember { mutableStateOf(1f) }
    var showPremiumPaywall by remember { mutableStateOf(false) }

    // Constants
    val stepGoal = 8000
    val calorieGoal = 2000
    val caloriesBurned = 1180
    val waterGoal = 8
    val sleepDuration = 6.1f // 6h 5m
    val sleepGoal = 7f

    Scaffold(
        bottomBar = {
            // Premium Floating Glassmorphic Nav Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = MascotGreen, modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = "Workout", tint = Color.Gray, modifier = Modifier.size(26.dp))
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Restaurant, contentDescription = "Nutrition", tint = Color.Gray, modifier = Modifier.size(26.dp))
                    }
                    IconButton(onClick = { showPremiumPaywall = true }) {
                        Icon(Icons.Default.Stars, contentDescription = "Premium", tint = XpOrange, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ==========================================
            // HEADER: STREAK & XP WIDGETS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mascot & Greeting
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(XpOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐼", fontSize = 30.sp) // Aero Mascot avatar
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Good morning,",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rahul!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Stats Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // XP Chip
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
                        Text(text = "$userXp XP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Streak Flame Chip
                    Row(
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .background(SurfaceCardLight, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderOutlineLight, RoundedCornerShape(12.dp))
                            .clickable { userStreak++ }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, "Streak", tint = Color(0xFFFF5722), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$userStreak DAYS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // ==========================================
            // STREAK HERO CARD (DUOLINGO 3D GRADIENT)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(XpOrange, Color(0xFFFF7A00))
                        )
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        userSteps += 500
                        userXp += 20
                    }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$userStreak-DAY STREAK!",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Aero is cheering! Keep walking to win FitCoins.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Log +500 steps (Tap to cheat)",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    // Bouncy Level Circle
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Lv.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("7", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // ==========================================
            // CONCENTRIC GOALS RINGS (CANVAS DRAWING)
            // ==========================================
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
                // Interactive concentric rings on Canvas
                Canvas(modifier = Modifier.size(140.dp)) {
                    val strokeWidth = 10.dp.toPx()
                    val gap = 6.dp.toPx()
                    val ringSize = 140.dp.toPx()

                    // Ring 1: Steps (Outer - Neon Blue)
                    val r1Percent = (userSteps.toFloat() / stepGoal).coerceIn(0f, 1f)
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = FitnessBlue,
                        startAngle = -90f,
                        sweepAngle = r1Percent * 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )

                    // Ring 2: Calories (Middle - Coral Orange)
                    val r2Percent = (caloriesBurned.toFloat() / calorieGoal).coerceIn(0f, 1f)
                    val r2Diameter = ringSize - (strokeWidth * 2) - (gap * 2)
                    val r2Offset = strokeWidth + gap
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(r2Offset, r2Offset),
                        size = Size(r2Diameter, r2Diameter)
                    )
                    drawArc(
                        color = Color(0xFFFF5722),
                        startAngle = -90f,
                        sweepAngle = r2Percent * 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(r2Offset, r2Offset),
                        size = Size(r2Diameter, r2Diameter)
                    )

                    // Ring 3: Sleep (Inner - Purple)
                    val r3Percent = (sleepDuration / sleepGoal).coerceIn(0f, 1f)
                    val r3Diameter = r2Diameter - (strokeWidth * 2) - (gap * 2)
                    val r3Offset = r2Offset + strokeWidth + gap
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(r3Offset, r3Offset),
                        size = Size(r3Diameter, r3Diameter)
                    )
                    drawArc(
                        color = MindPurple,
                        startAngle = -90f,
                        sweepAngle = r3Percent * 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(r3Offset, r3Offset),
                        size = Size(r3Diameter, r3Diameter)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Stats Legend list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GoalLegendRow("Steps", "$userSteps / $stepGoal", FitnessBlue, Icons.Default.DirectionsRun)
                    GoalLegendRow("Kcal", "$caloriesBurned / $calorieGoal", Color(0xFFFF5722), Icons.Default.LocalFireDepartment)
                    GoalLegendRow("Water", "$waterLoggedCups / $waterGoal cups", WaterBlue, Icons.Default.LocalActivity)
                    GoalLegendRow("Sleep", "6h 5m / ${sleepGoal.toInt()}h", MindPurple, Icons.Default.NightsStay)
                }
            }

            // ==========================================
            // TODAY AT A GLANCE (WORKOUTS & CALORIE LOGS)
            // ==========================================
            Text(text = "TODAY AT A GLANCE", style = MaterialTheme.typography.titleMedium)

            // Workout card
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
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(XpOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, "Workout", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Chest & Triceps", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "12 exercises · 48 min", color = Color.Gray, fontSize = 13.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MascotGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, "Done", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            // ==========================================
            // WATER REFUELING INTERACTIVE DRAWER
            // ==========================================
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
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WaterBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💧", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Hydration Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "$waterLoggedCups of $waterGoal cups drank", color = Color.Gray, fontSize = 13.sp)
                    }
                }
                
                // Interactive Incrementer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (waterLoggedCups > 0) {
                                waterLoggedCups--
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    ) {
                        Icon(Icons.Default.RemoveCircle, "Minus", tint = Color.LightGray)
                    }
                    Text(text = "$waterLoggedCups", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    IconButton(
                        onClick = {
                            waterLoggedCups++
                            userXp += 5
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    ) {
                        Icon(Icons.Default.AddCircle, "Plus", tint = WaterBlue)
                    }
                }
            }

            // ==========================================
            // INTERACTIVE BREATHING EXERCISE CORE
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MindPurple.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, MindPurple.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MINDFUL BREATHING HELPER",
                        color = MindPurple,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    
                    // The breathing visual circle
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
                                        // Rhythmic Breathing simulation: Inhale 4s -> Hold 4s -> Exhale 4s
                                        while (breathingActive) {
                                            breathingText = "INHALE..."
                                            breathScale = 1.4f
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            delay(4000)

                                            breathingText = "HOLD..."
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            delay(4000)

                                            breathingText = "EXHALE..."
                                            breathScale = 1f
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            delay(4000)
                                        }
                                    }
                                } else {
                                    breathingActive = false
                                    breathingText = "TAP TO BREATHE"
                                    breathScale = 1f
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = breathingText,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Aligns your heartbeat & counts towards mindful minutes.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ==========================================
            // LIFETIME PREMIUM UNLOCK (Razorpay Gateway gate)
            // ==========================================
            if (showPremiumPaywall) {
                AlertDialog(
                    onDismissRequest = { showPremiumPaywall = false },
                    title = {
                        Text(
                            "🔑 UNLOCK THRIVIO LIFETIME",
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👑", fontSize = 50.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Get unlimited streak freezes, special avatars for Aero, and participate in Obsidian & Diamond competitive leagues!",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "One-Time Purchase: ₹499 (Lifetime Access)",
                                fontWeight = FontWeight.Bold,
                                color = MascotGreen,
                                fontSize = 16.sp
                            )
                        }
                    },
                    confirmButton = {
                        TactileButton(
                            text = "PAY NOW VIA UPI",
                            primaryColor = MascotGreen,
                            shadowColor = MascotGreenShadow,
                            onClick = {
                                showPremiumPaywall = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { showPremiumPaywall = false }) {
                            Text("MAYBE LATER", color = Color.Gray)
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = SurfaceCardLight
                )
            }
        }
    }
}

@Composable
fun GoalLegendRow(
    title: String,
    progress: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = progress, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
