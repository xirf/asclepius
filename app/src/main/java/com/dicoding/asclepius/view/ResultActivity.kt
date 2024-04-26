package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import kotlin.math.roundToInt

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()


        with(binding) {
            val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f) * 100
            confidenceText.text = "${confidenceScore.roundToInt()}%"
            resultText.text = intent.getStringExtra(EXTRA_RESULT)
            resultImage.setImageURI(Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI)))
        }

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
    }


}