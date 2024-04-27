package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.entity.HistoryEntity
import com.dicoding.asclepius.data.repository.HistoryRepository

class HistoryViewModel(application: Application) : ViewModel() {
    private val mNewsRepository = HistoryRepository(application)

    fun getAllHistory() = mNewsRepository.getAllHistory()
    fun getHistoryById(id: Int) = mNewsRepository.getHistoryById(id)
    fun deleteHistoryById(id: Int) = mNewsRepository.deleteHistoryById(id)
    fun insertHistory(history: HistoryEntity) = mNewsRepository.insertHistory(history)

}