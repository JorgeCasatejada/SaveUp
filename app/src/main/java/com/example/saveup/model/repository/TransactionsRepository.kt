package com.example.saveup.model.repository

import android.net.Uri
import android.util.Log
import com.example.saveup.model.Group
import com.example.saveup.model.Transaction
import com.example.saveup.model.firestore.FireGoal
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.model.firestore.FireTransaction
import com.example.saveup.model.firestore.FireUser
import com.example.saveup.model.firestore.FireUserGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionsRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference

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

    fun getCurrentUserRegistration(
        userId: String,
        listener: EventListener<DocumentSnapshot>
    ): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .addSnapshotListener(listener)
    }

    suspend fun uploadUserImage(userId: String, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val photoRef = storageRef.child("profilePics").child(userId)
            var success = false
            var imageUrl = ""
            photoRef.putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Storage", "Respuesta exitosa de firebase al guardar la imagen")
                    success = true
                } else {
                    Log.d("Storage", "Respuesta fallida de firebase al guardar la imagen")
                }
            }.await()
            if (success) {
                photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    imageUrl = downloadUrl.toString()
                }.await()
            }
            return@withContext imageUrl
        }
    }

    suspend fun updateUserImageInGroup(groupId: String, userId: String, imagePath: String) {
        withContext(Dispatchers.IO) {
            db.collection("groups")
                .document(groupId)
                .collection("participants")
                .document(userId)
                .get().addOnSuccessListener { document ->
                    document?.reference?.update("imagePath", imagePath)?.addOnSuccessListener {
                        Log.d("Repository", "Actualizada foto de usuario $userId en grupo $groupId")
                    }
                }
        }
    }

    suspend fun updateAuthUser(userName: String) {
        withContext(Dispatchers.IO) {
            auth.currentUser!!.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(userName).build()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "Auth",
                        "Respuesta exitosa de firebase al modificar usuario"
                    )
                } else {
                    Log.d(
                        "Auth",
                        "Respuesta fallida de firebase al modificar usuario"
                    )
                }
            }
        }
    }

    suspend fun modifyUserFireStore(userData: MutableMap<String, Any>) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update(userData).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al modificar usuario"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al modificar usuario"
                        )
                    }
                }
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

    suspend fun deleteMonthlyLimit() {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update("monthlyLimit", null).addOnCompleteListener {
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

    suspend fun getUserGroups(
        userId: String,
    ): List<FireUserGroup> {
        return withContext(Dispatchers.IO) {
            db.collection("users")
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
                }.await().map { document ->
                    Log.d("Firestore", "Group: " + document.id + " => " + document.data)
                    document.toObject(FireUserGroup::class.java)
                }
        }
    }

    fun getGroupInfoRegistration(
        group: Group,
        listener: EventListener<DocumentSnapshot>
    ): ListenerRegistration {
        return db.collection("groups")
            .document(group.id)
            .addSnapshotListener(listener)
    }

    suspend fun uploadGroupImage(groupId: String, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val photoRef = storageRef.child("groupPics").child(groupId)
            var success = false
            var imageUrl = ""
            photoRef.putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Storage", "Respuesta exitosa de firebase al guardar la imagen")
                    success = true
                } else {
                    Log.d("Storage", "Respuesta fallida de firebase al guardar la imagen")
                }
            }.await()
            if (success) {
                photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    imageUrl = downloadUrl.toString()
                }.await()
            }
            return@withContext imageUrl
        }
    }

    fun getNewIdForGroup(): String {
        val docRef = db.collection("groups")
            .document()
        return docRef.id
    }

    suspend fun createGroup(group: Group): String {
        return withContext(Dispatchers.IO) {
            val docRef = db.collection("groups")
                .document(group.id)
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

    suspend fun updateGoal(goal: FireGoal) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update(mapOf("goal" to goal)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al modificar la meta"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al modificar la meta"
                        )
                    }
                }
        }
    }

    suspend fun deleteGoal() {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update(mapOf("goal" to null)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al eliminar la meta"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al eliminar la meta"
                        )
                    }
                }
        }
    }

    suspend fun getGoal(userId: String): FireGoal? {
        return withContext(Dispatchers.IO) {
            val respuesta = db.collection("users")
                .document(userId)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(
                            "Repository",
                            "Respuesta exitosa de firebase al recuperar la meta del usuario"
                        )
                    } else {
                        Log.d(
                            "Repository",
                            "Respuesta fallida de firebase al recuperar la meta del usuario"
                        )
                    }
                }
                .await().get("goal", FireGoal::class.java)
            return@withContext respuesta
        }
    }

}
