package com.example.saveup.model

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.saveup.view.statistics.LimitsFragment

object Notifications {

    @JvmStatic
    fun simpleNotification(activity: Activity, title: String, body: String, icon: Int) {
        val noti = NotificationCompat.Builder(activity.applicationContext,
            LimitsFragment.SIMPLE_CHANNEL
        )
            .setSmallIcon(icon) //El icono. Es obligatorio. El resto es opcional.
            .setContentTitle(title) // Título
            .setContentText(body) //Cuerpo
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Compatibilidad con API 25 y anteriores. (Android 7.1)

        //El estilo/formato es configurable, échale un ojo:
        //      https://developer.android.com/develop/ui/views/notifications/expanded


        //Descomenta el código siguiente cuando te lo indique.

        /*
        val intent = Intent(this, MainActivity::class.java).apply {
            //Evitamos duplicidad
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        notificacion.setContentIntent(pendingIntent)
            .setAutoCancel(true) // La notificación se cierra al pulsarla.

        */

        //Accedemos al servicio de notificaciones
        val manager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        /*
            Fíjate que siempre pasamos la misma ID. En este caso, porque estoy asumiendo
            que siempre tendremos la MISMA notificación de este tipo.

            ¡Date cuenta que aquí estamos llamando al build() de la notificación!
         */
        manager.notify(LimitsFragment.SIMPLE_NOTIFICATION_ID, noti.build())
    }

}