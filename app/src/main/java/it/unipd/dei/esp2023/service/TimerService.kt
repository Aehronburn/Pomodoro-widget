package it.unipd.dei.esp2023.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment
import it.unipd.dei.esp2023.settings.SettingsFragment

class TimerService : Service() {

    private lateinit var mMessenger: Messenger
    private var isForeground: Boolean = false
    private var currentTimer: CountDownTimer? = null
    private var msUntilFinish: Long = 0
    private var timerType: Int = 0
    private var remainingTimerMs: Long = 0
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
            /*
            * what: Action type
            * arg1: Timer type (pomodoro, short break, long break, if 0 defaults to pomodoro)
            * arg2: Timer duration in ms (if 0 defaults to the correct value in SettingsFragment, eg DEFAULT_POMODORO_DURATION*ONE_MINUTE_IN_MS for arg1 = pomodoro)
            *
            * See acceptable Action and Timer types in companion object
            */

            // Passo sempre msg a tutti i metodi per uniformare le interfacce, poi lì capisco se usarlo o meno
            // La descrizione di ogni azione si trova nel metodo handleNomeAzione corrispondente
            when (msg.what) {
                ACTION_CREATE_TIMER->{
                    handleCreateTimer(msg)
                }
                ACTION_START_TIMER ->{
                    handleStartTimer(msg)
                }
                ACTION_PAUSE_TIMER->{
                    handlePauseTimer(msg)
                }
                ACTION_DELETE_TIMER -> {
                    handleDeleteTimer(msg)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }
    private fun enterForeground(){
        startForeground(TIMER_SERVICE_NOTIFICATION_ID, createNotification())
    }
    private fun exitForeground(removeNotificationBehaviour: Int = STOP_FOREGROUND_REMOVE){
        stopForeground(removeNotificationBehaviour)
    }
    override fun onCreate()
    {
        super.onCreate()
        notificationManager = getSystemService( NotificationManager::class.java )
        val channel = NotificationChannel(TIMER_SERVICE_NOTIFICATION_CHANNEL_ID, TIMER_SERVICE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
    private fun getTimerTypeString(): String{
        return when(timerType){
            TIMER_TYPE_SHORT_BREAK -> getString(R.string.short_break_duration_title)
            TIMER_TYPE_LONG_BREAK -> getString(R.string.long_break_duration_title)
            else -> getString(R.string.pomodoro_duration_title)
        }
    }
    private fun createNotification(): Notification {
        return notificationBuilder
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_text_progress, getTimerTypeString(), remainingTimerMs/ONE_MINUTE_IN_MS))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            ) // TODO aprire nel fragment del timer invece che nell'homepage dell'app
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOnlyAlertOnce(true)
            .build()
    }
    private fun updateNotification(){
        notificationManager.notify(TIMER_SERVICE_NOTIFICATION_ID, createNotification())
    }
    private fun cancelTimer(){
        currentTimer?.cancel()
        currentTimer = null
    }
    private fun startTimer(){ // TODO inviare timer progress iniziale
        currentTimer?.start()
    }
    private fun onTickCountDownTimer(millisUntilFinished: Long){
        remainingTimerMs = millisUntilFinished
        updateNotification()
    }
    private fun onFinishCountDownTimer(){
    }
    private fun createNewTimer(msDuration: Long, msInterval: Long): CountDownTimer{
        return object: CountDownTimer(msDuration,msInterval) {
            override fun onTick(millisUntilFinished: Long) = onTickCountDownTimer(millisUntilFinished)
            override fun onFinish() = onFinishCountDownTimer()
        }
    }
    private fun handleCreateTimer(msg: Message){
        if(isForeground){
            exitForeground()
        }
        cancelTimer()
        // todo azzerare campi pausa
        val timerDuration: Long = if(msg.arg2!=0) msg.arg2.toLong() else DEFAULT_DURATION_FOR_TIMER_TYPE(msg.arg1)
        currentTimer = createNewTimer(timerDuration, TIMER_TICK_DURATION)
        remainingTimerMs = timerDuration
        timerType = msg.arg1
        enterForeground()
        startTimer()
    }
    private fun handleStartTimer(msg: Message) {
    }
    private fun handlePauseTimer(msg: Message){}
    private fun handleDeleteTimer(msg: Message) {
        currentTimer?.cancel()
        currentTimer = null
        if(isForeground){
            exitForeground()
            isForeground = false
        }
    }
    companion object{
        const val TIMER_SERVICE_NOTIFICATION_CHANNEL_ID = "PomodoroTimer"
        const val TIMER_SERVICE_NOTIFICATION_CHANNEL_NAME = "Pomodoro Timer notification channel"

        const val TIMER_SERVICE_NOTIFICATION_ID = 1

        const val ACTION_CREATE_TIMER = 1
        const val ACTION_START_TIMER = 2
        const val ACTION_PAUSE_TIMER = 3
        const val ACTION_DELETE_TIMER = 4

        const val TIMER_TICK_DURATION: Long = 1000

        const val ONE_MINUTE_IN_MS: Long = 1000*60

        const val TIMER_TYPE_POMODORO = 0
        const val TIMER_TYPE_SHORT_BREAK = 1
        const val TIMER_TYPE_LONG_BREAK = 1
        fun DEFAULT_DURATION_FOR_TIMER_TYPE(timer_type: Int): Long{
            return when(timer_type){
                TIMER_TYPE_SHORT_BREAK -> SettingsFragment.DEFAULT_SHORT_BREAK_DURATION
                TIMER_TYPE_LONG_BREAK -> SettingsFragment.DEFAULT_LONG_BREAK_DURATION
                else -> SettingsFragment.DEFAULT_POMODORO_DURATION
            } * ONE_MINUTE_IN_MS
        }
    }
}