package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null
    private var analyzeResult: List<Classifications>? = null
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            analyzeButton.isClickable = false
            galleryButton.setOnClickListener { startGallery() }
            if (currentImageUri != null) {
                showImage()
                analyzeButton.isClickable = true
                analyzeButton.setOnClickListener { analyzeImage() }
            }
        }
    }

    private fun startGallery() {
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri == null) {
                showToast(getString(R.string.empty_image_warning))
            } else {
                currentImageUri = uri
                showImage()
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            val imageClassifierHelper =
                ImageClassifierHelper(this, object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?) {
                        analyzeResult = results
                        moveToResult()
                    }
                })
            imageClassifierHelper.classifyStaticImage(it)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_PREDICTION, analyzeResult?.toTypedArray())
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}