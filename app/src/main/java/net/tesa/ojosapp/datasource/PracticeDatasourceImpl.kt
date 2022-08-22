package net.tesa.ojosapp.datasource

import android.content.Context
import com.google.gson.Gson
import net.tesa.ojosapp.datasource.helper.SharedPreferencesHelper
import net.tesa.ojosapp.datasource.helper.SharedPreferencesHelper.practice
import net.tesa.ojosapp.model.Practice

class PracticeDatasourceImpl(var context: Context) : PracticeDatasourceInterface {
    private val practiceData = "practice_data"
    private val prefs = SharedPreferencesHelper.customPreference(context, practiceData)

    override fun startPractice(durationInMills: Long) {
        prefs.practice = Gson().toJson(
            Practice(
                startDate = Practice.calendarUtils.now(),
                practiceDurationMills = durationInMills,
                running = true
            )
        )
    }

    override fun resumePractice() {
        val practice = Gson().fromJson(prefs.practice, Practice::class.java)
        practice.resumePractice()
        prefs.practice = Gson().toJson(practice)
    }

    override fun stopPractice() {
        val practice = Gson().fromJson(prefs.practice, Practice::class.java)
        if (practice != null) {
            if (practice.running) {
                practice.stopPractice()
                prefs.practice = Gson().toJson(practice)
            }
        }
    }

    override fun cancelPractice() {
        prefs.practice = null
    }

    override fun getLastPractice(): Practice? {
        val practice: Practice? = Gson().fromJson(prefs.practice, Practice::class.java)
        return practice?.validateLastPracticeDate()
    }
}