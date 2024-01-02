package com.example.saveup.model

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.saveup.view.MainActivity
import com.example.saveup.view.statistics.LimitsGoalsFragment

// https://developer.android.com/develop/ui/views/notifications/expanded
object Notifications {

    @JvmStatic
    fun simpleNotification(activity: Activity, title: String, body: String, icon: Int, id: Int) {
        val noti = NotificationCompat.Builder(
            activity.applicationContext,
            LimitsGoalsFragment.SIMPLE_CHANNEL
        )
            .setSmallIcon(icon) //El icono. Es obligatorio. El resto es opcional.
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

}