package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.service.FCMService.Action.*
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    companion object {
        private const val DATA_ACTION_KEY = "action"
        private const val DATA_CONTENT_KEY = "content"
        private const val CHANNEL_ID = "remote"
        private val gson by lazy { Gson() }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[DATA_ACTION_KEY]?.let {
            when (it) {
                LIKE.toString() -> handLike(
                    gson.fromJson(
                        message.data[DATA_CONTENT_KEY],
                        Like::class.java
                    )
                )
                NEW_POST.toString() -> handNewPost(
                    gson.fromJson(
                        message.data[DATA_CONTENT_KEY],
                        NewPost::class.java
                    )
                )
                else -> handOther(
                    gson.fromJson(
                        message.data[DATA_CONTENT_KEY],
                        Other::class.java
                    )
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM token", "token: $token")
    }

    enum class Action {
        LIKE,
        NEW_POST
    }

    private fun handLike(content: Like) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handNewPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_post,
                    content.userName,
                )
            )
            .setContentText(
                getString(
                    R.string.notification_content_post,
                    content.content
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.content))
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)

    }

    private fun handOther(content: Other) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_fail
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
    )

    data class NewPost(
        val userName: String,
        val content: String
    )

    data class Other(
        val content: String
    )
}