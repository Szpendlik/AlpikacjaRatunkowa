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

    private var originalPhoneNumber: String = ""
    private var originalAlertDuration: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        saveButton = findViewById(R.id.saveButton)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        alertDurationEditText = findViewById(R.id.alertDurationEditText)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        loadSettings()

        originalPhoneNumber = phoneNumberEditText.text.toString()
        originalAlertDuration = alertDurationEditText.text.toString()

        phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }
        })

        alertDurationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 > 5) {
                    s?.delete(5, s.length)
                }
                updateSaveButtonState()
            }
        })

        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun updateSaveButtonState() {
        val phoneNumberValid = isValidPhoneNumber(phoneNumberEditText.text.toString())
        val alertDurationValid = isValidAlertDuration(alertDurationEditText.text.toString())
        val phoneNumberChanged = phoneNumberEditText.text.toString() != originalPhoneNumber
        val alertDurationChanged = alertDurationEditText.text.toString() != originalAlertDuration
        saveButton.isEnabled = phoneNumberValid && alertDurationValid && (phoneNumberChanged || alertDurationChanged)
    }

    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putString("phoneNumber", phoneNumberEditText.text.toString())
        editor.putString("alertDuration", alertDurationEditText.text.toString())
        editor.apply()
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()

        // Aktualizuj oryginalne wartości po zapisie
        originalPhoneNumber = phoneNumberEditText.text.toString()
        originalAlertDuration = alertDurationEditText.text.toString()
    }

    private fun loadSettings() {
        val phoneNumber = sharedPreferences.getString("phoneNumber", "")
        val alertDuration = sharedPreferences.getString("alertDuration", "")

        phoneNumberEditText.setText(phoneNumber)
        alertDurationEditText.setText(alertDuration)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = "^[+]?[0-9]{9,13}\$"
        return phoneNumber.matches(phoneRegex.toRegex())
    }

    private fun isValidAlertDuration(alertDuration: String): Boolean {
        val durationRegex = "^[0-9]{1,5}\$"
        return alertDuration.matches(durationRegex.toRegex())
    }
}
