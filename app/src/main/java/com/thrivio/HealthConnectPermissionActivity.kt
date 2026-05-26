package com.thrivio

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class HealthConnectPermissionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // In a complete app, show a details dialog explaining why Thrivio requests health data.
        Toast.makeText(this, "Thrivio imports steps to automatically reward you with XP!", Toast.LENGTH_LONG).show()
        finish()
    }
}
