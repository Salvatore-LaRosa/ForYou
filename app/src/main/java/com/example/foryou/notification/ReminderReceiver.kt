package com.example.foryou.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.foryou.MainActivity
import com.example.foryou.R

/**
 * A [BroadcastReceiver] that handles scheduled medicine reminders.
 * It receives an intent from the [AlarmManager], builds, and displays a notification.
 */
class ReminderReceiver : BroadcastReceiver() {

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Medicine"
        val message = "It's time to take your $medicineName"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an intent to launch MainActivity when the notification is tapped.
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
        }

        // Use TaskStackBuilder to create a PendingIntent that preserves the back stack.
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(
                medicineName.hashCode(), // Use the medicine name's hash code as a unique request code.
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Build the notification.
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Ensure this drawable exists.
            .setContentTitle("Medicine Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss the notification when tapped.
            .setContentIntent(pendingIntent) // Set the action to perform when tapped.
            .build()

        // Display the notification. The medicine name's hash code is used as a unique notification ID.
        notificationManager.notify(medicineName.hashCode(), notification)
    }

    companion object {
        // Constants for intent extras and notification channel ID.
        const val EXTRA_MEDICINE_NAME = "medicineName"
        const val CHANNEL_ID = "medicine_reminder_channel"
    }
}
