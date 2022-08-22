package net.tesa.ojosapp.view.helper

import android.media.Ringtone

class AlarmHelper {

    companion object {
        lateinit var ringTone: Ringtone
        fun playRingtone() {
            try {
                ringTone.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun stopRingtone() {
            if (ringTone.isPlaying) {
                ringTone.stop()
            }
        }
    }
}