package net.tesa.ojosapp.datasource

import net.tesa.ojosapp.model.Practice

interface PracticeDatasourceInterface {
    fun startPractice(durationInMills: Long)
    fun stopPractice()
    fun resumePractice()
    fun cancelPractice()
    fun getLastPractice(): Practice?
}