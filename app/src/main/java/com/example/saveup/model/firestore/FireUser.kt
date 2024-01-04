package com.example.saveup.model.firestore

data class FireUser(
    val email: String = "",
    val monthlyLimit: Double? = null,
    val goal: FireGoal? = null,
)
