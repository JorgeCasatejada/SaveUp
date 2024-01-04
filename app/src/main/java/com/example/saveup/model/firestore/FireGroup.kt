package com.example.saveup.model.firestore

data class FireGroup(
    val id: String = "",
    val title: String = "",
    val initialBudget: Double = 0.0,
    val currentBudget: Double = 0.0,
    val description: String = "",
    val urlGroupImage: String = ""
)
