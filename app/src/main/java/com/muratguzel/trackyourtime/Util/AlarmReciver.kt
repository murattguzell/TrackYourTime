package com.muratguzel.trackyourtime.Util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.muratguzel.trackyourtime.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Intent'ten gelen mesajı ve başlığı alıyoruz
        val message = intent.getStringExtra("notification_message") ?: "Zaman Doldu"
        val title = intent.getStringExtra("notification_title") ?: ""

        // Title'ı kontrol ediyoruz, eğer boşsa "Geri sayım" yapıyoruz
        val notificationTitle = if (title.isNotEmpty()) title else "Geri sayım"

        // Bildirimi gönderme işlemini başlatıyoruz
        sendNotification(context, notificationTitle, message)
    }

    // Bildirimi oluşturup gönderen fonksiyon
    private fun sendNotification(context: Context, title: String, message: String) {
        val notificationId = System.currentTimeMillis().toInt() // Her bildirim için benzersiz ID
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Bildirim kanalı oluştur (Android O ve üzeri için)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "COUNTDOWN_CHANNEL",
                "Zamanlayıcı Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirimi oluştur
        val notification = NotificationCompat.Builder(context, "COUNTDOWN_CHANNEL")
            .setSmallIcon(R.drawable.calender_icon) // Bildirim simgesi
            .setContentTitle(title) // Kullanıcının girdiği başlık veya varsayılan başlık
            .setContentText(message) // Bildirim içeriği (mesaj)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Bildirimi göster
        notificationManager.notify(notificationId, notification)
    }
}
