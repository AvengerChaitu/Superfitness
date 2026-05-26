package com.thrivio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.theme.*

@Composable
fun NutritionScreen(viewModel: DashboardViewModel) {
    val meals by viewModel.todayMeals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalCals = meals.sumOf { it.totalCalories }
    val totalProtein = meals.sumOf { it.totalProtein.toDouble() }
    val totalCarbs = meals.sumOf { it.totalCarbs.toDouble() }
    val totalFat = meals.sumOf { it.totalFat.toDouble() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MascotGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Log meal")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("NUTRITION", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)

            // Macros summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                border = BorderStroke(1.dp, BorderOutlineLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Intake", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroBadge("Calories", "$totalCals", XpOrange)
                        MacroBadge("Protein", "${totalProtein.toInt()}g", Color(0xFF4CAF50))
                        MacroBadge("Carbs", "${totalCarbs.toInt()}g", FitnessBlue)
                        MacroBadge("Fat", "${totalFat.toInt()}g", Color(0xFFFF5722))
                    }
                }
            }

            if (meals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Restaurant, null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No meals logged today", color = Color.Gray)
                        Text("Tap + to add a meal", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                meals.forEach { meal ->
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
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MascotGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(meal.mealType.first().toString(), fontWeight = FontWeight.Black, color = MascotGreen)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(meal.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(meal.mealType, color = Color.Gray, fontSize = 12.sp)
                            }
                            Text("${meal.totalCalories} cal", fontWeight = FontWeight.Bold, color = XpOrange)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMealDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, type, cals, protein, carbs, fat ->
                viewModel.logMeal(name, type, cals, protein, carbs, fat)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MacroBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = color)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Float, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Breakfast") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Meal", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Meal name") }, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { mealType = type; expanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Fat (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cals = calories.toIntOrNull() ?: return@TextButton
                    val p = protein.toFloatOrNull() ?: 0f
                    val c = carbs.toFloatOrNull() ?: 0f
                    val f = fat.toFloatOrNull() ?: 0f
                    onSave(name, mealType, cals, p, c, f)
                },
                enabled = name.isNotBlank() && calories.isNotBlank()
            ) { Text("Save", color = MascotGreen, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
