package com.example.saveup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.repositorios.TransactionsRepository

class MainViewModelProviderFactory(
    val repository: TransactionsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}