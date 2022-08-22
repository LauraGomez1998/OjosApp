package net.tesa.ojosapp.model

import net.tesa.ojosapp.util.CalendarUtil
import net.tesa.ojosapp.util.CalendarUtilsProtocol
import java.util.*

class Practice(
    var startDate: Date,
    var practiceDurationMills: Long,
    var running: Boolean = false,
    var stopDate: Date? = null
) {

    companion object {
        val calendarUtils: CalendarUtilsProtocol = CalendarUtil()
        fun getPracticeType(): PracticeType? {
            return when (calendarUtils.dateOfWeek()) {
                CalendarUtil.DayOfWeek.MONDAY, CalendarUtil.DayOfWeek.WEDNESDAY, CalendarUtil.DayOfWeek.FRIDAY -> {
                    PracticeType.RED
                }
                CalendarUtil.DayOfWeek.TUESDAY, CalendarUtil.DayOfWeek.THURSDAY, CalendarUtil.DayOfWeek.SATURDAY -> {
                    PracticeType.GREEN
                }
                CalendarUtil.DayOfWeek.SUNDAY -> {
                    PracticeType.REST
                }
                else -> null
            }
        }
    }

    fun resumePractice() {
        running = true
        startDate = calendarUtils.now()
        stopDate = null
    }

    fun stopPractice() {
        running = false
        stopDate = calendarUtils.now()
        practiceDurationMills = calculateNewPracticeDuration()
    }

    fun validateLastPracticeDate(): Practice? {
        if (calendarUtils.isSameDay(startDate)) {
            return this
        }
        return null
    }

    /**
     * calculate remaining time to finish the practice
     *
     * return@ time in minutes
     */
    private fun calculateNewPracticeDuration(): Long {
        return practiceDurationMills - calendarUtils.datesTimeDifference(
            startDate,
            stopDate!!
        )
    }
}