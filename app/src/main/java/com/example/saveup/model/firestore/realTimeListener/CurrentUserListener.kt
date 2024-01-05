package com.example.saveup.model.firestore.realTimeListener

import android.util.Log
import com.example.saveup.model.firestore.FireUser
import com.example.saveup.viewModel.MainViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class CurrentUserListener(private val vm: MainViewModel) :
    EventListener<DocumentSnapshot> {
    override fun onEvent(documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.d("Repository", "Respuesta fallida de firebase al obtener el usuario")
            return
        }
        if (documentSnapshot != null) {
            Log.d("Repository", "Respuesta exitosa de firebase al obtener el usuario")
            val newUserData = documentSnapshot
                .toObject(FireUser::class.java)
            if (newUserData != null) {
                vm.updateCurrentUserInfo(newUserData)
            }
        }
    }
}