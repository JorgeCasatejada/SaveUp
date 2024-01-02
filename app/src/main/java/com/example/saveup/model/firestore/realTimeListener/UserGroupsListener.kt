package com.example.saveup.model.firestore.realTimeListener

import android.util.Log
import com.example.saveup.model.Group
import com.example.saveup.model.firestore.FireUserGroup
import com.example.saveup.viewModel.MainViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class UserGroupsListener(private val vm: MainViewModel) : EventListener<QuerySnapshot> {
    override fun onEvent(querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.d(
                "Repository",
                error.message ?: "Error al obtener el listener"
            )
            return
        }
        if (querySnapshot != null) {
            val groups = querySnapshot
                .map { document ->
                    Log.d("Firestore", "Group: " + document.id + " => " + document.data)
                    Group(document.toObject(FireUserGroup::class.java))
                }.toMutableList()
            vm.updateUserGroupList(groups)
        }
    }
}