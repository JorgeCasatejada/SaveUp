package com.example.saveup.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveup.model.repository.TransactionsRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: TransactionsRepository
) : ViewModel() {

    val completedUserCreation: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        Log.d("SignUpViewModel", "Se inicializa el viewModel")
    }

    fun saveUserInFirestore(currentUser: FirebaseUser?) {
        if (currentUser == null) return
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("SignUpViewModel", "Se intenta guardar el usuario en Firestore")
            val id = currentUser.uid
            val user = hashMapOf(
                "id" to id,
                "email" to currentUser.email
            )
            val completed = repository.createUser(id, user)
            Log.d("SignUpViewModel", "Nuevo valor para el usuarioCreado: $completed")
            completedUserCreation.postValue(completed)
        }
    }

}