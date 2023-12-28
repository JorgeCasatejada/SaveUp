package com.example.saveup.model.firestore

data class FireGroup(
    val id: String,
    val title: String,
    val initialBudget: Double,
    val currentBudget: Double,
    val description: String,
    val urlGroupImage: String
) {
    constructor() : this("", "", 0.0, 0.0, "", "")
}
