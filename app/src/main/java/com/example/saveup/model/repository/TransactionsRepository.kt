package com.example.saveup.model.repository

import android.util.Log
import com.example.saveup.model.Group
import com.example.saveup.model.Transaction
import com.example.saveup.model.firestore.FireGroup
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.model.firestore.FireTransaction
import com.example.saveup.model.firestore.FireUser
import com.example.saveup.model.firestore.FireUserGroup
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

    suspend fun getUserGroups(userId: String): MutableList<Group> {
        return withContext(Dispatchers.IO) {
            val groups = db.collection("users")
                .document(userId)
                .collection("myGroups")
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al recuperar los grupos del usuario"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al recuperar los grupos del usuario"
                        )
                    }
                }
                .await().map { document ->
                    Log.d("Firestore", "Group: " + document.id + " => " + document.data)
                    Group(document.toObject(FireUserGroup::class.java))
                }.toMutableList()
            return@withContext groups
        }
    }

    suspend fun getGroup(group: Group): FireGroup? {
        return withContext(Dispatchers.IO) {
            val newGroupData = db.collection("groups")
                .document(group.id)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("Repository", "Respuesta exitosa de firebase al crear el grupo")
                    } else {
                        Log.d("Repository", "Respuesta fallida de firebase al crear el grupo")
                    }
                }
                .await()
                .toObject(FireGroup::class.java)
            return@withContext newGroupData
        }
    }

    suspend fun createGroup(group: Group): String {
        return withContext(Dispatchers.IO) {
            val docRef = db.collection("groups")
                .document()
            group.id = docRef.id
            docRef.set(group).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Repository", "Respuesta exitosa de firebase al crear el grupo")
                } else {
                    Log.d("Repository", "Respuesta fallida de firebase al crear el grupo")
                }
            }
            return@withContext docRef.id
        }
    }

    suspend fun getGroupParticipants(group: Group): List<FireParticipant> {
        return withContext(Dispatchers.IO) {
            val groupParticipants = db.collection("groups")
                .document(group.id)
                .collection("participants")
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al recuperar los participantea del grupo"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al recuperar los participantea del grupo"
                        )
                    }
                }.await().map { document ->
                    Log.d("Firestore", "Participante: " + document.id + " => " + document.data)
                    document.toObject(FireParticipant::class.java)
                }
            return@withContext groupParticipants
        }
    }

    suspend fun getParticipant(participant: String): FireParticipant {
        return withContext(Dispatchers.IO) {
            val p = db.collection("users")
                .whereEqualTo("email", participant)
                .limit(1)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al recuperar el participante"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al recuperar el participante"
                        )
                    }
                }
                .await().map { document ->
                    Log.d("Firestore", "Participante: " + document.id + " => " + document.data)
                    document.toObject(FireUser::class.java).toFireParticipant()
                }[0]
            return@withContext p
        }
    }

    suspend fun addParticipantToGroup(group: Group, groupParticipant: FireParticipant) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(groupParticipant.email)
                .collection("myGroups")
                .document(group.id)
                .set(group.toFireUserGroup()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al añadir el grupo al usuario"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al añadir el grupo al usuario"
                        )
                    }
                }
            db.collection("groups")
                .document(group.id)
                .collection("participants")
                .document(groupParticipant.email)
                .set(groupParticipant).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al añadir el participante al grupo"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al añadir el participante al grupo"
                        )
                    }
                }
        }
    }

    suspend fun deleteParticipantFromGroup(group: Group, participant: String) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(group.id)
                .collection("participants")
                .document(participant)
                .delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al eliminar el participante del grupo"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al eliminar el participante del grupo"
                        )
                    }
                }
        }
    }

    suspend fun getGroupTransactions(group: Group): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val groupTransactions = db.collection("groups")
                .document(group.id)
                .collection("transactions")
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al recuperar las transacciones del grupo"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al recuperar las transacciones del grupo"
                        )
                    }
                }.await().map { document ->
                    Log.d("Firestore", "Transacción: " + document.id + " => " + document.data)
                    Transaction(document.toObject(FireTransaction::class.java))
                }
            return@withContext groupTransactions
        }
    }

    suspend fun addTransactionToGroup(transaction: Transaction, group: Group): String {
        return withContext(Dispatchers.IO) {
            val docRef = db.collection("groups")
                .document(group.id)
                .collection("transactions").document()
            transaction.transactionID = docRef.id
            docRef.set(transaction.toFirestore()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "Repository",
                        "Respuesta exitosa de firebase al añadir la transacción al grupo"
                    )
                } else {
                    Log.d(
                        "Repository",
                        "Respuesta fallida de firebase al añadir la transacción al grupo"
                    )
                }
            }
            return@withContext docRef.id
        }
    }

    suspend fun deleteTransactionFromGroup(transactionId: String, group: Group) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(group.id)
                .collection("transactions")
                .document(transactionId)
                .delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al eliminar la transacción del grupo"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al eliminar la transacción del grupo"
                        )
                    }
                }
        }
    }

    suspend fun modifyTransactionFromGroup(transaction: Transaction, group: Group) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(group.id)
                .collection("transactions")
                .document(transaction.transactionID)
                .set(transaction.toFirestore()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al modificar la transacción del grupo"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al modificar la transacción del grupo"
                        )
                    }
                }
        }
    }

}
