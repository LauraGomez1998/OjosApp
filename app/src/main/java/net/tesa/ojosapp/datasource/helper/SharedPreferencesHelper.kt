package net.tesa.ojosapp.datasource.helper

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {

    val PRACTICE_MODEL = "PRACTICE"

    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.practice
        get() = getString(PRACTICE_MODEL, null)
        set(value) {
            editMe {
                it.putString(PRACTICE_MODEL, value)
            }
        }

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
            }
        }
}