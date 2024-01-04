package com.example.saveup.model.repository

import android.util.Log
import com.example.saveup.model.Group
import com.example.saveup.model.Transaction
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.model.firestore.FireTransaction
import com.example.saveup.model.firestore.FireUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
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

    fun getUserGroupsRegistration(
        userId: String,
        listener: EventListener<QuerySnapshot>
    ): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("myGroups")
            .addSnapshotListener(listener)
    }

    fun getGroupInfoRegistration(
        group: Group,
        listener: EventListener<DocumentSnapshot>
    ): ListenerRegistration {
        return db.collection("groups")
            .document(group.id)
            .addSnapshotListener(listener)
    }

    suspend fun createGroup(group: Group): String {
        return withContext(Dispatchers.IO) {
            val docRef = db.collection("groups")
                .document()
            group.id = docRef.id
            docRef.set(group.toFireGroup()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Repository", "Respuesta exitosa de firebase al crear el grupo")
                } else {
                    Log.d("Repository", "Respuesta fallida de firebase al crear el grupo")
                }
            }
            return@withContext docRef.id
        }
    }

    suspend fun deleteGroup(groupID: String) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(groupID)
                .collection("participants")
                .get().await().documents.forEach {
                    it.reference.delete()
                }
            db.collection("groups")
                .document(groupID)
                .collection("transactions")
                .get().await().documents.forEach {
                    it.reference.delete()
                }
            db.collection("groups")
                .document(groupID)
                .delete()
        }
    }

    fun getGroupParticipantsRegistration(
        group: Group,
        listener: EventListener<QuerySnapshot>
    ): ListenerRegistration {
        return db.collection("groups")
            .document(group.id)
            .collection("participants")
            .addSnapshotListener(MetadataChanges.INCLUDE, listener)
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
                .await()
            if (p.isEmpty) {
                return@withContext FireParticipant()
            } else {
                return@withContext p.map { document ->
                    Log.d("Firestore", "Participante: " + document.id + " => " + document.data)
                    document.toObject(FireUser::class.java).toFireParticipant()
                }[0]
            }
        }
    }

    suspend fun addParticipantToGroup(group: Group, groupParticipant: FireParticipant) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(groupParticipant.id)
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
                .document(groupParticipant.id)
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

    suspend fun deleteParticipantFromGroup(group: Group, participantID: String) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(group.id)
                .collection("participants")
                .document(participantID)
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

    suspend fun deleteGroupFromMyGroups(groupID: String, participantID: String) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(participantID)
                .collection("myGroups")
                .document(groupID)
                .delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al eliminar el grupo de la lista de grupos del usuario"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al eliminar el grupo de la lista de grupos del usuario"
                        )
                    }
                }
        }
    }

    fun getGroupTransactionsRegistration(
        group: Group,
        listener: EventListener<QuerySnapshot>
    ): ListenerRegistration {
        return db.collection("groups")
            .document(group.id)
            .collection("transactions")
            .addSnapshotListener(listener)
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
            db.collection("groups")
                .document(group.id)
                .update("currentBudget", FieldValue.increment(transaction.signedValue))
            return@withContext docRef.id
        }
    }

    suspend fun deleteTransactionFromGroup(transaction: Transaction, id: String, budget: Double) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(id)
                .collection("transactions")
                .document(transaction.transactionID)
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

            db.collection("groups")
                .document(id)
                .update("currentBudget", budget)
        }
    }

    suspend fun modifyTransactionFromGroup(
        transaction: Transaction,
        id: String,
        budget: Double
    ) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(id)
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
            db.collection("groups")
                .document(id)
                .update("currentBudget", budget)
        }
    }

}