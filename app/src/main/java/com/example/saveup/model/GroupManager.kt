package com.example.saveup.model

import com.example.saveup.model.firestore.FireParticipant

class GroupManager {

    fun isAdmin(group: List<FireParticipant>, userEmail: String): Boolean {
        val user = group.find { it.email == userEmail }
        return user?.isAdmin ?: false
    }

    fun isParticipantInGroup(group: List<FireParticipant>, userEmail: String): Boolean {
        val user = group.find { it.email == userEmail }
        return user != null
    }
}