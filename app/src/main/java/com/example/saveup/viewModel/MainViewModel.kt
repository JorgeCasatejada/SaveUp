package com.example.saveup.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveup.model.Category
import com.example.saveup.model.Group
import com.example.saveup.model.GroupManager
import com.example.saveup.model.Transaction
import com.example.saveup.model.TransactionManager
import com.example.saveup.model.firestore.FireParticipant
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
    private val groupManager: GroupManager = GroupManager()

    val allUserTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()

    // ------------------ MainScreenFragment ------------------
    val balance: MutableLiveData<Double> = MutableLiveData(0.0)
    val showedMainTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val appliedTransactionFilter: MutableLiveData<Int> = MutableLiveData(0)

    // ------------------ LimitsFragment ------------------
    val monthlyLimit: MutableLiveData<Double?> = MutableLiveData()

    // ------------------ GroupsFragment ------------------
    val userGroups: MutableLiveData<List<Group>> = MutableLiveData()
    val currentGroup: MutableLiveData<Group?> = MutableLiveData()
    val currentGroupTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val currentGroupParticipants: MutableLiveData<List<FireParticipant>> = MutableLiveData()

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
            monthlyLimit.postValue(limit)
        }
    }

    fun updateLimit(limit: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta modificar el límite del usuario")
            repository.updateMonthlyLimit(limit)
            Log.d("MainViewModel", "Nuevo valor para límite mensual: $limit")
            monthlyLimit.postValue(limit)
        }
    }

    fun getMonthlyExpenses(): Double? {
        val transactions = transactionManager.getGroupedTransactions(Date().year + 1900)
        if (!transactions.isNullOrEmpty()) {
            val transactionsInMonth = transactions[Date().month]
            if (!transactionsInMonth.isNullOrEmpty()) {
                return transactionsInMonth
                    .filter { it.isExpense }
                    .map { transaction -> transaction.value }
                    .reduce { total, expense -> total + expense }
            }
        }
        return null
    }

    // ------------------ GroupsFragment ------------------
    fun getCurrentGroup(): Group? {
        return currentGroup.value
    }


    fun getUserGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener los grupos del usuario")
            val resp = repository.getUserGroups(auth.currentUser!!.uid)
            Log.d("MainViewModel", "Nuevo valor para userGroups: $resp")
            groupManager.groupList = resp
            userGroups.postValue(resp)
        }
        // TODO: usar y probar esta función
    }

    fun loadInfoFromGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener la información del grupo")
            val newGroupData = repository.getGroup(group)
            if (newGroupData == null) {
                Log.d("MainViewModel", "El grupo buscado no existe")
            } else {
                Log.d("MainViewModel", "Nueva información para el grupo: $newGroupData")
                groupManager.addDataToGroup(newGroupData)
                currentGroup.postValue(groupManager.getGroup(newGroupData.id))
            }
        }
        // TODO: usar y probar esta función
    }

    fun createGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan crear un grupo por el usuario")
            val groupId = repository.createGroup(group)
            Log.d("MainViewModel", "Nuevo id para el grupo: $groupId")
            groupManager.addGroup(group)
            userGroups.postValue(groupManager.groupList)
        }
        // TODO: usar y probar esta función
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan eliminar un grupo por el usuario")
            val groupId = repository.deleteGroup(group)
            groupManager.removeGroup(group)
            userGroups.postValue(groupManager.groupList)
        }
        // TODO: usar y probar esta función
    }

    fun loadParticipantsFromGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener los participantes del grupo")
            val groupParticipants = repository.getGroupParticipants(group)
            Log.d("MainViewModel", "Nuevos participantes para el grupo: $groupParticipants")
            groupManager.addParticipantsToGroup(groupParticipants, group)
            currentGroupParticipants.postValue(groupParticipants)
        }
        // TODO: usar y probar esta función
    }

    fun addParticipantToGroup(group: Group, participant: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan añadir un participante al grupo")
            val groupParticipant = repository.getParticipant(participant)
            Log.d("MainViewModel", "Nuevo participante para el grupo: $groupParticipant")
            if (groupParticipant.email.isNotEmpty()) {
                repository.addParticipantToGroup(group, groupParticipant)
                groupManager.addParticipantToGroup(groupParticipant, group)
                currentGroupParticipants.postValue(group.participants)
            }
        }
        // TODO: usar y probar esta función
    }

    fun addAdminToGroup(group: Group, participant: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan añadir un participante al grupo")
            val groupParticipant = repository.getParticipant(participant)
            groupParticipant.isAdmin = true
            Log.d("MainViewModel", "Nuevo participante para el grupo: $groupParticipant")
            repository.addParticipantToGroup(group, groupParticipant)
            groupManager.addParticipantToGroup(groupParticipant, group)
            currentGroupParticipants.postValue(group.participants)
        }
        // TODO: usar y probar esta función
    }

     //Quité suspend porque sin no puedo usarlo
    fun deleteParticipantFromGroup(group: Group, participant: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener el FireParticipant")
            val groupParticipant = repository.getParticipant(participant)
            Log.d("MainViewModel", "Se intentan eliminar un participante del grupo")
            repository.deleteParticipantFromGroup(group, groupParticipant.id)
            groupManager.removeParticipantFromGroup(groupParticipant, group)
            currentGroupParticipants.postValue(group.participants)
        }
        // TODO: usar y probar esta función
    }

    fun loadTransactionsFromGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan obtener las transacciones del grupo")
            val groupTransactions = repository.getGroupTransactions(group)
            Log.d("MainViewModel", "Nuevas transacciones para el grupo: $groupTransactions")
            groupManager.addTransactionsToGroup(groupTransactions, group)
            currentGroupTransactions.postValue(groupTransactions)
        }
        // TODO: usar y probar esta función
    }

    fun addTransactionToGroup(transaction: Transaction, group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan añadir una transacción al grupo")
            val transactionId = repository.addTransactionToGroup(transaction, group)
            Log.d("MainViewModel", "Nuevo id para la transacción: $transactionId")
            groupManager.addTransactionToGroup(transaction, group)
            currentGroupTransactions.postValue(group.transactionList)
        }
        // TODO usar y probar esta función
    }

    fun removeTransactionFromGroup(transaction: Transaction, group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan eliminar una transacción del grupo")
            repository.deleteTransactionFromGroup(transaction.transactionID, group)
            groupManager.removeTransactionFromGroup(transaction, group)
            currentGroupTransactions.postValue(group.transactionList)
        }
        // TODO usar y probar esta función
    }

    fun modifyTransactionFromGroup(
        transactionOld: Transaction,
        transactionNew: Transaction,
        group: Group
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan modificar una transacción del grupo")
            transactionNew.transactionID = transactionOld.transactionID
            repository.modifyTransactionFromGroup(transactionNew, group)
            groupManager.modifyTransactionFromGroup(transactionOld, transactionNew, group)
            currentGroupTransactions.postValue(group.transactionList)
        }
        // TODO usar y probar esta función
    }


}