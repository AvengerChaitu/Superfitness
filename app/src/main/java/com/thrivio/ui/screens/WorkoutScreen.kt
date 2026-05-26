package com.thrivio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.theme.*

data class Exercise(val name: String, val sets: Int, val reps: Int, val icon: String)

@Composable
fun WorkoutScreen(viewModel: DashboardViewModel) {
    val workouts by viewModel.recentWorkouts.collectAsState()
    val haptic = LocalHapticFeedback.current
    var showLogDialog by remember { mutableStateOf(false) }
    var selectedChip by remember { mutableStateOf("All") }

    val categories = listOf("All", "Chest", "Back", "Legs", "Arms", "Shoulders", "Core")

    val exercises = listOf(
        Exercise("Bench Press", 4, 10, "🏋️"),
        Exercise("Squat", 4, 8, "🏋️"),
        Exercise("Deadlift", 3, 6, "🏋️"),
        Exercise("Pull Up", 3, 12, "🤸"),
        Exercise("Push Up", 3, 15, "💪"),
        Exercise("Plank", 3, 60, "🧘")
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogDialog = true },
                containerColor = MascotGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.FitnessCenter, "Log workout")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("WORKOUT", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }

            // Categories
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedChip == cat,
                        onClick = { selectedChip = cat; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                        label = { Text(cat, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = XpOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(exercises) { ex ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                        border = BorderStroke(1.dp, BorderOutlineLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ex.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ex.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${ex.sets}x${ex.reps} reps", color = Color.Gray, fontSize = 13.sp)
                            }
                            Icon(Icons.Default.PlayCircle, "Start", tint = MascotGreen, modifier = Modifier.size(32.dp))
                        }
                    }
                }

                if (workouts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recent Workouts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                    }
                    items(workouts) { w ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                            border = BorderStroke(1.dp, BorderOutlineLight)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MascotGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, null, tint = MascotGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(w.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("${w.durationSeconds / 60} min · ${w.caloriesBurned} cal", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showLogDialog) {
        LogWorkoutDialog(
            onDismiss = { showLogDialog = false },
            onSave = { name, duration, calories ->
                viewModel.logWorkout(name, duration, calories)
                showLogDialog = false
            }
        )
    }
}

@Composable
fun LogWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Workout", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Workout name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (min)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories burned") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val d = duration.toIntOrNull() ?: return@TextButton
                    val c = calories.toIntOrNull() ?: 0
                    onSave(name, d, c)
                },
                enabled = name.isNotBlank() && duration.isNotBlank()
            ) { Text("Save", color = MascotGreen, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
