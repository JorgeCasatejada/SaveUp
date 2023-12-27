package com.example.saveup.model.firestore

data class FireParticipant(
    val email: String,
    val username: String,
    val imagePath: String,
    @field:JvmField val isAdmin: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FireParticipant
        if (email != other.email) return false
        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}
