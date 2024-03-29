package com.example.alpikacjaratunkowa


import android.content.Context

import android.media.MediaPlayer

import android.location.Location
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
class EmergencyAlertManager(private val context: Context) {
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var alertTimer: CountDownTimer
    private var isAlertShown = false
    private lateinit var sharedPreferences: SharedPreferences
    private var switchCount: Int = 1

    init {
        // Inicjalizacja sharedPreferences w konstruktorze
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    fun startEmergencyAlert(countdownDuration: Long, phoneNumber: String, lastSeenLocation: Location?, locationString: String?) {
        if (!isAlertShown) {
            Log.d("ALERT", "Goedemorgen")
            showAlert((countdownDuration)*1000, phoneNumber, null, lastSeenLocation, locationString)
        }
    }



    private fun showAlert(countdownDuration: Long, phoneNumber: String, reason: String?, location: Location?, locationString: String?) {
        // Dostosowanie stylu okna alertu
        val alertDialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)


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
                switchCount = sharedPreferences.getInt("switchCount", 0)
                if(switchCount == 15)
                {
                    alertDialog.setMessage("Do lądowania pozostało: $secondsRemaining s")
                }
                else
                {
                    alertDialog.setMessage("Wykryto wypadek. Czy potrzebujesz pomocy? Kliknij OK, aby anulować. Pozostały czas: $secondsRemaining s")
                }

            }

            override fun onFinish() {
                // Automatyczne wysłanie SMS-a po zakończeniu odliczania
                sendEmergencySMS(phoneNumber, reason, location, locationString)
                alertDialog.dismiss()
                isAlertShown = false
            }
        }

        alertTimer = object : CountDownTimer(countdownDuration, 500) {
            override fun onTick(millisUntilFinished: Long) {
                // Odtwarzanie dźwięku i wibracji co 500 ms
                playAlertSound()
                vibrate()
            }

            override fun onFinish() {
                // Zakończenie odtwarzania dźwięku i wibracji po zakończeniu alertu
                stopAlertSound()
                stopVibration()
            }
        }

        // Dostosowanie tła i koloru przycisków
        alertDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.alert_dialog_bg))
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.alert_button_color))
        }

        alertDialog.show()
        countDownTimer.start()
        alertTimer.start()
        isAlertShown = true
    }

    private fun cancelAlert() {
        countDownTimer.cancel()
        alertTimer.cancel()
        isAlertShown = false
        stopAlertSound()
        stopVibration()
        Toast.makeText(context, "Alert anulowany", Toast.LENGTH_SHORT).show()
    }



    private var mediaPlayer: MediaPlayer? = null

    private fun playAlertSound() {
        switchCount = sharedPreferences.getInt("switchCount", 0)
        if(switchCount == 15)
        {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.believe)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }
        else
        {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.alert_sound)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }

    }

    private fun stopAlertSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }

    private fun stopVibration() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
    private fun sendEmergencySMS(phoneNumber: String, reason: String?, location: Location?, locationString: String?) {

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
        } else if (locationString != null){
            val parts = locationString.split("/")

            val latitude = parts[0].toDouble()
            val longitude = parts[1].toDouble()
            stringBuilder2.append("Location: $latitude, $longitude")
            stringBuilder3.append(SMSMessageUtils.getCity(latitude, longitude, context))
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


    

    }
}
