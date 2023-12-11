package com.example.saveup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveup.model.Category
import com.example.saveup.model.Transaction
import com.example.saveup.model.TransactionManager
import com.example.saveup.repositorios.TransactionsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(
    private val repository: TransactionsRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val transactionManager: TransactionManager = TransactionManager()

    val balance: MutableLiveData<Double> = MutableLiveData(0.0)
    val allUserTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val showedMainTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val appliedTransactionFilter: MutableLiveData<Int> = MutableLiveData(0)

    init {
        Log.d("MainViewModel", "Se inicializa el viewModel")
    }

    fun getUserTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener las transacciones del usuario")
            val resp = repository.getUserTransactions(auth.currentUser!!.uid)
            Log.d("MainViewModel", "Nuevo valor para allUserTransactions: $resp")
            allUserTransactions.postValue(resp)
            Log.d("MainViewModel", "Nuevo valor para showedMainTransactions: $resp")
            showedMainTransactions.postValue(resp)
            transactionManager.transactionsList = resp
            Log.d("MainViewModel", "Nuevo valor para balance: ${transactionManager.balance}")
            balance.postValue(transactionManager.balance)
        }
    }

    fun setFilter(filter: Int) {
        appliedTransactionFilter.value = filter
    }

    fun filterTransactions(filter: Int) {
        Log.d("MainViewModel", "Se intentan filtrar las transacciones del usuario. Filtro: $filter")
        val filteredList = transactionManager.getFilteredTransactionsList(filter)
        Log.d("MainViewModel", "Nuevo valor para showedMainTransactions: $filteredList")
        showedMainTransactions.postValue(filteredList)
    }

    fun groupedTransactionsByYear(year: Int): MutableMap<Int, MutableList<Transaction>>? {
        return transactionManager.getGroupedTransactions(year)
    }

    fun groupedCategories(year: Int, showExpenses: Boolean): MutableMap<Category, Double>? {
        return transactionManager.getCategories(year, showExpenses)
    }

    fun getStrBalance(): String {
        return String.format(Locale.getDefault(), "%.2f", balance.value)
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan añadir una transacción al usuario")
            val transactionId = repository.addTransactionToCurrentUser(transaction)
            Log.d("MainViewModel", "Nuevo id para la transacción: $transactionId")
            transactionManager.addTransaction(transaction)
            allUserTransactions.postValue(transactionManager.transactionsList)
            Log.d("MainViewModel", "Nuevo valor para balance: ${transactionManager.balance}")
            balance.postValue(transactionManager.balance)
            filterTransactions(appliedTransactionFilter.value!!)
        }
    }

    fun removeTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan eliminar una transacción del usuario")
            repository.deleteTransactionFromCurrentUser(transaction.transactionID)
            transactionManager.removeTransaction(transaction)
            allUserTransactions.postValue(transactionManager.transactionsList)
            Log.d("MainViewModel", "Nuevo valor para balance: ${transactionManager.balance}")
            balance.postValue(transactionManager.balance)
            filterTransactions(appliedTransactionFilter.value!!)
        }
    }

    fun modifyTransaction(transactionOld: Transaction, transactionNew: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan modificar una transacción del usuario")
            transactionNew.transactionID = transactionOld.transactionID
            repository.modifyTransactionFromCurrentUser(transactionNew)
            transactionManager.removeTransaction(transactionOld)
            transactionManager.addTransaction(transactionNew)
            allUserTransactions.postValue(transactionManager.transactionsList)
            Log.d("MainViewModel", "Nuevo valor para balance: ${transactionManager.balance}")
            balance.postValue(transactionManager.balance)
            filterTransactions(appliedTransactionFilter.value!!)
        }
    }

}