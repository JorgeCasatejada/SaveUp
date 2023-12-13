package com.example.saveup.repositorios

import android.util.Log
import com.example.saveup.model.Group
import com.example.saveup.model.Transaction
import com.example.saveup.model.firestore.FireTransaction
import com.example.saveup.model.firestore.FireUser
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
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al recuperar las transacciones del usuario"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al recuperar las transacciones del usuario"
                        )
                    }
                }
                .await().map { document ->
                    Log.d("Firestore", "Transaction: " + document.id + " => " + document.data)
                    Transaction(document.toObject(FireTransaction::class.java))
                }
            return@withContext respuesta
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

    suspend fun createUser(userId: String?, userData: HashMap<String, String?>): Boolean {
        return withContext(Dispatchers.IO) {
            var completed = false
            db.collection("users")
                .document(userId.orEmpty())
                .set(userData).addOnCompleteListener {
                    completed = true
                    if (it.isSuccessful) {
                        Log.d("Repository", "Respuesta exitosa de firebase al crear el usuario")
                    } else {
                        Log.d("Repository", "Respuesta fallida de firebase al crear el usuario")
                    }
                }.await()
            return@withContext completed
        }
    }

    suspend fun updateMonthlyLimit(limit: Double) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update("monthlyLimit", limit).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al modificar el límite mensual"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al modificar el límite mensual"
                        )
                    }
                }
        }
    }

    suspend fun getMonthlyLimit(): Double? {
        return withContext(Dispatchers.IO) {
            return@withContext db.collection("users")
                .document(auth.currentUser!!.uid)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al modificar el límite mensual"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al modificar el límite mensual"
                        )
                    }
                }
                .await().toObject(FireUser::class.java)?.monthlyLimit
        }
    }

    suspend fun getUserGroups(userId: String): List<Group> {
        return withContext(Dispatchers.IO) {
            val groups = ArrayList<Group>()
            val participants = ArrayList<String>()
            participants.add("Alice")
            participants.add("Bob")

            val transactionList = ArrayList<Transaction>()

            val group1 = Group(
                "Vacation Group",
                1000.0,
                "Summer Vacation",
                participants,
                transactionList,
                ""
            )
            val group2 = Group(
                "Vacation Group",
                1000.0,
                "Summer Vacation",
                participants,
                transactionList,
                "https://via.placeholder.com/20"
            )

            groups.add(group2)
            groups.add(group1)

            return@withContext groups
        }
    }

}
