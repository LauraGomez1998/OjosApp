package net.tesa.ojosapp.model

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class PracticeTest {

    private lateinit var sut: Practice

    @Before
    fun setup() {
        sut = Practice(Date(), 20)
    }

    @Test
    fun stopPractice() {
        // Given
        val startDate = giveStartDate(15)
        sut = Practice(startDate = startDate, practiceDurationMills = (60000 * 30)) //30 minutes

        //When
        sut.stopPractice()

        //Then new time practice recalculated
        assert(sut.practiceDurationMills == ((60000 * 15)).toLong()) //15 minutes left
    }

    private fun giveStartDate(minBeforeNow: Int): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, -minBeforeNow)
        return cal.time
    }
}