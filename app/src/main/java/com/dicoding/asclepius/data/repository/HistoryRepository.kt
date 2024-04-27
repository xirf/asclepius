package com.dicoding.asclepius.data.repository

import android.app.Application
import com.dicoding.asclepius.data.entity.HistoryEntity
import com.dicoding.asclepius.data.room.HistoryDao
import com.dicoding.asclepius.data.room.HistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {
    private val mHistoryDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getInstance(application)
        mHistoryDao = db.historyDao()
    }

    fun getAllHistory() = mHistoryDao.getAll()

    fun insertHistory(history:HistoryEntity) {
        executorService.execute { mHistoryDao.insert(history) }
    }

}