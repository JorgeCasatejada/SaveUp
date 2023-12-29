package com.example.saveup.model.firestore

import java.util.Date

data class FireGoal(
    val name: String = "",
    val initialDate: Date? = null,
    val finalDate: Date? = null,
    val initialBalance: Double = 0.0,
    val objectiveBalance: Double? = null,
    var goalID: String = "",
) {
    override fun toString(): String {
        return "Goal [name=$name, initialDate=(${initialDate?.date}/${initialDate?.month?.plus(1)}/${initialDate?.year?.plus(1900)}), finalDate=(${finalDate?.date}/${finalDate?.month?.plus(1)}/${finalDate?.year?.plus(1900)}), initialBalance=$initialBalance, objectiveBalance=$objectiveBalance"
    }
}
