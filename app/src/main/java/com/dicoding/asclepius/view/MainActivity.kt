package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.dicoding.asclepius.viewmodel.ViewModelFactory
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var historyViewModel: HistoryViewModel

    private var currentImageUri: Uri? = null
    private var toast: Toast? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                showToast("Permission Granted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()


        if (!checkPermissions()) requestPermissionLauncher.launch(REQUIRE_PERMISSION);
        if (currentImageUri == null) binding.analyzeButton.visibility = View.GONE;


        with(binding) {
            progressIndicator.visibility = View.GONE

            analyzeButton.setOnClickListener { analyzeImage() }
            galleryButton.setOnClickListener {
                launcherGallery.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(application)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
    }

    private fun checkPermissions() = ContextCompat.checkSelfPermission(
        this,
        REQUIRE_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED


    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
                binding.analyzeButton.visibility = View.VISIBLE
            } else {
                binding.analyzeButton.visibility = View.GONE
                showToast(getString(R.string.empty_image_warning))
            }
        }


    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            binding.progressIndicator.visibility = View.VISIBLE
            val imageClassifierHelper =
                ImageClassifierHelper(this, object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                        binding.progressIndicator.visibility = View.GONE
                    }

                    override fun onResults(results: List<Classifications>?) {
                        insertIntoHistory(results!!)
                        moveToResult(results)
                        binding.progressIndicator.visibility = View.GONE
                    }
                })
            imageClassifierHelper.classifyStaticImage(it)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun insertIntoHistory(results: List<Classifications>) {
        historyViewModel.insertHistory(
            HistoryEntity(
                label = results[0].categories?.get(0)?.label,
                confidence = results[0].categories?.get(0)?.score ?: 0f,
                image = currentImageUri.toString()
            )
        )
    }

    private fun moveToResult(analyzeResult: List<Classifications>) {
        val topClassifications = analyzeResult[0].categories
        if (topClassifications != null) {
            val intent = Intent(this, ResultActivity::class.java)
            Log.d("RESULT", topClassifications[0].toString())
            intent.putExtra(ResultActivity.EXTRA_RESULT, topClassifications[0].label)
            intent.putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, topClassifications[0].score)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            startActivity(intent)
        } else {
            showToast(getString(R.string.classification_failed))
        }
    }

    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    companion object {
        private const val REQUIRE_PERMISSION = Manifest.permission.CAMERA
    }
}