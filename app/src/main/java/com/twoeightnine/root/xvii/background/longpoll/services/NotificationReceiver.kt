package com.twoeightnine.root.xvii.background.longpoll.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.*
import com.twoeightnine.root.xvii.App
import com.twoeightnine.root.xvii.scheduled.core.SendMessageWorker
import com.twoeightnine.root.xvii.utils.showToast

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //if (intent.action == "zug") {
            //App.appComponent?.inject(this)
        Toast.makeText(context, "ZAZAZA", Toast.LENGTH_LONG).show()
        val zzz = 1
        //}
    }

    companion object {

        fun permissionBattery(baseContext:Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName: String = baseContext.packageName
                val pm: PowerManager =
                    baseContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.data = Uri.parse("package:$packageName")
                    baseContext.startActivity(intent)
                }
            }
        }


        @RequiresApi(Build.VERSION_CODES.S)
        fun launch(context:Context) {

            permissionBattery(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
                if (alarmManager?.canScheduleExactAlarms() == false) {
                    Intent().also { intent ->
                        intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        context.startActivity(intent)
                    }
                }else{

                    /*Intent alarmIntent = new Intent(context, HabitBroadcastReceiver.class);
                    alarmIntent.setAction(HabitBroadcastReceiver.ACTION_SHOW_REMINDER);
                    alarmIntent.setData(uri);
                    alarmIntent.putExtra("timestamp", timestamp);
                    alarmIntent.putExtra("reminderTime", reminderTime);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    ((int) (habit.getId() % Integer.MAX_VALUE)) + 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/


                    val alarmIntent: PendingIntent = Intent(context, NotificationReceiver::class.java).let { intent ->
                        PendingIntent.getBroadcast(context, 0, intent, 0)
                    }



                    val reminderTime = SystemClock.elapsedRealtime() + 60 * 3 * 1000
                    alarmManager?.setExact(AlarmManager.RTC_WAKEUP, reminderTime, alarmIntent);

                }
            }

        }
    }
}