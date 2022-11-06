package com.example.slpn.fragments.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.example.slpn.activity.App.Companion.CHANNEL_ID
import com.example.slpn.activity.MainActivity
import com.example.slpn.fragmenty.repository.FirebaseRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService: FirebaseMessagingService() {

    private val repository = FirebaseRepository()
    // Aktualizacja Tokena uzytkownika
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //Aktualizacja Tokena uzytkownika
        pushNewToken()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("title", message.data["title"])
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
               //todo .setSmallIcon(R.drawable.ic_baseline_package_24)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        } else {
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
              //todo  .setSmallIcon(R.drawable.ic_baseline_package_24)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }
    }

    private fun pushNewToken()
    {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        println("Fetching FCM registration token failed")
                        return
                    }

                    // Get new FCM registration token
                    val token: String? = task.getResult()

                    // Log and toast
                    token?.let { Log.d("moj token ", it) }

                    /*Toast.makeText(
                        this@MainActivity,
                        "Your device registration token is$token",
                        Toast.LENGTH_SHORT
                    ).show()*/
                  //todo  repository.pushToken(token.toString())
                }
            })
    }

    private fun createChannel(notificationManager: NotificationManager){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(CHANNEL_ID,"ExampleServiceChannel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Description"
                enableLights(true)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(serviceChannel)
        }

    }
}