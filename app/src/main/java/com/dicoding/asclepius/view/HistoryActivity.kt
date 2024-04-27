package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.helper.MediaStorageHelper
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.dicoding.asclepius.viewmodel.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var mediaStorageHelper: MediaStorageHelper

    private val historyAdapter: HistoryAdapter by lazy { HistoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        val factory = ViewModelFactory.getInstance(application)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
        mediaStorageHelper = MediaStorageHelper(this)

        enableEdgeToEdge()
        setContentView(binding.root)
        listenToHistory()
        setupRecyclerView()
    }

    private fun listenToHistory() {
        historyViewModel.getAllHistory().observe(this) {
            if (it.isEmpty()) {
                binding.noHistory.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                historyAdapter.setListHistory(it)
                binding.noHistory.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }

        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            rvHistory.layoutManager = GridLayoutManager(this@HistoryActivity, 2)
            rvHistory.adapter = historyAdapter
            historyAdapter.setOnClickCallback {
                moveToResultActivity(it.label, it.confidence, it.image)
            }
        }
    }

    private fun moveToResultActivity(label: String, confidence: Float, imageUri: String) {
        val intent = Intent(this, ResultActivity::class.java)
        val image = mediaStorageHelper.getImageFromGallery(imageUri)

        intent.putExtra(ResultActivity.EXTRA_RESULT, label)
        intent.putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, confidence)
        if (image != null)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, image.toString())
        startActivity(intent)
    }
}