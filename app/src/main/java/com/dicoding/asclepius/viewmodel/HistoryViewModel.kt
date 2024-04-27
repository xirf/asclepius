package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.entity.HistoryEntity
import com.dicoding.asclepius.data.repository.HistoryRepository

class HistoryViewModel(application: Application) : ViewModel() {
    private val mNewsRepository = HistoryRepository(application)
    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        val history = mNewsRepository.getAllHistory()
        return history
    }

    fun insertHistory(history: HistoryEntity) = mNewsRepository.insertHistory(history)

}