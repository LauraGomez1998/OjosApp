package net.tesa.ojosapp.model

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import net.tesa.ojosapp.datasource.PracticeDatasourceImpl
import net.tesa.ojosapp.view.helper.AlarmHelper
import net.tesa.ojosapp.view.helper.NotificationHelper


open class BroadcastTimerService : Service() {

    companion object {
        const val COUNTDOWN_BR = "timer_intent_service"

        const val ACTION_START = "net.veritran.ojosapp.ACTION_START"
        const val ACTION_STOP = "net.veritran.ojosapp.ACTION_STOP"
        const val ACTION_CANCEL = "net.veritran.ojosapp.ACTION_CANCEL"
        var cdt: CountDownTimer? = null
    }

    var millsTimerReminding: Long = 0
    var bi = Intent(COUNTDOWN_BR)

    override fun onDestroy() {
        val datasourceInterface = PracticeDatasourceImpl(applicationContext)
        datasourceInterface.stopPractice()
        if (cdt != null) cdt?.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.extras?.get("action").toString()) {
            ACTION_START -> {
                val newTime = intent?.extras?.get("time")
                if (newTime != null) {
                    millsTimerReminding = newTime as Long
                }
                setupCountdownTimer(millsTimerReminding)
                cdt!!.start()
            }
            ACTION_STOP -> {
                val datasourceInterface = PracticeDatasourceImpl(applicationContext)
                datasourceInterface.stopPractice()
                cdt!!.cancel()
            }
            ACTION_CANCEL -> {
                cdt!!.cancel()
                millsTimerReminding = 1800000
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun setupCountdownTimer(mills: Long) {
        var almostFinished = false
        cdt = object : CountDownTimer(mills, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                millsTimerReminding = millisUntilFinished

                if (millisUntilFinished < 60000 && !almostFinished) {
                    almostFinished = true
                    NotificationHelper.sendLocalNotification(
                        applicationContext,
                        "Ya casi terminamos"
                    )
                }

                bi.putExtra("countdown", millisUntilFinished)
                sendBroadcast(bi)
            }

            override fun onFinish() {
                Log.d("BroadcastService", "Timer finished")
                val datasourceInterface = PracticeDatasourceImpl(applicationContext)
                datasourceInterface.cancelPractice()
                AlarmHelper.playRingtone()
            }
        }
    }
}

