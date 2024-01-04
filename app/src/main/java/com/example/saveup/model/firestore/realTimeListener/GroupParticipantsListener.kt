package com.example.saveup.model.firestore.realTimeListener

import android.util.Log
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.viewModel.MainViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class GroupParticipantsListener(private val vm: MainViewModel) :
    EventListener<QuerySnapshot> {
    override fun onEvent(querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.d(
                "Repository",
                "Respuesta fallida de firebase al recuperar los participantes del grupo"
            )
            return
        }
        if (querySnapshot != null) {
            Log.d(
                "Repository",
                "Respuesta exitosa de firebase al recuperar los participantes del grupo"
            )
            val groupParticipants = querySnapshot
                .map { document ->
                    Log.d("Firestore", "Participante: " + document.id + " => " + document.data)
                    document.toObject(FireParticipant::class.java)
                }
            vm.updateGroupParticipantsList(groupParticipants)
        }
    }
}