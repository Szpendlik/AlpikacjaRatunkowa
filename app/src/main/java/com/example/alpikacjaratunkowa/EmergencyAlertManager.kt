package com.example.alpikacjaratunkowa
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.widget.Toast

class EmergencyAlertManager(private val context: Context) {
    private lateinit var countDownTimer: CountDownTimer
    private var isAlertShown = false

    fun startEmergencyAlert(countdownDuration: Long, phoneNumber: String) {
        if (!isAlertShown) {
            showAlert(countdownDuration, phoneNumber)
        }
    }

    private fun showAlert(countdownDuration: Long, phoneNumber: String) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Emergency Alert")
            .setMessage("Czy potrzebujesz pomocy? Kliknij OK, aby anulować.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                cancelAlert()
            }
            .create()

        countDownTimer = object : CountDownTimer(countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Wyświetlanie pozostałego czasu
                val secondsRemaining = millisUntilFinished / 1000
                alertDialog.setMessage("Czy potrzebujesz pomocy? Kliknij OK, aby anulować. Pozostały czas: $secondsRemaining s")
            }

            override fun onFinish() {
                // Automatyczne wysłanie SMS-a po zakończeniu odliczania
                sendEmergencySMS(phoneNumber)
                alertDialog.dismiss()
                isAlertShown = false
            }
        }

        alertDialog.show()
        countDownTimer.start()
        isAlertShown = true
    }

    private fun cancelAlert() {
        countDownTimer.cancel()
        isAlertShown = false
        Toast.makeText(context, "Alert anulowany", Toast.LENGTH_SHORT).show()
    }

    private fun sendEmergencySMS(phoneNumber: String) {
//        try {
            val smsManager = SmsManager.getDefault()
            val message = "Pomusz mi proszę"
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "SMS wysłany", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(context, "Wystąpił błąd podczas wysyłania SMS-a", Toast.LENGTH_SHORT).show()
      //  }
    }
}
