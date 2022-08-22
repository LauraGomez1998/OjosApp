package net.tesa.ojosapp.util

import net.tesa.ojosapp.R

object FileUtils {

    fun getPdfNameFromAssets(): String {
        return "pdfafiche.pdf"
    }

    fun getVideoVerdePath(packageName: String): String {
        return "android.resource://" + packageName + "/" + R.raw.video_verde
    }

    fun getVideoRojoPath(packageName: String): String {
        return "android.resource://" + packageName + "/" + R.raw.video_rojo
    }
}