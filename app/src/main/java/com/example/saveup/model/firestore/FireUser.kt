package com.example.saveup.model.firestore

data class FireUser(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val imagePath: String = "",
    val monthlyLimit: Double? = null,
    val goal: FireGoal? = null,
) {

    fun toFireParticipant(): FireParticipant {
        return FireParticipant(id, email, username, imagePath, false)
    }
}
