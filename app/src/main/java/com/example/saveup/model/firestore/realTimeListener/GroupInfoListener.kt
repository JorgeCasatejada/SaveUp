package com.example.saveup.model.firestore.realTimeListener

import android.util.Log
import com.example.saveup.model.firestore.FireGroup
import com.example.saveup.viewModel.MainViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class GroupInfoListener(private val vm: MainViewModel, private val searchedGroupID: String) :
    EventListener<DocumentSnapshot> {
    override fun onEvent(documentSnapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.d("Repository", "Respuesta fallida de firebase al obtener el grupo")
            return
        }
        if (documentSnapshot != null) {
            Log.d("Repository", "Respuesta exitosa de firebase al obtener el grupo")
            val newGroupData = documentSnapshot
                .toObject(FireGroup::class.java)
            vm.updateGroupInfo(newGroupData, searchedGroupID)
        }
    }
}