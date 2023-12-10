package com.example.saveup.repositorios

import android.util.Log
import com.example.saveup.model.Transaction
import com.example.saveup.model.firestore.FireTransaction
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getUserTransactions(userId: String): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val respuesta = db.collection("users")
                .document(userId)
                .collection("transactions")
                .get().await().map { document ->
                    Log.d("Firestore", "Transaction: " + document.id + " => " + document.data)
                    Transaction(document.toObject(FireTransaction::class.java))
                }
            return@withContext respuesta

//            if (respuesta.isSuccessful) {
//                Log.d("Repository", "Respuesta exitosa de firebase al recuperar las transacciones del usuario")
//                respuesta.result
//                    .map { document ->
//                        Log.d("Firestore", "Transaction: " + document.id + " => " + document.data)
//                        Transaction(document.toObject(FireTransaction::class.java))
//                 }
//            } else {
//                Log.d("Repository", "Respuesta fallida de firebase al recuperar las transacciones del usuario")
//                emptyList()
//            }
        }
    }

    suspend fun addTransactionToCurrentUser(transaction: Transaction): String {
        return withContext(Dispatchers.IO) {
            val docRef = db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("transactions").document()
            transaction.transactionID = docRef.id
            docRef.set(transaction.toFirestore()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Repository", "Respuesta exitosa de firebase al añadir la transacción")
                } else {
                    Log.d("Repository", "Respuesta fallida de firebase al añadir la transacción")
                }
            }
            return@withContext docRef.id
        }
    }

    suspend fun deleteTransactionFromCurrentUser(transactionId: String) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("transactions")
                .document(transactionId)
                .delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al eliminar la transacción"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al eliminar la transacción"
                        )
                    }
                }
        }
    }

    suspend fun modifyTransactionFromCurrentUser(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("transactions")
                .document(transaction.transactionID)
                .set(transaction.toFirestore()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al modificar la transacción"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al modificar la transacción"
                        )
                    }
                }
        }
    }

    suspend fun createUser(userId: String?, userData: HashMap<String, String?>): Int {
        Log.d("Repository", "Intentando crear el usuario")
        return withContext(Dispatchers.IO) {
            db.collection("users")
                .document(userId.orEmpty())
                .set(userData).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("Repository", "Respuesta exitosa de firebase al crear el usuario")
                    } else {
                        Log.d("Repository", "Respuesta fallida de firebase al crear el usuario")
                    }
                }
            return@withContext 5
        }
    }

    // AHORA (usando el repositorio)
    fun prueba(userId: String): Task<List<Transaction>> {
        return db.collection("users")
            .document(userId)
            .collection("transactions")
            .get().continueWith {
                if (it.isSuccessful) {
                    Log.d(
                        "Repository",
                        "Respuesta exitosa de firebase al recuperar las transacciones del usuario"
                    )
                    return@continueWith it.result
                        .map { document ->
                            Log.d(
                                "Firestore",
                                "Transaction: " + document.id + " => " + document.data
                            )
                            Transaction(document.toObject(FireTransaction::class.java))
                        }
                } else {
                    Log.d(
                        "Repository",
                        "Respuesta fallida de firebase al recuperar las transacciones del usuario"
                    )
                    return@continueWith emptyList()
                }
            }
    }

}
