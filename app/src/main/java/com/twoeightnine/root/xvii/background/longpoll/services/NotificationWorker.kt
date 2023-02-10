/*
 * $Id: $
 * Ответственный: Попова
 */
package com.twoeightnine.root.xvii.background.longpoll.services

import android.R
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.work.*
import com.twoeightnine.root.xvii.main.MainActivity
import com.twoeightnine.root.xvii.scheduled.core.SendMessageWorker
import com.twoeightnine.root.xvii.utils.NotificationChannels
import kotlin.random.Random


class NotificationWorker(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    val keyExchanges = NotificationChannels.Channel(
        id = "xvii.key_exchanges",
        name = com.twoeightnine.root.xvii.R.string.channel_key_exchanges,
        description = 0,
        importance = NotificationManager.IMPORTANCE_HIGH,
        sound = false,
        vibrate = true
    )

    fun createNotification(ctx: Context): ForegroundInfo{
        val intent = Intent(ctx, MainActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(ctx, keyExchanges.id)
            .setAutoCancel(false)
            .setDefaults(Notification.DEFAULT_ALL)
            //.setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_menu_my_calendar)
            .setTicker("Hearty365")
            .setContentTitle("WW Default notification")
            .setContentText("WW Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            //.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setContentIntent(contentIntent)
            .setContentInfo("Info")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setOngoing(true)
            .build()

        val notificationId = 8888 /*Random.nextInt()*/
        /*(ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
            ?.notify(notificationId, notification)*/
        return ForegroundInfo(notificationId, notification)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createNotification(context)
    }

    override suspend fun doWork(): Result {
        return try {
            setForeground(getForegroundInfo())
            while(true){
            }
            return Result.success()
        } catch(e: Exception) {
            Result.failure()
        }
    }
    companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        fun launch(context:Context) {
            val inputData =  workDataOf(
            SendMessageWorker.ARG_MESSAGE_ID to "ZZZ"
            )

            val request = OneTimeWorkRequestBuilder<NotificationWorker>().apply {
                setInputData(inputData)
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            }.build()

            //request.id

            WorkManager.getInstance(context)
                .enqueueUniqueWork("ZUG", ExistingWorkPolicy.REPLACE, request)
                //.enqueue(request)
        }
    }
}
