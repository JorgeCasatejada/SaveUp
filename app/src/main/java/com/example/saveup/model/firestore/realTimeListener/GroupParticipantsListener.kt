package com.example.saveup.model.firestore.realTimeListener

import android.util.Log
import com.example.saveup.model.Group
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.viewModel.MainViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class GroupParticipantsListener(private val vm: MainViewModel, private val group: Group) :
    EventListener<QuerySnapshot> {
    override fun onEvent(querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.d(
                "Repository",
                error.message ?: "Error al obtener el listener"
            )
            return
        }
        if (querySnapshot != null) {
            val groupParticipants = querySnapshot
                .map { document ->
                    Log.d("Firestore", "Participante: " + document.id + " => " + document.data)
                    document.toObject(FireParticipant::class.java)
                }
            vm.updateGroupParticipantsList(groupParticipants, group)
        }
    }
}