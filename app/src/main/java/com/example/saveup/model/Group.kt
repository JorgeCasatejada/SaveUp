package com.example.saveup.model

import com.example.saveup.model.firestore.FireGroup
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.model.firestore.FireUserGroup
import java.util.Collections

data class Group(
    var id: String = "",
    var title: String = "",
    var initialBudget: Double = 0.0,
    var currentBudget: Double = 0.0,
    var description: String = "",
    var urlGroupImage: String = "",
    var participants: MutableList<FireParticipant> = mutableListOf(),
    var transactionList: MutableList<Transaction> = mutableListOf()
) {

    constructor(group: FireUserGroup) : this(
        group.id,
        group.title,
        urlGroupImage = group.urlGroupImage
    )

    constructor(group: FireGroup) : this(
        group.id,
        group.title,
        group.initialBudget,
        group.currentBudget,
        group.description,
        group.urlGroupImage
    )

    fun toFireGroup(): FireGroup {
        return FireGroup(
            id,
            title,
            initialBudget,
            currentBudget,
            description,
            urlGroupImage
        )
    }

    fun toFireUserGroup(): FireUserGroup {
        return FireUserGroup(
            id,
            title,
            urlGroupImage
        )
    }

    fun addData(newGroupData: FireGroup) {
        this.title = newGroupData.title
        this.initialBudget = newGroupData.initialBudget
        this.currentBudget = newGroupData.currentBudget
        this.description = newGroupData.description
        this.urlGroupImage = newGroupData.urlGroupImage
    }

    fun setParticipantsList(groupParticipants: List<FireParticipant>) {
        participants = groupParticipants.toMutableList()
    }

    fun setTransactionsList(groupTransactions: List<Transaction>) {
        transactionList = groupTransactions.toMutableList()
        transactionList.sortWith(Collections.reverseOrder(Comparator.comparing(Transaction::getDate)))
        currentBudget = recalculateBudget()
    }

    private fun recalculateBudget(): Double {
        var budget = initialBudget
        for (t in transactionList) {
            budget += t.signedValue
        }
        return budget
    }

    fun deleteParticipant(participant: FireParticipant) {
        participants.remove(participant)
    }

    fun addParticipant(participant: FireParticipant) {
        participants.add(participant)
    }

    fun addTransaction(transaction: Transaction) {
        transactionList.add(transaction)
        transactionList.sortWith(Collections.reverseOrder(Comparator.comparing(Transaction::getDate)))
        currentBudget += transaction.signedValue
    }

    fun removeTransaction(transaction: Transaction) {
        transactionList.remove(transaction)
        currentBudget -= transaction.signedValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Group
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}