package com.thrivio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.razorpay.PaymentResultListener
import com.thrivio.auth.AuthScreen
import com.thrivio.auth.AuthViewModel
import com.thrivio.service.StepCounterService
import com.thrivio.ui.DashboardViewModel
import com.thrivio.ui.navigation.ThrivioNavigationBar
import com.thrivio.ui.navigation.ThrivioScreen
import com.thrivio.ui.screens.*
import com.thrivio.ui.theme.ThrivioTheme

class MainActivity : ComponentActivity(), PaymentResultListener {

    private var currentScreenOverride: String? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startStepTrackingService()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            ThrivioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isSignedIn by remember { mutableStateOf(false) }
                    var showPremium by remember { mutableStateOf(false) }
                    var showBarcodeScanner by remember { mutableStateOf(false) }
                    var scannedBarcode by remember { mutableStateOf<String?>(null) }

                    if (showPremium) {
                        PremiumScreen(onBack = { showPremium = false })
                    } else if (showBarcodeScanner) {
                        BarcodeScannerScreen(
                            onBarcodeScanned = { code ->
                                scannedBarcode = code
                                showBarcodeScanner = false
                            },
                            onBack = { showBarcodeScanner = false }
                        )
                    } else if (isSignedIn) {
                        val dashboardViewModel: DashboardViewModel = viewModel()
                        MainScaffold(
                            viewModel = dashboardViewModel,
                            onPremiumClick = { showPremium = true },
                            onBarcodeClick = { showBarcodeScanner = true }
                        )
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
            val uri = intent.data
            if (uri?.scheme == "thrivio" && uri.host == "payment-success") {
                currentScreenOverride = "premium"
            }
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        setContent {
            ThrivioTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PremiumScreen(onBack = { finish() })
                }
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?) { }

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
fun MainScaffold(
    viewModel: DashboardViewModel,
    onPremiumClick: () -> Unit,
    onBarcodeClick: () -> Unit
) {
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
                ThrivioScreen.Nutrition -> NutritionScreen(
                    viewModel = viewModel,
                    onBarcodeClick = onBarcodeClick
                )
                ThrivioScreen.Mind -> MindScreen(viewModel = viewModel)
                ThrivioScreen.Profile -> ProfileScreen(
                    viewModel = viewModel,
                    onPremiumClick = onPremiumClick
                )
            }
        }
    }
}
