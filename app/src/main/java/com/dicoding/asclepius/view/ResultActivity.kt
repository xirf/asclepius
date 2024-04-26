package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.ViewModelFactory
import java.util.Locale
import kotlin.math.roundToInt

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: MainViewModel
    private val adapter: NewsAdapter by lazy { NewsAdapter() }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val factory = ViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContentView(binding.root)
        enableEdgeToEdge()
        getNews()

        setupUI()

        viewModel.isLoading.observe(this) {
            loadingState(it)
        }
    }

    private fun setupUI() {
        setupResultUI()
        setupFindHospitalButton()
    }

    private fun setupResultUI() {
        with(binding) {
            val predictionResult = intent.getStringExtra(EXTRA_RESULT)
            val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f) * 100

            confidenceText.text = "${confidenceScore.roundToInt()}%"
            resultText.text = predictionResult
            resultImage.setImageURI(Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI)))

            if (predictionResult != null) {
                if (predictionResult.lowercase(Locale.ROOT) == "cancer") {
                    resultText.setTextColor(getColor(R.color.tertiary_600))
                    descriptionText.text = getString(R.string.result_cancer)
                } else {
                    resultText.setTextColor(getColor(R.color.primary_400))
                    descriptionText.text = getString(R.string.result_no_cancer)
                }
            }
        }
    }

    private fun setupFindHospitalButton() {
        binding.findHospitalButton.setOnClickListener { goToGoogleMaps() }
    }

    private fun getNews() {
        viewModel.getNews()
        viewModel.news.observe(this) {
            if (it != null) {
                binding.newsList.visibility = View.VISIBLE
                binding.noNewsText.visibility = View.GONE
                adapter.setListNews(it)
                renderRecyclerView()
            }else{
                binding.newsList.visibility = View.GONE
                binding.noNewsText.visibility = View.VISIBLE
            }
        }
    }

    private fun renderRecyclerView() {
        with(binding) {
            newsList.layoutManager = LinearLayoutManager(this@ResultActivity)
            newsList.adapter = adapter
            newsList.setHasFixedSize(true)
            adapter.setOnClickCallback {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                startActivity(intent)
            }
        }
    }

    private fun goToGoogleMaps() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/Hospitals"))
        startActivity(intent)
    }

    private fun loadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}