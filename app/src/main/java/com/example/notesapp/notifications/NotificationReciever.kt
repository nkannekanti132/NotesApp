package com.example.notesapp.notifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val title = intent.getStringExtra("title")
            val message = intent.getStringExtra("message")

            val notificationHelper = NotificationHelper(context)
            notificationHelper.sendNotification(title ?: "Reminder", message ?: "It's time for your note.")
        }
    }
}
