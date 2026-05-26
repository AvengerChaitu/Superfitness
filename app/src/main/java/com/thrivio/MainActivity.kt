package com.thrivio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thrivio.auth.AuthScreen
import com.thrivio.auth.AuthViewModel
import com.thrivio.service.StepCounterService
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.navigation.ThrivioNavigationBar
import com.thrivio.ui.navigation.ThrivioScreen
import com.thrivio.ui.screens.*
import com.thrivio.ui.theme.ThrivioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startStepTrackingService()

        setContent {
            ThrivioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isSignedIn by remember { mutableStateOf(false) }

                    if (isSignedIn) {
                        val dashboardViewModel: DashboardViewModel = viewModel()
                        MainScaffold(viewModel = dashboardViewModel)
                    } else {
                        val authViewModel: AuthViewModel = viewModel()
                        AuthScreen(
                            viewModel = authViewModel,
                            onSignedIn = { isSignedIn = true }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) {
            // Supabase OAuth redirect
        }
    }

    private fun startStepTrackingService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}

@Composable
fun MainScaffold(viewModel: DashboardViewModel) {
    var currentScreen by remember { mutableStateOf(ThrivioScreen.Home) }

    Scaffold(
        bottomBar = {
            ThrivioNavigationBar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                ThrivioScreen.Home -> DashboardScreen(viewModel = viewModel)
                ThrivioScreen.Workout -> WorkoutScreen(viewModel = viewModel)
                ThrivioScreen.Nutrition -> NutritionScreen(viewModel = viewModel)
                ThrivioScreen.Mind -> MindScreen(viewModel = viewModel)
                ThrivioScreen.Profile -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
