package com.twoeightnine.root.xvii.background.test
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.login.LoginActivity
import com.twoeightnine.root.xvii.main.MainActivity

class ZeForegroundService : Service() {
    private val CHANNEL_ID = "ZeForegroundService Kotlin"
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun launch(context: Context) {
            /*val request:WorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()


            WorkManager.getInstance(context).enqueue(request)*/
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun launch2(context: Context) {
            val startIntent = Intent(context, ZeForegroundService::class.java)
            startIntent.putExtra("inputExtra", "Ze Test")
            context.startForegroundService(startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ZeForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_key)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //stopSelf();
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Ze Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}