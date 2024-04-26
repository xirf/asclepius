package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(): ViewModelFactory {
            if (instance == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (instance == null) {
                        instance = ViewModelFactory()
                    }
                }
            }
            return instance as ViewModelFactory
        }

    }
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")

        }
    }
}