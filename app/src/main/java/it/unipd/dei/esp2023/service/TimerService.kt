package it.unipd.dei.esp2023.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.R

class TimerService : Service() {

    private lateinit var mMessenger: Messenger
    private var isForeground: Boolean = false
    private var count: Int = 1
    /*
        From Jared's reply to https://stackoverflow.com/a/25821942:
        "Also please keep in mind if you want it to update pretty and not erase it and re-create
        (which will make your notification get erased and re-pop up somewhere else in the list),
        you NEED to use builder.setOnlyAlertOnce(true) as well as you MUST use the same builder,
        otherwise it wont update, it will do the goofy recreation thing."
     */
    private val notificationBuilder: Builder = Builder(this, TIMER_SERVICE_NOTIFICATION_CHANNEL_ID) // NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    inner class IncomingHandler(context: Context, private val applicationContext: Context = context.applicationContext) : Handler() {
        override fun handleMessage(msg: Message) {
            println("handleMessage called ${msg.what}")
            when (msg.what) {
                MSG_START_TIMER ->{
                    startTimer()
                }
                MSG_STOP_TIMER -> {
                    stopTimer()
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        println("onBind called")
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }

    private fun startTimer() {
        if(!isForeground){
            isForeground = true
            enterForeground()
        }else{
            count++
            updateNotification()
        }
    }
    private fun enterForeground(){
        startForeground(TIMER_SERVICE_NOTIFICATION_ID, createNotification())
    }
    private fun exitForeground(removeNotificationBehaviour: Int = STOP_FOREGROUND_REMOVE){
        stopForeground(removeNotificationBehaviour)
    }
    private fun stopTimer() {
        if(isForeground){
            exitForeground()
            isForeground = false
        }
    }
    override fun onCreate()
    {
        super.onCreate()
        notificationManager = getSystemService( NotificationManager::class.java )
        val channel = NotificationChannel(TIMER_SERVICE_NOTIFICATION_CHANNEL_ID, TIMER_SERVICE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return notificationBuilder
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText("testo testo testo testo testo $count")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOnlyAlertOnce(true)
            .build()
    }
    private fun updateNotification(){
        notificationManager.notify(TIMER_SERVICE_NOTIFICATION_ID, createNotification())
    }
    companion object{
        const val TIMER_SERVICE_NOTIFICATION_CHANNEL_ID = "PomodoroTimer"
        const val TIMER_SERVICE_NOTIFICATION_CHANNEL_NAME = "Pomodoro Timer notification channel"
        const val TIMER_SERVICE_NOTIFICATION_ID = 1
        const val MSG_START_TIMER = 1
        const val MSG_STOP_TIMER = 2
    }
}