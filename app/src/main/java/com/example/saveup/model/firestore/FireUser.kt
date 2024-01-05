package com.example.saveup.model.firestore

data class FireUser(
    val id: String = "",
    val email: String = "",
    val userName: String = "",
    val imagePath: String = "",
    val monthlyLimit: Double? = null,
) {

    fun toFireParticipant(): FireParticipant {
        return FireParticipant(id, email, userName, imagePath, false)
    }
}


