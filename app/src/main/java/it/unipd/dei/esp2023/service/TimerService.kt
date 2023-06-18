package it.unipd.dei.esp2023.service

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.control_widget.ControlWidgetProvider
import it.unipd.dei.esp2023.settings.SettingsFragment
import it.unipd.dei.esp2023.statistics_widget.StatisticsWidgetProvider

class TimerService : Service() {

    private lateinit var mMessenger: Messenger
    private var isCompleted: Boolean = false
    private var isForeground: Boolean = false
    private var isPaused: Boolean = false
    private var currentTimer: CountDownTimer? = null
    private var lastInitialDuration: Long = DEFAULT_DURATION_FOR_TIMER_TYPE(TIMER_TYPE_POMODORO)
    private var timerType: Int = 0
    private var remainingTimerMs: Long = 0
    private var boundFragmentMessenger: Messenger? = null

    /*
        From Jared's reply to https://stackoverflow.com/a/25821942:
        "Also please keep in mind if you want it to update pretty and not erase it and re-create
        (which will make your notification get erased and re-pop up somewhere else in the list),
        you NEED to use builder.setOnlyAlertOnce(true) as well as you MUST use the same builder,
        otherwise it wont update, it will do the goofy recreation thing."
     */
    private val notificationBuilder: Builder =
        Builder(this, TIMER_SERVICE_NOTIFICATION_CHANNEL_ID) // NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    inner class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            /*
            * what: Action type
            * arg1: Timer type (pomodoro, short break, long break, if 0 defaults to pomodoro)
            * arg2: Timer duration in ms (if 0 defaults to the correct value in SettingsFragment, eg DEFAULT_POMODORO_DURATION*ONE_MINUTE_IN_MS for arg1 = pomodoro)
            *
            * See acceptable Action and Timer types in companion object
            *
            * The duration parameter is passed as Int via arg2 and not as Long in a Bundle because Int.MAX_VALUE ms (more than 24 days) is
            * deemed by the author to be more than enough for a maximum timer duration for the scope of this app. If a focus time of 25+
            * days straight is needed on a single pomodoro, consider taking at least a short break after the 24th day :)
            */

            // Passo sempre msg a tutti i metodi per uniformare le interfacce, poi lì capisco se usarlo o meno
            // La descrizione di ogni azione si trova nel metodo handleNomeAzione corrispondente
            when (msg.what) {
                ACTION_CREATE_TIMER -> {
                    handleCreateTimer(msg)
                }

                ACTION_START_TIMER -> {
                    handleStartTimer(msg)
                }

                ACTION_PAUSE_TIMER -> {
                    handlePauseTimer(msg)
                }

                ACTION_DELETE_TIMER -> {
                    handleDeleteTimer(msg)
                }

                ACTION_RESET_TIMER -> {
                    handleResetTimer(msg)
                }

                ACTION_SUBSCRIBE -> {
                    handleSubscribe(msg)
                }

                ACTION_UNSUBSCRIBE -> {
                    handleUnsubscribe(msg)
                }

                ACTION_WIDGET_INFO -> {
                    handleWidgetInfo(msg)
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    private fun enterForeground() {
        startForeground(TIMER_SERVICE_NOTIFICATION_ID, createNotification())
        isForeground = true
    }

    private fun exitForeground(removeNotificationBehaviour: Int = STOP_FOREGROUND_REMOVE) {
        stopForeground(removeNotificationBehaviour)
        isForeground = false
        isCompleted = false
    }

    private fun getTimerTypeString(): String {
        return when (timerType) {
            TIMER_TYPE_SHORT_BREAK -> getString(R.string.short_break_duration_title)
            TIMER_TYPE_LONG_BREAK -> getString(R.string.long_break_duration_title)
            else -> getString(R.string.pomodoro_duration_title)
        }
    }

    private fun createNotification(): Notification {
        return notificationBuilder
            .setContentTitle(
                if (isCompleted) getString(R.string.service_notification_title_completed) else getString(
                    R.string.service_notification_title
                )
            )
            .setContentText(
                if (isCompleted) (getString(
                    R.string.service_notification_text_progress_completed,
                    getTimerTypeString()
                )) else (getString(
                    if (isPaused) R.string.service_notification_text_progress_paused else R.string.service_notification_text_progress_running,
                    getTimerTypeString(),
                    remainingTimerMs/ONE_MINUTE_IN_MS,
                    (remainingTimerMs%ONE_MINUTE_IN_MS)/1000
                ))
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOnlyAlertOnce(!isCompleted)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(TIMER_SERVICE_NOTIFICATION_ID, createNotification())
    }

    private fun sendProgress(status: Int, progress: Int = 0) {
        boundFragmentMessenger?.send(Message.obtain(null, status, progress, 0))
        sendWidgetUpdate(status, progress)
    }

    private fun sendWidgetUpdate(status: Int, progress: Int) {
        /*
        update control widgets
         */
        val brIntent = Intent()
        brIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        // https://stackoverflow.com/a/6446264
        brIntent.putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_IDS,
            AppWidgetManager.getInstance(this).getAppWidgetIds(
                ComponentName(
                    this.packageName,
                    ControlWidgetProvider::class.java.name
                )
            )
        )
        brIntent.putExtra(
            ControlWidgetProvider.EXTRAS_KEY_STATUS, when (status) {
                PROGRESS_STATUS_RUNNING -> {
                    ControlWidgetProvider.CURRENT_STATUS_RUNNING
                }

                PROGRESS_STATUS_PAUSED -> {
                    ControlWidgetProvider.CURRENT_STATUS_PAUSED
                }

                PROGRESS_STATUS_DELETED -> {
                    ControlWidgetProvider.CURRENT_STATUS_IDLE
                }

                PROGRESS_STATUS_COMPLETED -> {
                    ControlWidgetProvider.CURRENT_STATUS_IDLE
                }

                INITIAL_STATUS_IDLE -> {
                    ControlWidgetProvider.CURRENT_STATUS_IDLE
                }

                INITIAL_STATUS_RUNNING -> {
                    ControlWidgetProvider.CURRENT_STATUS_RUNNING
                }

                INITIAL_STATUS_PAUSED -> {
                    ControlWidgetProvider.CURRENT_STATUS_PAUSED
                }

                else -> {
                    ControlWidgetProvider.CURRENT_STATUS_IDLE
                }
            }
        )
        brIntent.putExtra(ControlWidgetProvider.EXTRAS_KEY_MS, remainingTimerMs.toInt())
        brIntent.putExtra(ControlWidgetProvider.EXTRAS_KEY_TYPE, timerType)
        brIntent.putExtra(
            ControlWidgetProvider.WIDGET_TYPE,
            ControlWidgetProvider.WIDGET_TYPE_CONTROL
        )
        sendBroadcast(brIntent)
    }

    private fun cancelTimer() {
        currentTimer?.cancel()
        currentTimer = null
    }

    private fun startTimer() {
        sendProgress(PROGRESS_STATUS_RUNNING, remainingTimerMs.toInt())
        currentTimer?.start()
    }

    private fun onTickCountDownTimer(millisUntilFinished: Long) {
        remainingTimerMs = millisUntilFinished
        if (remainingTimerMs > 0) {
            updateNotification()
            sendProgress(PROGRESS_STATUS_RUNNING, millisUntilFinished.toInt())
            isCompleted = false
        }
    }

    private fun onFinishCountDownTimer() {
        cancelTimer()
        /*if(isForeground){
            exitForeground()
        }*/
        isPaused = false
        isCompleted = true
        remainingTimerMs = 0
        updateNotification()
        sendProgress(PROGRESS_STATUS_COMPLETED)
        /*
        update statistics widgets
         */
        val ids = AppWidgetManager.getInstance(this)
            .getAppWidgetIds(ComponentName(this, StatisticsWidgetProvider::class.java))
        val statsUpdateIntent = Intent(this, StatisticsWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            putExtra(
                StatisticsWidgetProvider.WIDGET_TYPE,
                StatisticsWidgetProvider.WIDGET_TYPE_STATS
            )
        }
        sendBroadcast(statsUpdateIntent)
    }

    private fun createNewTimer(
        msDuration: Long,
        msInterval: Long = TIMER_TICK_DURATION
    ): CountDownTimer {
        return object : CountDownTimer(msDuration, msInterval) {
            override fun onTick(millisUntilFinished: Long) =
                onTickCountDownTimer(millisUntilFinished)

            override fun onFinish() = onFinishCountDownTimer()
        }
    }

    private fun handleWidgetInfo(ignored: Message) {
        if (!isForeground) {
            sendWidgetUpdate(INITIAL_STATUS_IDLE, 0)
            return
        }
        sendWidgetUpdate(
            if (isPaused) INITIAL_STATUS_PAUSED else INITIAL_STATUS_RUNNING,
            remainingTimerMs.toInt()
        )
    }

    private fun handleCreateTimer(msg: Message) {
        if (isForeground) {
            exitForeground()
        }
        cancelTimer()
        isPaused = false
        val timerDuration: Long =
            if (msg.arg2 != 0) msg.arg2.toLong() else DEFAULT_DURATION_FOR_TIMER_TYPE(msg.arg1)
        lastInitialDuration = timerDuration
        currentTimer = createNewTimer(timerDuration)
        remainingTimerMs = timerDuration
        timerType = msg.arg1
        enterForeground()
        startTimer()
    }

    private fun handleStartTimer(ignored: Message) {
        if (isPaused) {
            if (remainingTimerMs > 0) {
                currentTimer = createNewTimer(remainingTimerMs)
                if (!isForeground) {
                    enterForeground()
                }
                startTimer()
            } else {
                if (isForeground) {
                    exitForeground()
                }
                cancelTimer()
                sendProgress(PROGRESS_STATUS_COMPLETED)
            }
            isPaused = false
        }
        if (isForeground) {
            updateNotification()
        }
    }

    private fun handlePauseTimer(ignored: Message) {
        cancelTimer()
        if (remainingTimerMs > 0) {
            if (!isForeground) {
                enterForeground()
            }
            isPaused = true
            isCompleted = false
            updateNotification()
            sendProgress(PROGRESS_STATUS_PAUSED, remainingTimerMs.toInt())
        } else {
            /*if(isForeground){
                exitForeground()
            }*/
            isCompleted = true
            isPaused = false
            sendProgress(PROGRESS_STATUS_COMPLETED)
        }
    }

    private fun handleDeleteTimer(ignored: Message) {
        cancelTimer()
        if (isForeground) {
            exitForeground()
        }
        remainingTimerMs = 0
        isPaused = false
        isCompleted = false
        sendProgress(PROGRESS_STATUS_DELETED)
    }

    private fun handleResetTimer(msg: Message) {
        cancelTimer()
        if (!isForeground) {
            enterForeground()
        }
        isPaused = true
        isCompleted = false
        remainingTimerMs = if (msg.arg1 != 0) msg.arg1.toLong() else lastInitialDuration
        updateNotification()
        sendProgress(PROGRESS_STATUS_PAUSED, remainingTimerMs.toInt())
    }

    private fun handleSubscribe(msg: Message) {
        boundFragmentMessenger = msg.replyTo
        sendFirstUpdate(msg.replyTo)
    }

    private fun sendFirstUpdate(msgr: Messenger) {
        if (isPaused) {
            msgr.send(Message.obtain(null, INITIAL_STATUS_PAUSED, remainingTimerMs.toInt(), 0))
        } else {
            msgr.send(
                Message.obtain(
                    null,
                    if (remainingTimerMs > 0) INITIAL_STATUS_RUNNING else INITIAL_STATUS_IDLE,
                    remainingTimerMs.toInt(),
                    0
                )
            )
        }
    }

    private fun handleUnsubscribe(ignored: Message) {
        boundFragmentMessenger = null
    }

    // region lifecycle
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            TIMER_SERVICE_NOTIFICATION_CHANNEL_ID,
            TIMER_SERVICE_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        mMessenger = Messenger(IncomingHandler(this))
    }

    override fun onBind(intent: Intent): IBinder? {
        return mMessenger.binder
    }

    /*
    * Note: onUnbind is not overridden because the default implementation is fine (new clients will use onBind and not onRebind)
    * https://developer.android.com/reference/android/app/Service#onUnbind(android.content.Intent)
    */
    override fun onDestroy() {
        if (isForeground) {
            exitForeground()
        }
        cancelTimer()
        remainingTimerMs = 0
        isPaused = false
        return
    }
    // endregion

    companion object {
        const val TIMER_SERVICE_NOTIFICATION_CHANNEL_ID = "PomodoroTimer"
        const val TIMER_SERVICE_NOTIFICATION_CHANNEL_NAME =
            "Pomodoro Timer notifications" // Used in the settings page for the app (notifications settings)

        const val TIMER_SERVICE_NOTIFICATION_ID = 1

        const val ACTION_CREATE_TIMER = 1
        const val ACTION_START_TIMER = 2
        const val ACTION_PAUSE_TIMER = 3
        const val ACTION_DELETE_TIMER = 4
        const val ACTION_RESET_TIMER = 5
        const val ACTION_SUBSCRIBE = 6
        const val ACTION_UNSUBSCRIBE = 7
        const val ACTION_WIDGET_INFO = 8

        const val TIMER_TICK_DURATION: Long = 1000

        const val ONE_MINUTE_IN_MS: Long = 1000 * 60

        const val TIMER_TYPE_POMODORO = -1
        const val TIMER_TYPE_SHORT_BREAK = -2
        const val TIMER_TYPE_LONG_BREAK = -3
        fun DEFAULT_DURATION_FOR_TIMER_TYPE(timer_type: Int): Long {
            return when (timer_type) {
                TIMER_TYPE_SHORT_BREAK -> SettingsFragment.DEFAULT_SHORT_BREAK_DURATION
                TIMER_TYPE_LONG_BREAK -> SettingsFragment.DEFAULT_LONG_BREAK_DURATION
                else -> SettingsFragment.DEFAULT_POMODORO_DURATION
            } * ONE_MINUTE_IN_MS
        }

        const val PROGRESS_STATUS_RUNNING = 1
        const val PROGRESS_STATUS_PAUSED = 2
        const val PROGRESS_STATUS_DELETED = 3
        const val PROGRESS_STATUS_COMPLETED = 4

        const val INITIAL_STATUS_IDLE = 100
        const val INITIAL_STATUS_RUNNING = 101
        const val INITIAL_STATUS_PAUSED = 102
    }
}