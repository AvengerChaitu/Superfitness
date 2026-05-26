package com.thrivio.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.razorpay.PaymentResultListener
import com.thrivio.payment.PaymentManager
import com.thrivio.ui.components.TactileButton
import com.thrivio.ui.theme.*

@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val paymentManager = remember { PaymentManager(context) }
    var isPremium by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isPremium = paymentManager.isPremium()
    }

    if (activity is PaymentResultListener) {
        // Handled by the activity
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text("👑", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text("THRIVIO PREMIUM", fontWeight = FontWeight.Black, fontSize = 26.sp, color = XpOrange)
        Text("Lifetime Access · One-Time Payment", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        if (isPremium) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MascotGreen.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, MascotGreen)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = MascotGreen, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You're a Premium Member!", fontWeight = FontWeight.Black, color = MascotGreen, fontSize = 18.sp)
                }
            }
        } else {
            // Benefits
            PremiumBenefitCard("Streak Freezes", "Never lose your streak — freeze it up to 3 times per month")
            PremiumBenefitCard("Special Avatars", "Unlock exclusive Aero the Panda outfits and themes")
            PremiumBenefitCard("Competitive Leagues", "Compete in Obsidian & Diamond leagues with real rewards")
            PremiumBenefitCard("Advanced Analytics", "Detailed charts for sleep, steps, calories, and macros")
            PremiumBenefitCard("No Ads", "Ad-free experience forever")

            Spacer(modifier = Modifier.height(20.dp))

            // Price card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
                border = BorderStroke(2.dp, XpOrange)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("₹499", fontWeight = FontWeight.Black, fontSize = 44.sp, color = XpOrange)
                    Text("Lifetime · One Payment", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    TactileButton(
                        text = "UNLOCK PREMIUM",
                        primaryColor = MascotGreen,
                        shadowColor = MascotGreenShadow,
                        onClick = {
                            activity?.let {
                                paymentManager.startPayment(it) { success ->
                                    if (success) {
                                        isPremium = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        Text(
            paymentStatus,
            color = if (paymentStatus.contains("Success")) MascotGreen else Color.Red,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back to Home")
        }
    }
}

@Composable
fun PremiumBenefitCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
        border = BorderStroke(1.dp, BorderOutlineLight)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(XpOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Stars, null, tint = XpOrange, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(description, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
