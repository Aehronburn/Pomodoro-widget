package it.unipd.dei.esp2023.control_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.RemoteViews
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.service.TimerService


class ControlWidgetProvider(): AppWidgetProvider() {
    private var status: Int = CURRENT_STATUS_MISSING
    private var remainingMs: Int = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        android.util.Log.d("WIDGET", "onReceive")
        if(INTENT_ACTION_PAUSE == intent?.action){
            val binder: IBinder? = peekService(context, Intent(context, TimerService::class.java))
            if(binder!=null){
                Messenger(binder).send(Message.obtain(null, TimerService.ACTION_PAUSE_TIMER, 0, 0))
            }
        }
        if(INTENT_ACTION_PLAY == intent?.action){
            val binder: IBinder? = peekService(context, Intent(context, TimerService::class.java))
            if(binder!=null){
                Messenger(binder).send(Message.obtain(null, TimerService.ACTION_START_TIMER, 0, 0))
            }
        }
        if(INTENT_ACTION_RESET == intent?.action){
            val binder: IBinder? = peekService(context, Intent(context, TimerService::class.java))
            if(binder!=null){
                Messenger(binder).send(Message.obtain(null, TimerService.ACTION_RESET_TIMER, 0, 0))
            }
        }
        // https://stackoverflow.com/a/61129553
        if(intent?.extras != null){
            android.util.Log.d("WIDGET", "extras")
            status = intent.extras!!.getInt(EXTRAS_KEY_STATUS, CURRENT_STATUS_MISSING)
            remainingMs = intent.extras!!.getInt(EXTRAS_KEY_MS, 0)
        }
        android.util.Log.d("WIDGET", "after extras: $status $remainingMs")
        super.onReceive(context, intent)
    }
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        android.util.Log.d("WIDGET", "onUpdate")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if(status == CURRENT_STATUS_MISSING){
            android.util.Log.d("WIDGET", "CURRENT_STATUS_MISSING")
            if(context!=null){
                val binder: IBinder? = peekService(context, Intent(context, TimerService::class.java))
                if(binder!=null){
                    Messenger(binder).send(Message.obtain(null, TimerService.ACTION_WIDGET_INFO, 0, 0))
                }else{
                    /* Se peekService ritorna null vuol dire che il service non è bound a niente (quindi sicuramente non al fragment del timer).
                    *  Questo può avvenire solo quando lo stato del timer è 'IDLE', quindi lo imposto a mano
                    */
                    status = CURRENT_STATUS_IDLE
                }
            }else{
                // per qualche motivo non ho un context e non ho potuto richiedere info, metto lo status a Idle
                status = CURRENT_STATUS_IDLE
            }
        }
        if(context == null || appWidgetManager == null || appWidgetIds == null){
            return
        }
        for (id in appWidgetIds) {
            updateControlWidget(context, appWidgetManager, id)
        }
    }
    private fun updateControlWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int){
        if(status == CURRENT_STATUS_MISSING){
            /*
            * Ho già mandato richiesta di info, per il momento non faccio niente
            * Tutti i widget che hanno già dentro qualcosa restano riempiti,
            * quelli che richiedono update (appena aggiunti) aspettano.
            * */
            return
        }
        if(status == CURRENT_STATUS_IDLE){
            val views = RemoteViews(context.packageName, R.layout.control_widget_idle)
            appWidgetManager.updateAppWidget(widgetId, views)
            return
        }
        val views = RemoteViews(context.packageName, R.layout.control_widget)
        // region single widget construction logic

        // https://stackoverflow.com/a/14798107
        views.setOnClickPendingIntent(R.id.resetBtn, getResetIntent(context))
        views.setOnClickPendingIntent(R.id.controlBtn, when(status){
            CURRENT_STATUS_PAUSED -> getPlayIntent(context)
            CURRENT_STATUS_RUNNING -> getPauseIntent(context)
            else -> null
        })
        views.setTextViewText(R.id.timeTv, when(status){
            CURRENT_STATUS_IDLE -> "IDLE"
            CURRENT_STATUS_RUNNING -> "RUNNING"
            CURRENT_STATUS_PAUSED -> "PAUSED"
            else -> "pippo"
        } +" ${remainingMs/1000}")
        // endregion
        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun getPauseIntent(ctx: Context): PendingIntent{
        return getPendingIntentFromActionString(ctx, INTENT_ACTION_PAUSE)
    }
    private fun getResetIntent(ctx: Context): PendingIntent{
        return getPendingIntentFromActionString(ctx, INTENT_ACTION_RESET)
    }
    private fun getPlayIntent(ctx: Context): PendingIntent{
        return getPendingIntentFromActionString(ctx, INTENT_ACTION_PLAY)
    }
    private fun getPendingIntentFromActionString(ctx: Context, action: String): PendingIntent{
        val intent = Intent(ctx, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    companion object {
        const val CURRENT_STATUS_MISSING = -1
        const val CURRENT_STATUS_IDLE = 0
        const val CURRENT_STATUS_RUNNING = 1
        const val CURRENT_STATUS_PAUSED = 2

        const val EXTRAS_KEY_STATUS = "STATUS"
        const val EXTRAS_KEY_MS = "MS"

        const val INTENT_ACTION_PAUSE = "PAUSE_TIMER"
        const val INTENT_ACTION_PLAY = "PLAY_TIMER"
        const val INTENT_ACTION_RESET = "RESET_TIMER"
    }


    /*
        private fun updateControlWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int){
        val views = RemoteViews(context.packageName, R.layout.control_widget)
        // region single widget construction logic

        views.setTextViewText(R.id.timeTv, currentTimerRemainingMs.toString())
        // endregion
        appWidgetManager.updateAppWidget(widgetId, views)
    }
    fun triggerUpdate(){
        // https://stackoverflow.com/questions/3570146/is-it-possible-to-throw-an-intent-for-appwidget-update-programmatically
        println("chiamata triggerUpdate() ${ctx==null}")
        val brIntent = Intent()
        brIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        println("aa "+activeWidgetIds.toIntArray().joinToString(" "))
        brIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(ctx!!).getAppWidgetIds( ComponentName(ctx!!.packageName, ControlWidgetProvider::class.java.name)));
        println(AppWidgetManager.getInstance(ctx!!).getAppWidgetIds( ComponentName(ctx!!.packageName, ControlWidgetProvider::class.java.name)).joinToString ( " " ))
        ctx!!.sendBroadcast(brIntent)
    }
    companion object {
        const val CURRENT_STATUS_IDLE = 0
        const val CURRENT_STATUS_RUNNING = 1
        const val CURRENT_STATUS_PAUSED = 2
    }*/
}