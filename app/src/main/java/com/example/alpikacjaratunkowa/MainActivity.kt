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
import android.content.SharedPreferences
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var settingsButton: Button
    private lateinit var modeSwitch: Switch
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var switchCount: Int = 1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, MyServices::class.java))

        settingsButton = findViewById(R.id.settingsButton)
        modeSwitch = findViewById(R.id.modeSwitch)
        constraintLayout = findViewById(R.id.constraintLayout)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("switchCount", 0).apply()
        switchCount = sharedPreferences.getInt("switchCount", 0)

        settingsButton.setOnClickListener {
            openSettingsActivity()
        }

        modeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Ustaw tło w zależności od trybu
            if (isChecked) {
                // Tryb górski
                constraintLayout.setBackgroundResource(R.drawable.bg2)
            } else {
                // Tryb samochodowy
                constraintLayout.setBackgroundResource(R.drawable.bg)
            }

            // Zwiększ licznik przełączeń
            if(switchCount==16)
            {
                sharedPreferences.edit().putInt("switchCount", 0).apply()
                switchCount = 0
            }

            switchCount++

            sharedPreferences.edit().putInt("switchCount", switchCount).apply()


            // Sprawdź, czy osiągnięto warunki dla Easter Egg
            checkEasterEgg()
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
    private fun checkEasterEgg() {
        if (switchCount == 10) {
            // Toast "Już prawie!" przy 5 przełączeniach
            Toast.makeText(this, "Już prawie!", Toast.LENGTH_SHORT).show()
        } else if (switchCount == 15) {
            // Toast "Tryb samolotowy" przy 10 przełączeniach
            Toast.makeText(this, "Tryb samolotowy", Toast.LENGTH_SHORT).show()

            // Zmiana tła na bg5.png
            constraintLayout.setBackgroundResource(R.drawable.bg5)

        }
    }

    private fun openSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}
