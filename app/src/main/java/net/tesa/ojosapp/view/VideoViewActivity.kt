package net.tesa.ojosapp.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import net.tesa.ojosapp.databinding.ActivityVideoViewBinding
import net.tesa.ojosapp.util.FileUtils


class VideoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        when (intent.getStringExtra("option")) {
            "option_1" -> {
                openVideo(FileUtils.getVideoRojoPath(packageName))
            }
            "option_2" -> {
                openVideo(FileUtils.getVideoVerdePath(packageName))
            }
        }
    }

    private fun openVideo(path: String) {
        binding.simpleVideoView.setVideoURI(Uri.parse(path))
        binding.simpleVideoView.start()
    }
}