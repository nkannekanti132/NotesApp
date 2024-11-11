package com.example.notesapp.utils

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast

fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            // Redirect the user to the system settings for exact alarm permission
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(context, "Please grant exact alarm permission", Toast.LENGTH_LONG).show()
        }
    }
}