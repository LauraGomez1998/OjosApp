package net.tesa.ojosapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.tesa.ojosapp.R
import net.tesa.ojosapp.databinding.ActivityPdfViewActivtiyBinding
import net.tesa.ojosapp.util.FileUtils

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewActivtiyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewActivtiyBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        checkPdfAction(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.modo_1 -> openVideoOption("option_1")
            R.id.modo_2 -> openVideoOption("option_2")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openVideoOption(option: String) {
        val intent = Intent(this, VideoViewActivity::class.java)
        intent.putExtra("option", option)
        startActivity(intent)
    }

    private fun checkPdfAction(intent: Intent) {
        when (intent.getStringExtra("ViewType")) {
            "assets" -> {
                showPdfFromAssets(FileUtils.getPdfNameFromAssets())
            }
            "storage" -> {
                // perform action to show pdf from storage
            }
            "internet" -> {
                // perform action to show pdf from the internet
            }
        }
    }

    private fun showPdfFromAssets(pdfName: String) {
        binding.pdfView.fromAsset(pdfName)
            .password(null) // if password protected, then write password
            .defaultPage(0) // set the default page to open
            .onPageError { page, _ ->
                Toast.makeText(
                    this@PdfViewActivity,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }
}