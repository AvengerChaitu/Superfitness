package com.thrivio.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.thrivio.ui.screens.*

enum class ThrivioScreen(
    val label: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Default.Home),
    Workout("Workout", Icons.Default.FitnessCenter),
    Nutrition("Nutrition", Icons.Default.Restaurant),
    Mind("Mind", Icons.Default.SelfImprovement),
    Profile("Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThrivioNavigationBar(
    currentScreen: ThrivioScreen,
    onScreenSelected: (ThrivioScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        ThrivioScreen.entries.forEach { screen ->
            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(screen.icon, contentDescription = screen.label)
                },
                label = {
                    Text(screen.label, style = MaterialTheme.typography.labelSmall)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = com.thrivio.ui.theme.MascotGreen,
                    selectedTextColor = com.thrivio.ui.theme.MascotGreen,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
