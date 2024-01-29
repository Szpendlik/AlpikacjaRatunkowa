package com.example.alpikacjaratunkowa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class AlertActivity : AppCompatActivity() {

    private lateinit var emergencyAlert: EmergencyAlertManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emergencyAlert = EmergencyAlertManager(this)
        Log.d("Alert","Alert Activity onCreate")
        emergencyAlert.startEmergencyAlert(10000, "507480247", null)

    }
}