package com.example.alpikacjaratunkowa

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var phoneNumberEditText: EditText
    private lateinit var alertDurationEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        saveButton = findViewById(R.id.saveButton)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        alertDurationEditText = findViewById(R.id.alertDurationEditText)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        loadSettings()

        // Ustaw nasłuchiwacz zmiany tekstu w polu phoneNumberEditText
        phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Sprawdź poprawność numeru telefonu i ustaw dostępność przycisku Zapisz
                saveButton.isEnabled = isValidPhoneNumber(s.toString())
            }
        })

        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putString("phoneNumber", phoneNumberEditText.text.toString())
        editor.putString("alertDuration", alertDurationEditText.text.toString())
        editor.apply()
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun loadSettings() {
        val phoneNumber = sharedPreferences.getString("phoneNumber", "")
        val alertDuration = sharedPreferences.getString("alertDuration", "")

        phoneNumberEditText.setText(phoneNumber)
        alertDurationEditText.setText(alertDuration)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Sprawdzenie czy numer telefonu ma poprawny format
        val phoneRegex = "^[+]?[0-9]{9,13}\$"
        return phoneNumber.matches(phoneRegex.toRegex())
    }
}
