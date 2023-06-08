package it.unipd.dei.esp2023.control_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.SizeF
import android.widget.RemoteViews
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.service.TimerService
import kotlin.math.roundToInt


class ControlWidgetProvider(): AppWidgetProvider() {
    private var status: Int = CURRENT_STATUS_MISSING
    private var remainingMs: Int = 0
    private var timerType: Int = TimerService.TIMER_TYPE_POMODORO

    override fun onReceive(context: Context?, intent: Intent?) {
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
            status = intent.extras!!.getInt(EXTRAS_KEY_STATUS, CURRENT_STATUS_MISSING)
            remainingMs = intent.extras!!.getInt(EXTRAS_KEY_MS, 0)
            timerType = intent.extras!!.getInt(EXTRAS_KEY_TYPE, TimerService.TIMER_TYPE_POMODORO)
        }
        super.onReceive(context, intent)
    }
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if(status == CURRENT_STATUS_MISSING){
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
            views.setOnClickPendingIntent(
                R.id.idleControlWidgetLayoutRoot,
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            )
            appWidgetManager.updateAppWidget(widgetId, views)
            return
        }
        val largeViews = RemoteViews(context.packageName, R.layout.control_widget_large)
        val mediumViews = RemoteViews(context.packageName, R.layout.control_widget_medium)
        val smallViews = RemoteViews(context.packageName, R.layout.control_widget_small)
        val tinyViews = RemoteViews(context.packageName, R.layout.control_widget_tiny)


        // region single widget construction logic

        // https://stackoverflow.com/a/14798107
        largeViews.setOnClickPendingIntent(R.id.resetBtn, getResetIntent(context))
        mediumViews.setOnClickPendingIntent(R.id.resetBtn, getResetIntent(context))

        largeViews.setOnClickPendingIntent(R.id.controlBtn, when(status){
            CURRENT_STATUS_PAUSED -> getPlayIntent(context)
            CURRENT_STATUS_RUNNING -> getPauseIntent(context)
            else -> null
        })
        mediumViews.setOnClickPendingIntent(R.id.controlBtn, when(status){
            CURRENT_STATUS_PAUSED -> getPlayIntent(context)
            CURRENT_STATUS_RUNNING -> getPauseIntent(context)
            else -> null
        })

        val controlBtnResource = when(status){
            CURRENT_STATUS_PAUSED -> R.drawable.play_arrow_fill1_wght400_grad0_opsz48
            CURRENT_STATUS_RUNNING -> R.drawable.pause_fill1_wght400_grad0_opsz48
            else -> 0
        }
        // https://stackoverflow.com/a/3625940
        largeViews.setInt(R.id.controlBtn, "setImageResource", controlBtnResource)
        mediumViews.setInt(R.id.controlBtn, "setImageResource", controlBtnResource)
        smallViews.setInt(R.id.controlBtn, "setImageResource", controlBtnResource)

        largeViews.setInt(R.id.bgCircle, "setImageResource", when(timerType){
            TimerService.TIMER_TYPE_SHORT_BREAK -> R.drawable.circle_stroke_large_short
            TimerService.TIMER_TYPE_LONG_BREAK -> R.drawable.circle_stroke_large_long
            else -> R.drawable.circle_stroke_large_pom
        })
        mediumViews.setInt(R.id.bgCircle, "setImageResource", when(timerType){
            TimerService.TIMER_TYPE_SHORT_BREAK -> R.drawable.circle_stroke_medium_short
            TimerService.TIMER_TYPE_LONG_BREAK -> R.drawable.circle_stroke_medium_long
            else -> R.drawable.circle_stroke_medium_pom
        })
        smallViews.setInt(R.id.bgCircle, "setImageResource", when(timerType){
            TimerService.TIMER_TYPE_SHORT_BREAK -> R.drawable.circle_stroke_small_short
            TimerService.TIMER_TYPE_LONG_BREAK -> R.drawable.circle_stroke_small_long
            else -> R.drawable.circle_stroke_small_pom
        })
        val totSec: Int = (remainingMs / 1000.0).roundToInt()
        val sec: Int = totSec % 60
        val min: Int = totSec / 60
        largeViews.setTextViewText(R.id.minTv, min.toString().padStart(2, '0'))
        largeViews.setTextViewText(R.id.secTv, sec.toString().padStart(2, '0'))

        mediumViews.setTextViewText(R.id.minTv, min.toString().padStart(2, '0'))
        mediumViews.setTextViewText(R.id.secTv, sec.toString().padStart(2, '0'))

        smallViews.setTextViewText(R.id.minTv, min.toString().padStart(2, '0'))
        smallViews.setTextViewText(R.id.secTv, sec.toString().padStart(2, '0'))

        tinyViews.setTextViewText(R.id.tv, "${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}")

        val openIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        largeViews.setOnClickPendingIntent(R.id.controlWidgetLayoutRoot, openIntent)
        mediumViews.setOnClickPendingIntent(R.id.controlWidgetLayoutRoot, openIntent)
        smallViews.setOnClickPendingIntent(R.id.controlWidgetLayoutRoot, openIntent)
        tinyViews.setOnClickPendingIntent(R.id.controlWidgetLayoutRoot, openIntent)

        val layoutMap = RemoteViews(mapOf(
            SizeF(60f, 60f) to tinyViews,
            SizeF(100f, 100f) to smallViews,
            SizeF(170f, 170f) to mediumViews,
            SizeF(270f, 270f) to largeViews
        ))

        // endregion
        appWidgetManager.updateAppWidget(widgetId, layoutMap)
    }
    /*private fun isUsingNightModeResources(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }*/

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
        const val EXTRAS_KEY_TYPE = "TYPE"

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