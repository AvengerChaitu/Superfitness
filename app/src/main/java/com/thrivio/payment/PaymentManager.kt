package com.thrivio.payment

import android.app.Activity
import android.content.Context
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.thrivio.data.local.AppDatabase
import com.thrivio.network.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.Instant

class PaymentManager(private val context: Context) {

    private val db = AppDatabase.getInstance(context)

    fun startPayment(activity: Activity, onResult: (Boolean) -> Unit) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_live_xxxxxxxxxxxx") // Replace with actual Razorpay key

        val userId = try {
            SupabaseClient.client.auth.currentSessionOrNull()?.user?.id
        } catch (_: Exception) { null } ?: ""

        val options = JSONObject().apply {
            put("name", "Thrivio")
            put("description", "Lifetime Premium")
            put("image", "https://thrivio.app/icon.png")
            put("currency", "INR")
            put("amount", "49900") // ₹499 in paise
            put("prefill", JSONObject().apply {
                put("contact", "")
                put("email", "")
            })
            put("notes", JSONObject().apply {
                put("user_id", userId)
            })
        }

        try {
            checkout.open(activity, options)
        } catch (_: Exception) {
            onResult(false)
        }

        if (activity is PaymentResultListener) {
            // Result handled via onPaymentSuccess/onPaymentError
        }
    }

    suspend fun verifyAndGrantPremium() = withContext(Dispatchers.IO) {
        try {
            val userId = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id ?: return@withContext
            SupabaseClient.client.postgrest.from("user_health_profiles").upsert(
                mapOf(
                    "user_id" to userId,
                    "is_premium" to true,
                    "premium_updated_at" to Instant.now().toString()
                )
            )
        } catch (_: Exception) { }
    }

    suspend fun isPremium(): Boolean {
        return try {
            val userId = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id ?: return false
            val response = SupabaseClient.client.postgrest.from("user_health_profiles")
                .select { filter { eq("user_id", userId) } }
            response.toString().contains("\"is_premium\":true")
        } catch (_: Exception) {
            false
        }
    }
}
