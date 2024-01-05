package com.example.saveup.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveup.model.Category
import com.example.saveup.model.Group
import com.example.saveup.model.GroupManager
import com.example.saveup.model.Transaction
import com.example.saveup.model.TransactionManager
import com.example.saveup.model.firestore.FireGroup
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.model.firestore.FireUser
import com.example.saveup.model.firestore.realTimeListener.CurrentUserListener
import com.example.saveup.model.firestore.realTimeListener.GroupInfoListener
import com.example.saveup.model.firestore.realTimeListener.GroupParticipantsListener
import com.example.saveup.model.firestore.realTimeListener.GroupTransactionsListener
import com.example.saveup.model.firestore.realTimeListener.UserGroupsListener
import com.example.saveup.model.repository.TransactionsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
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

    // ------------------ ProfileFragment ------------------
    val currentUser: MutableLiveData<FireUser> = MutableLiveData()
    private var currentUserListenerRegistration: ListenerRegistration? = null

    // ------------------ GroupsFragment ------------------
    val userGroups: MutableLiveData<List<Group>> = MutableLiveData()
    val currentGroup: MutableLiveData<Group?> = MutableLiveData()
    val isGroupAdmin = MutableLiveData<Boolean>()
    val currentGroupTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val currentGroupParticipants: MutableLiveData<List<FireParticipant>> = MutableLiveData()
    val participantAddedResult = MutableLiveData<Pair<Boolean, String>>()
    val participantsNotAddedResult = MutableLiveData<List<String>>()

    var searchedGroupID: String = ""
    private var userGroupsListenerRegistration: ListenerRegistration? = null
    private var groupsInfoListenerRegistration: ListenerRegistration? = null
    private var groupParticipantsListenerRegistration: ListenerRegistration? = null
    private var groupTransactionsListenerRegistration: ListenerRegistration? = null

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

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun updateCurrentUserInfo(newUserData: FireUser) {
        Log.d("MainViewModel", "Nuevo valor para el usuario: $newUserData")
        currentUser.postValue(newUserData)
    }

    fun registerCurrentUserListener() {
        Log.d("MainViewModel", "Se empieza a observar la información del usuario")
        currentUserListenerRegistration =
            repository.getCurrentUserRegistration(auth.currentUser!!.uid, CurrentUserListener(this))
    }

    fun unregisterCurrentUserListener() {
        Log.d("MainViewModel", "Se deja de observar la información del usuario")
        currentUserListenerRegistration?.remove()
    }

    fun logOutFromCurrentUser() {
        Log.d("MainViewModel", "Se intentan cerrar la sesión del usuario")
        auth.signOut()
    }

    fun saveData(userName: String, imageUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            val newData = mutableMapOf<String, Any>()
            if (imageUri != null) {
                Log.d("MainViewModel", "Se intenta actualizar la imagen del usuario")
                val imagePath = repository.uploadUserImage(auth.currentUser!!.uid, imageUri)
                if (imagePath.isNotBlank()) {
                    newData["imagePath"] = imagePath
                }
            }
            if (userName != currentUser.value?.userName) {
                Log.d("MainViewModel", "Se intenta actualizar el userName del usuario")
                repository.updateAuthUser(userName)
                newData["userName"] = userName
            }
            if (newData.isNotEmpty()) {
                Log.d("MainViewModel", "Se intenta modificar el usuario: $newData")
                repository.modifyUserFireStore(newData)
            } else {
                Log.d("MainViewModel", "No hay datos nuevos que actualizar")
            }

        }
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
    fun updateUserGroupList(groupList: MutableList<Group>) {
        Log.d("MainViewModel", "Nuevo valor para userGroups: $groupList")
        userGroups.postValue(groupList)
    }

    fun registerUserGroupsListener() {
        Log.d("MainViewModel", "Se empiezan a observar los grupos del usuario")
        userGroupsListenerRegistration =
            repository.getUserGroupsRegistration(auth.currentUser!!.uid, UserGroupsListener(this))
    }

    fun unregisterUserGroupsListener() {
        Log.d("MainViewModel", "Se dejan de observar los grupos del usuario")
        userGroupsListenerRegistration?.remove()
    }

    fun getCurrentGroup(): Group? {
        return currentGroup.value
    }

    // ------------------ GroupDetailsFragment ------------------

    fun setDefaultGroupValues() {
        currentGroup.postValue(Group())
        currentGroupParticipants.postValue(listOf(FireParticipant(email = getUserEmail())))
        currentGroupTransactions.postValue(emptyList())
    }

    fun updateGroupInfo(newGroupData: FireGroup?, searchedGroupID: String) {
        this.searchedGroupID = searchedGroupID
        if (newGroupData == null) {
            Log.d("MainViewModel", "El grupo buscado no existe")
            currentGroup.postValue(null)
        } else {
            Log.d("MainViewModel", "Nueva información para el grupo: $newGroupData")
            currentGroup.postValue(Group(newGroupData))
        }
    }

    fun registerGroupInfoListener(group: Group) {
        Log.d("MainViewModel", "Se empiezan a observar la información del grupo")
        groupsInfoListenerRegistration =
            repository.getGroupInfoRegistration(group, GroupInfoListener(this, group.id))
    }

    fun unregisterGroupInfoListener() {
        Log.d("MainViewModel", "Se dejan de observar la información del grupo")
        groupsInfoListenerRegistration?.remove()
    }

    fun createGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan crear un grupo por el usuario")
            val groupId = repository.createGroup(group)
            Log.d("MainViewModel", "Nuevo id para el grupo: $groupId")
        }
        // TODO: usar y probar esta función
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan eliminar un grupo por el usuario")
            repository.deleteGroup(group.id)
        }
        // TODO: usar y probar esta función
    }

    // ------------------ GroupDetailsParticipantsFragment ------------------

    fun updateGroupParticipantsList(groupParticipants: List<FireParticipant>) {
        Log.d("MainViewModel", "Nuevos participantes para el grupo: $groupParticipants")
        currentGroupParticipants.postValue(groupParticipants)
    }

    fun registerGroupParticipantsListener(group: Group) {
        Log.d("MainViewModel", "Se empiezan a observar los participantes del grupo")
        groupParticipantsListenerRegistration = repository.getGroupParticipantsRegistration(
            group,
            GroupParticipantsListener(this)
        )
    }

    fun unregisterGroupParticipantsListener() {
        Log.d("MainViewModel", "Se dejan de observar los participantes del grupo")
        groupParticipantsListenerRegistration?.remove()
    }

    fun checkAdmin(participants: List<FireParticipant>) {
        isGroupAdmin.postValue(groupManager.isAdmin(participants, getUserEmail()))
    }

    fun checkUserStillInGroup(participants: List<FireParticipant>): Boolean {
        return groupManager.isParticipantInGroup(participants, getUserEmail())
    }

    fun addParticipantToGroup(group: Group, participant: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta añadir un participante al grupo")
            val groupParticipant = repository.getParticipant(participant)
            Log.d("MainViewModel", "Nuevo participante para el grupo: $groupParticipant")
            if (groupParticipant.email.isNotEmpty() && groupParticipant.email != getUserEmail()) {
                repository.addParticipantToGroup(group, groupParticipant)
                participantAddedResult.postValue(Pair(true, participant))
            } else {
                participantAddedResult.postValue(Pair(false, participant))
            }
        }
        // TODO: usar y probar esta función
    }

    fun addParticipantsToGroup(group: Group, participants: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan añadir participantes al grupo")
            val participantsNotAdded: MutableList<String> = mutableListOf()
            for (p in participants) {
                val groupParticipant = repository.getParticipant(p)
                Log.d("MainViewModel", "Nuevos participantes para el grupo: $group")
                if (groupParticipant.email.isNotEmpty() && groupParticipant.email != getUserEmail()) {
                    repository.addParticipantToGroup(group, groupParticipant)
                } else {
                    participantsNotAdded.add(p)
                }
            }
            participantsNotAddedResult.postValue(participantsNotAdded)
        }
        // TODO: usar y probar esta función
    }

    fun addAdminToGroup(group: Group, participant: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta añadir un participante al grupo")
            val groupParticipant = repository.getParticipant(participant)
            groupParticipant.isAdmin = true
            Log.d("MainViewModel", "Nuevo participante para el grupo: $groupParticipant")
            repository.addParticipantToGroup(group, groupParticipant)
        }
        // TODO: usar y probar esta función
    }

    fun deleteGroupFromMyGroups(groupID: String = searchedGroupID) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta eliminar grupo de la lista de grupos del usuario")
            repository.deleteGroupFromMyGroups(groupID, auth.currentUser!!.uid)
        }
        // TODO: usar y probar esta función
    }

    fun deleteParticipantFromGroup(group: Group, participantID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta eliminar un participante del grupo")
            repository.deleteParticipantFromGroup(group, participantID)
        }
        // TODO: usar y probar esta función
    }

    fun exitFromCurrentGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intenta salir del grupo actual")
            deleteParticipantFromGroup(getCurrentGroup()!!, auth.currentUser!!.uid)
            deleteGroupFromMyGroups(getCurrentGroup()!!.id)
        }
        // TODO: usar y probar esta función
    }

    // ------------------ GroupDetailsExpensesFragment ------------------

    fun updateGroupTransactionsList(groupTransactions: List<Transaction>) {
        Log.d("MainViewModel", "Nuevas transacciones para el grupo: $groupTransactions")
        currentGroupTransactions.postValue(groupTransactions)
    }

    fun registerGroupTransactionsListener(group: Group) {
        Log.d("MainViewModel", "Se empiezan a observar las transacciones del grupo")
        groupTransactionsListenerRegistration = repository.getGroupTransactionsRegistration(
            group,
            GroupTransactionsListener(this)
        )
    }

    fun unregisterGroupTransactionsListener() {
        Log.d("MainViewModel", "Se dejan de observar las transacciones del grupo")
        groupTransactionsListenerRegistration?.remove()
    }

    fun addTransactionToGroup(transaction: Transaction, group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan añadir una transacción al grupo")
            val transactionId = repository.addTransactionToGroup(transaction, group)
            Log.d("MainViewModel", "Nuevo id para la transacción: $transactionId")
        }
        // TODO usar y probar esta función
    }

    fun removeTransactionFromGroup(transaction: Transaction, group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "Se intentan eliminar una transacción del grupo")
            repository.deleteTransactionFromGroup(transaction, group)
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
            val valueDifference = transactionNew.signedValue - transactionOld.signedValue
            repository.modifyTransactionFromGroup(transactionNew, group, valueDifference)
        }
        // TODO usar y probar esta función
    }


}