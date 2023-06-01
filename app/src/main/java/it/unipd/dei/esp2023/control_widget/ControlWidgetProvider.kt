package it.unipd.dei.esp2023.control_widget

import it.unipd.dei.esp2023.R
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.RemoteViews
import it.unipd.dei.esp2023.service.TimerService


class ControlWidgetProvider(): AppWidgetProvider() {
    private var activeWidgetIds: MutableSet<Int> = mutableSetOf<Int>()
    private var replyMessenger: Messenger = Messenger(ControlWidgetProviderReplyHandler(this))
    private var sendMessenger: Messenger? = null
    private var bound = false
    private var currentStatus: Int = CURRENT_STATUS_IDLE
    private var currentTimerRemainingMs: Int = 0

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            sendMessenger = Messenger(service)
            val subscribeMsg = Message.obtain(null, TimerService.ACTION_SUBSCRIBE, 0, 0)
            subscribeMsg.replyTo = replyMessenger
            sendMessenger?.send(subscribeMsg)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // se sono qui il service è crashato. spiaze.
            // TODO rebind?
            sendMessenger = null
            bound = false
        }
    }

    override fun onEnabled(context: Context?) {
        // Bind to the service
        // If for whatever reason activeWidgetIds is already filled, update the widgets
        super.onEnabled(context)
        val intent = Intent(context, TimerService::class.java)
        context?.bindService(intent, conn, Context.BIND_AUTO_CREATE)

    }
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if(appWidgetIds != null){
            activeWidgetIds.addAll(appWidgetIds.toSet())
        }
        if(context == null || appWidgetManager == null || appWidgetIds == null){
            return
        }
        for (id in appWidgetIds) {
            updateControlWidget(context, appWidgetManager, id)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        if(appWidgetIds != null){
            activeWidgetIds.removeAll(appWidgetIds.toSet())
        }
    }
    private fun updateControlWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int){
        val views = RemoteViews(context.packageName, R.layout.control_widget)
        appWidgetManager.updateAppWidget(widgetId, views)
    }

    override fun onDisabled(context: Context?) {
        // Unbind from the service
        super.onDisabled(context)
        context?.unbindService(conn)
    }

    internal class ControlWidgetProviderReplyHandler(private val cwp: ControlWidgetProvider) : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                TimerService.PROGRESS_STATUS_RUNNING -> {
                }
                TimerService.PROGRESS_STATUS_DELETED -> {
                }
                TimerService.PROGRESS_STATUS_PAUSED -> {
                }
                TimerService.PROGRESS_STATUS_COMPLETED -> {
                }
                TimerService.INITIAL_STATUS_IDLE -> {
                    cwp.currentStatus = CURRENT_STATUS_IDLE
                    cwp.currentTimerRemainingMs = 0
                }
                TimerService.INITIAL_STATUS_RUNNING -> {
                    cwp.currentStatus = CURRENT_STATUS_RUNNING
                    cwp.currentTimerRemainingMs = msg.arg1
                }
                TimerService.INITIAL_STATUS_PAUSED -> {
                    cwp.currentStatus = CURRENT_STATUS_PAUSED
                    cwp.currentTimerRemainingMs = msg.arg1
                }
            }
        }
    }
    companion object {
        const val CURRENT_STATUS_IDLE = 0
        const val CURRENT_STATUS_RUNNING = 1
        const val CURRENT_STATUS_PAUSED = 2
    }
}