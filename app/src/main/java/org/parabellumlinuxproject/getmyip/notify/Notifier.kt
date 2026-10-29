package org.parabellumlinuxproject.getmyip.notify

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import org.parabellumlinuxproject.getmyip.MainActivity
import org.parabellumlinuxproject.getmyip.R
import org.parabellumlinuxproject.getmyip.core.Family
import org.parabellumlinuxproject.getmyip.history.IpChange

object Notifier {
    private const val CHANNEL_ID = "ip_changes"
    private const val NOTIFICATION_ID = 1

    fun canNotify(context: Context): Boolean {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return false
        if (!nm.areNotificationsEnabled()) return false
        return Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun ipChanged(context: Context, changes: List<IpChange>) {
        if (changes.isEmpty() || !canNotify(context)) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = context.getString(R.string.notif_channel_desc) }
        )
        val text = changes.joinToString("\n") { c ->
            val label = context.getString(if (c.family == Family.V4) R.string.family_v4 else R.string.family_v6)
            "$label: ${c.previous} → ${c.current}"
        }
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        // On the lock screen only "Your IP address changed" is shown, never the addresses.
        val publicVersion = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_ip)
            .setContentTitle(context.getString(R.string.notif_public))
            .build()
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_ip)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setVisibility(Notification.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .build()
        nm.notify(NOTIFICATION_ID, notification)
    }
}
