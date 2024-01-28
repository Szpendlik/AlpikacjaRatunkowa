package com.example.alpikacjaratunkowa


import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
class EmergencyAlertManager(private val context: Context) {
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var alertTimer: CountDownTimer
    private var isAlertShown = false

    fun startEmergencyAlert(countdownDuration: Long, phoneNumber: String) {
        if (!isAlertShown) {
            showAlert(countdownDuration, phoneNumber)
        }
    }

    private fun showAlert(countdownDuration: Long, phoneNumber: String) {
        // Dostosowanie stylu okna alertu
        val alertDialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
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

    private fun sendEmergencySMS(phoneNumber: String) {
        val smsManager = SmsManager.getDefault()
        val message = "Pomusz mi proszę"
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Toast.makeText(context, "SMS wysłany", Toast.LENGTH_SHORT).show()
    }

    private var mediaPlayer: MediaPlayer? = null

    private fun playAlertSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alert_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
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
}
