package com.thrivio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thrivio.auth.AuthScreen
import com.thrivio.auth.AuthViewModel
import com.thrivio.service.StepCounterService
import com.thrivio.ui.screens.DashboardScreen
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
                        DashboardScreen()
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
        // Handle OAuth deep link callback
        if (intent.action == Intent.ACTION_VIEW) {
            // Supabase OAuth redirect handled here in future
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
