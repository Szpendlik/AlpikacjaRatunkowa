package com.example.alpikacjaratunkowa
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.widget.Toast

class EmergencyAlertManager(private val context: Context) {
    private lateinit var countDownTimer: CountDownTimer
    private var isAlertShown = false

    fun startEmergencyAlert(countdownDuration: Long, phoneNumber: String, lastSeenLocation: Location?) {
        if (!isAlertShown) {
            showAlert(countdownDuration, phoneNumber, null, lastSeenLocation)
        }
    }

    private fun showAlert(countdownDuration: Long, phoneNumber: String, reason: String?, location: Location?) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Emergency Alert")
            .setMessage("Wykryto $reason. Czy potrzebujesz pomocy? Kliknij OK, aby anulować.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                cancelAlert()
            }
            .create()

        countDownTimer = object : CountDownTimer(countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Wyświetlanie pozostałego czasu
                val secondsRemaining = millisUntilFinished / 1000
                alertDialog.setMessage("Wykryto $reason. Czy potrzebujesz pomocy? Kliknij OK, aby anulować. Pozostały czas: $secondsRemaining s")
            }

            override fun onFinish() {
                // Automatyczne wysłanie SMS-a po zakończeniu odliczania
                sendEmergencySMS(phoneNumber, reason, location)
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

    private fun sendEmergencySMS(phoneNumber: String, reason: String?, location: Location?) {
//        try {
            val smsManager = SmsManager.getDefault()
        var message: String
        val stringBuilder = StringBuilder()
        val stringBuilder2 = StringBuilder()
        val stringBuilder3 = StringBuilder()
        if (reason != null) {
            stringBuilder.append("My Accident Detection App has detected a potential accident ($reason). ")
        } else {
            stringBuilder.append("My Accident Detection App has detected a potential accident. ")
        }
        if (location != null){
            stringBuilder2.append("Location: ${location.latitude}, ${location.longitude}")
            stringBuilder3.append(SMSMessageUtils.getCity(location.latitude, location.longitude, context))
        }
        stringBuilder.append("Please check on me immediately. " +
                "If no response, please contact emergency services. ")
        println(stringBuilder.toString())
        val stringMessage : String = stringBuilder.toString()
        val stringMessage2 : String = stringBuilder2.toString()
        val stringMessage3 : String = stringBuilder3.toString()

        smsManager.sendTextMessage(phoneNumber, null, stringMessage, null, null)
        smsManager.sendTextMessage(phoneNumber, null, stringMessage2, null, null)
        smsManager.sendTextMessage(phoneNumber, null, stringMessage3, null, null)
        Toast.makeText(context, "SMS has been sended", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(context, "Wystąpił błąd podczas wysyłania SMS-a", Toast.LENGTH_SHORT).show()
      //  }
    }
}
