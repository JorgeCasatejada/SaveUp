package com.example.saveup.model

data class Group (
    val title: String,
    val budget: Double,
    val description: String,
    var participants: MutableList<String>,
    val transactionList: MutableList<Transaction>,
    val urlGroupImage: String
) {
    fun deleteParticipant(id: String) {
        participants.remove(id)
    }
    fun addParticipant(id: String) {
        participants.add(id)
    }
    fun addTransaction(transaction: Transaction) {
        transactionList.add(transaction)
    }
}