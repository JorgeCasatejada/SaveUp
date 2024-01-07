package com.example.saveup.model

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.saveup.R
import com.example.saveup.model.firestore.FireGoal
import com.example.saveup.view.MainActivity
import com.example.saveup.view.statistics.LimitsGoalsFragment
import com.example.saveup.viewModel.MainViewModel
import java.util.Date

// https://developer.android.com/develop/ui/views/notifications/expanded
object Notifications {

    @JvmStatic
    fun simpleNotification(activity: Activity, title: String, body: String, id: Int) {
        val noti = NotificationCompat.Builder(
            activity.applicationContext,
            LimitsGoalsFragment.SIMPLE_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_notification) //El icono. Es obligatorio. El resto es opcional.
            .setContentTitle(title) // Título
            .setContentText(body) //Cuerpo
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Compatibilidad con API 25 y anteriores. (Android 7.1)

        val intent = Intent(activity.applicationContext, MainActivity::class.java).apply {
            //Evitamos duplicidad
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            activity.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        noti.setContentIntent(pendingIntent)
            .setAutoCancel(true) // La notificación se cierra al pulsarla.

        // Accedemos al servicio de notificaciones
        val manager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(id, noti.build())
    }

    @JvmStatic
    fun checkLimit(activity: Activity, viewModel: MainViewModel) {
        val expenses: Double? = viewModel.getMonthlyExpenses()
        val limit: Double? = viewModel.monthlyLimit.value
        if (expenses != null && limit != null) {
            if (expenses >= limit) {
                notifyLimitExceeded(activity, viewModel)
            }
        }
    }

    @JvmStatic
    fun checkGoal(activity: Activity, viewModel: MainViewModel) {
        val balance: Double? = viewModel.balance.value
        val goal: FireGoal? = viewModel.goal.value
        val currentDate = Date()
        if (balance != null) {
            if (goal != null) {
                val objectiveBalance = goal.objectiveBalance
                val finalDate = goal.finalDate
                if (objectiveBalance != null) {
                    if (currentDate < finalDate) { // Comprobar si ha llegado a la meta
                        if (balance >= objectiveBalance) {
                            notifyGoalReached(activity, viewModel)
                        }
                    } else { // Comprobar si se ha pasado la fecha de la meta
                        if (balance < objectiveBalance) {
                            notifyGoalNotReached(activity, viewModel)
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    private fun notifyLimitExceeded(activity: Activity, viewModel: MainViewModel) {
        simpleNotification(
            activity,
            "¡Atención! Ha Excedido Su Límite Mensual",
            "El límite mensual que ha creado de " + viewModel.monthlyLimit.value
                    + " € ha sido excedido, ahora mismo sus gastos mensuales son "
                    + viewModel.getMonthlyExpenses() + "€",
            LimitsGoalsFragment.LIMIT_REACHED_NOTIFICATION_ID
        )
    }

    private fun notifyGoalReached(activity: Activity, viewModel: MainViewModel) {
        val (name, _, date, _, value) = viewModel.goal.value ?: return
        val title = "¡Atención! Ha Llegado a su meta $name\n"
        val builder = StringBuilder()
        if (value != null) {
            builder.append("Ha llegado a ").append(value).append(" €").append("\n")
        }
        if (date != null) {
            val dateFormatted =
                date.date.toString() + "/" + date.month + 1 + "/" + (date.year + 1900)
            builder.append("Antes del ").append(dateFormatted)
        }
        simpleNotification(
            activity,
            title,
            builder.toString(),
            LimitsGoalsFragment.GOAL_REACHED_NOTIFICATION_ID
        )
    }

    private fun notifyGoalNotReached(activity: Activity, viewModel: MainViewModel) {
        val (name, _, date, _, value) = viewModel.goal.value ?: return
        val title = "¡Atención! Su meta $name ha expirado\n"
        val builder = StringBuilder()
        if (value != null) {
            builder.append("No ha llegado a ").append(value).append(" €").append("\n")
        }
        if (date != null) {
            val dateFormatted =
                date.date.toString() + "/" + date.month + 1 + "/" + (date.year + 1900)
            builder.append("Antes del ").append(dateFormatted)
        }
        simpleNotification(
            activity,
            title,
            builder.toString(),
            LimitsGoalsFragment.GOAL_NOT_REACHED_NOTIFICATION_ID
        )
    }

}