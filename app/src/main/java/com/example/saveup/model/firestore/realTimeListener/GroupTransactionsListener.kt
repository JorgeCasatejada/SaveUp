package com.example.saveup.model.firestore.realTimeListener

import android.util.Log
import com.example.saveup.model.Transaction
import com.example.saveup.model.firestore.FireTransaction
import com.example.saveup.viewModel.MainViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class GroupTransactionsListener(private val vm: MainViewModel) :
    EventListener<QuerySnapshot> {
    override fun onEvent(querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.d(
                "Repository",
                "Respuesta fallida de firebase al recuperar las transacciones del grupo"
            )
            return
        }
        if (querySnapshot != null) {
            Log.d(
                "Repository",
                "Respuesta exitosa de firebase al recuperar las transacciones del grupo"
            )
            val groupTransactions = querySnapshot
                .map { document ->
                    Log.d("Firestore", "Transacción: " + document.id + " => " + document.data)
                    Transaction(document.toObject(FireTransaction::class.java))
                }
            vm.updateGroupTransactionsList(groupTransactions)
        }
    }
}