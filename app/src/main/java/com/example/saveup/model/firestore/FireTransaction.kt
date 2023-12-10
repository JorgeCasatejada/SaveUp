package com.example.saveup.model.firestore

import com.example.saveup.model.Category
import java.util.Date

data class FireTransaction(
    val transactionID: String = "",
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isExpense: Boolean = true,
    val name: String = "",
    val value: Double = 0.0,
    val category: Category? = null,
    val date: Date? = null,
    val description: String? = null,
)
