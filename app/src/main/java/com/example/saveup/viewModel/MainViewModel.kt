package com.example.saveup.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveup.model.Category
import com.example.saveup.model.Group
import com.example.saveup.model.Transaction
import com.example.saveup.model.TransactionManager
import com.example.saveup.model.firestore.FireGoal
import com.example.saveup.model.repository.TransactionsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
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
    val userGroups: MutableLiveData<List<Group>> = MutableLiveData()
    val monthlyLimit: MutableLiveData<Double?> = MutableLiveData()
    val goal: MutableLiveData<FireGoal?> = MutableLiveData()

    init {
        Log.d("MainViewModel", "Se inicializa el viewModel")
    }

    // ------------------ MainScreenFragment ------------------
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

    // ------------------ ProfileFragment ------------------
    fun getUserName(): String {
        return auth.currentUser?.displayName ?: ""
    }

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun logOutFromCurrentUser() {
        Log.d("MainViewModel", "Se intentan cerrar la sesión del usuario")
        auth.signOut()
    }


    // ------------------ GraphsFragment ------------------
    fun groupedTransactionsByYear(year: Int): MutableMap<Int, MutableList<Transaction>>? {
        return transactionManager.getGroupedTransactions(year)
    }

    fun groupedCategories(year: Int, showExpenses: Boolean): MutableMap<Category, Double>? {
        return transactionManager.getCategories(year, showExpenses)
    }

    // ------------------ LimitsFragment ------------------
    fun getLimit() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta obtener el límite del usuario")
            val limit = repository.getMonthlyLimit()
            Log.d("MainViewModel", "Nuevo valor para límite mensual: $limit")
            if (limit == null || limit >= 1_000_000_000) {
                monthlyLimit.postValue(null)
            } else {
                monthlyLimit.postValue(limit)
            }
        }
    }

    fun updateLimit(limit: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta modificar el límite del usuario")
            repository.updateMonthlyLimit(limit)
            Log.d("MainViewModel", "Nuevo valor para límite mensual: $limit")
            if (limit >= 1_000_000_000) {
                monthlyLimit.postValue(null)
            } else {
                monthlyLimit.postValue(limit)
            }
        }
    }

    fun deleteLimit() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta eliminar el límite del usuario")
            repository.deleteMonthlyLimit()
            monthlyLimit.postValue(null)
        }
    }

    fun getMonthlyExpenses(): Double? {
        val transactions = transactionManager.getGroupedTransactions(Date().year + 1900)
        if (!transactions.isNullOrEmpty()) {
            val transactionsInMonth =  transactions[Date().month]
            if (!transactionsInMonth.isNullOrEmpty()) {
                Log.d("debug", transactionsInMonth.toString())
                val expenses = transactionsInMonth
                    .filter { it.isExpense }
                return if (expenses.isEmpty()) {
                    null
                } else {
                    expenses
                        .map { transaction -> transaction.value }
                        .reduce { total, expense -> total + expense }
                }
            }
        }
        return null
    }

    fun getCurrentGoal() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta obtener la meta del usuario")
            val newGoal = repository.getGoal(auth.currentUser!!.uid)
            Log.d("MainViewModel", "Nuevo valor para la meta: $newGoal")
            goal.postValue(newGoal)
        }
    }

    fun updateGoal(newGoal: FireGoal) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta modificar la meta del usuario")
            repository.updateGoal(newGoal)
            Log.d("MainViewModel", "Nuevo valor para la meta: $newGoal")
            goal.postValue(newGoal)
        }
    }

    fun deleteGoal() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta eliminar la meta del usuario")
            repository.deleteGoal()
            goal.postValue(null)
        }
    }

    // ------------------ GroupsFragment ------------------
    fun getUserGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener los grupos del usuario")
            val resp = repository.getUserGroups(auth.currentUser!!.uid)
            Log.d("MainViewModel", "Nuevo valor para userGroups: $resp")
            userGroups.postValue(resp)
        }
    }

}