package com.example.alpikacjaratunkowa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var settingsButton: Button
    private lateinit var modeSwitch: Switch
    private lateinit var constraintLayout: ConstraintLayout // Dodana deklaracja zmiennej constraintLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, MyServices::class.java))

        settingsButton = findViewById(R.id.settingsButton)
        modeSwitch = findViewById(R.id.modeSwitch)
        constraintLayout = findViewById(R.id.constraintLayout) // Inicjalizacja zmiennej constraintLayout

        settingsButton.setOnClickListener {
            openSettingsActivity()
        }

        // Ustaw nasłuchiwacz zmiany trybu w przełączniku
        modeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Tryb górski
                constraintLayout.setBackgroundResource(R.drawable.bg2)

            } else {
                // Tryb samochodowy
                constraintLayout.setBackgroundResource(R.drawable.bg)

            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.SEND_SMS),
                2
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.VIBRATE),
                3
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }
    }

    private fun openSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}
