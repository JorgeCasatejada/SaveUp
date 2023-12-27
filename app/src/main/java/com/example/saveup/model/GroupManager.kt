package com.example.saveup.model

import com.example.saveup.model.firestore.FireGroup
import com.example.saveup.model.firestore.FireParticipant

class GroupManager {

    var groupList: MutableList<Group>? = null

    fun addGroup(group: Group) {
        groupList?.add(group)
    }

    fun removeGroup(group: Group) {
        groupList?.remove(group)
    }

    fun getGroup(groupId: String): Group? {
        return groupList?.find { it.id == groupId }
    }

    fun addDataToGroup(newGroupData: FireGroup) {
        val group = groupList?.find { it.id == newGroupData.id }
        if (group == null) addGroup(Group(newGroupData))
        else group.addData(newGroupData)
    }

    fun addParticipantToGroup(groupParticipant: FireParticipant, group: Group) {
        group.addParticipant(groupParticipant)
    }

    fun addParticipantsToGroup(groupParticipants: List<FireParticipant>, group: Group) {
        group.setParticipantsList(groupParticipants)
    }

    fun removeParticipantFromGroup(participant: FireParticipant, group: Group) {
        group.deleteParticipant(participant)
    }

    fun addTransactionsToGroup(groupTransactions: List<Transaction>, group: Group) {
        group.setTransactionsList(groupTransactions)
    }

    fun addTransactionToGroup(transaction: Transaction, group: Group) {
        group.addTransaction(transaction)
    }

    fun removeTransactionFromGroup(transaction: Transaction, group: Group) {
        group.removeTransaction(transaction)
    }

    fun modifyTransactionFromGroup(
        transactionOld: Transaction,
        transactionNew: Transaction,
        group: Group
    ) {
        group.removeTransaction(transactionOld)
        group.addTransaction(transactionNew)
    }
}