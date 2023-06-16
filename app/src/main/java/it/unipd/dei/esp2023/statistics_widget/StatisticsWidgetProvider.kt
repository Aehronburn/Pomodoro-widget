package it.unipd.dei.esp2023.statistics_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.SizeF
import android.widget.RemoteViews
import androidx.navigation.NavDeepLinkBuilder
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.content_providers.StatisticsContentProvider
import it.unipd.dei.esp2023.control_widget.ControlWidgetProvider
import it.unipd.dei.esp2023.statistics.StatisticsFragment
import it.unipd.dei.esp2023.statistics.StatisticsViewModel
import com.google.assistant.appactions.widgets.AppActionsWidgetExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class StatisticsWidgetProvider : AppWidgetProvider() {

    // Info on Google Assistant support can be found in shortcuts.xml
    private fun setTextToSpeech(
        context: Context, widgetId: Int
    ) {
        val appActionsWidgetExtension: AppActionsWidgetExtension =
            AppActionsWidgetExtension.newBuilder(AppWidgetManager.getInstance(context))
                .setResponseSpeech(context.getString(R.string.stats_widget_assistant_speech))
                .setResponseText(context.getString(R.string.stats_widget_assistant_text))
                .build()
        appActionsWidgetExtension.updateWidget(widgetId)
    }
    override fun onReceive(context: Context?, intent: Intent) {
        if((intent.action == Intent.ACTION_DATE_CHANGED || intent.action == Intent.ACTION_TIME_CHANGED) ||
            intent.getStringExtra(WIDGET_TYPE) == WIDGET_TYPE_STATS) {
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context!!, StatisticsWidgetProvider::class.java))
            onUpdate(context, AppWidgetManager.getInstance(context), ids)
        } else {
            /*
            if the update was referring to ControlWidgetProvider do nothing
             */
            if(intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE &&
                intent.getStringExtra(ControlWidgetProvider.WIDGET_TYPE) == ControlWidgetProvider.WIDGET_TYPE_CONTROL)
                return
            super.onReceive(context, intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            runBlocking {
                updateSingleWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private suspend fun updateSingleWidget(context: Context,
                                           appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int){
        withContext(Dispatchers.IO){
            launch {
                val isAssistantWidget = !appWidgetManager.getAppWidgetOptions(appWidgetId).getString(AppActionsWidgetExtension.EXTRA_APP_ACTIONS_BII).isNullOrBlank()
                if(isAssistantWidget){
                    setTextToSpeech(context, appWidgetId)
                }

                val contentResolver = context.contentResolver

                val pendingIntent = NavDeepLinkBuilder(context).setGraph(R.navigation.navigation_graph)
                    .setDestination(R.id.sessions_fragment).createPendingIntent()

                //region retrieving statistics from StatisticsContentProvider
                /*
                today stats
                 */
                val todayStats = contentResolver.query(
                    Uri.parse("content://" + StatisticsContentProvider.AUTHORITY + "/" + StatisticsContentProvider.TODAY_PATH),
                    null, null, null, null
                )
                todayStats!!.moveToNext()
                val todayCompleted = todayStats.getInt(2)
                val todayFocusTime = todayStats.getInt(3)
                todayStats.close()
                val (todayFocusHours, todayFocusMinutes) = StatisticsViewModel.timeToHhMm(todayFocusTime)
                val productivityImage = StatisticsFragment.getProductivityImage(todayCompleted)

                /*
                week stats
                 */
                var weekCompleted = 0
                var weekFocusTime = 0

                val weekStats = contentResolver.query(
                    Uri.parse("content://" + StatisticsContentProvider.AUTHORITY + "/" + StatisticsContentProvider.WEEK_PATH),
                    null, null, null, null
                )
                while (weekStats!!.moveToNext()) {
                    weekCompleted += weekStats.getInt(2)
                    weekFocusTime += weekStats.getInt(3)
                }
                weekStats.close()
                val (weekFocusHours, weekFocusMinutes) = StatisticsViewModel.timeToHhMm(weekFocusTime)

                /*
                month stats
                 */
                var monthCompleted = 0
                var monthFocusTime = 0

                val monthStats = contentResolver.query(
                    Uri.parse("content://" + StatisticsContentProvider.AUTHORITY + "/" + StatisticsContentProvider.MONTH_PATH),
                    null, null, null, null
                )
                while (monthStats!!.moveToNext()) {
                    monthCompleted += monthStats.getInt(2)
                    monthFocusTime += monthStats.getInt(3)
                }
                monthStats.close()
                val (monthFocusHours, monthFocusMinutes) = StatisticsViewModel.timeToHhMm(monthFocusTime)
                //endregion

                //region remoteViews
                val smallView =
                    RemoteViews(context.packageName, R.layout.statistics_widget_small).apply {
                        setTextViewText(R.id.today_completed_text_widget, todayCompleted.toString())
                        setTextViewText(
                            R.id.today_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                todayFocusHours,
                                todayFocusMinutes
                            )
                        )
                        setOnClickPendingIntent(R.id.statistics_widget_container, pendingIntent)
                    }

                val mediumView =
                    RemoteViews(context.packageName, R.layout.statistics_widget_medium).apply {
                        setTextViewText(R.id.today_completed_text_widget, todayCompleted.toString())
                        setTextViewText(
                            R.id.today_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                todayFocusHours,
                                todayFocusMinutes
                            )
                        )

                        setTextViewText(R.id.week_completed_text_widget, weekCompleted.toString())
                        setTextViewText(
                            R.id.week_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                weekFocusHours,
                                weekFocusMinutes
                            )
                        )
                        setOnClickPendingIntent(R.id.statistics_widget_container, pendingIntent)
                    }

                val largeView =
                    RemoteViews(context.packageName, R.layout.statistics_widget_large).apply {
                        setTextViewText(R.id.today_completed_text_widget, todayCompleted.toString())
                        setTextViewText(
                            R.id.today_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                todayFocusHours,
                                todayFocusMinutes
                            )
                        )

                        setTextViewText(R.id.week_completed_text_widget, weekCompleted.toString())
                        setTextViewText(
                            R.id.week_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                weekFocusHours,
                                weekFocusMinutes
                            )
                        )

                        setTextViewText(R.id.month_completed_text_widget, monthCompleted.toString())
                        setTextViewText(
                            R.id.month_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                monthFocusHours,
                                monthFocusMinutes
                            )
                        )
                        setOnClickPendingIntent(R.id.statistics_widget_container, pendingIntent)
                    }

                val extraLargeView =
                    RemoteViews(context.packageName, R.layout.statistics_widget_extralarge).apply {
                        setTextViewText(R.id.today_completed_text_widget, todayCompleted.toString())
                        setTextViewText(
                            R.id.today_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                todayFocusHours,
                                todayFocusMinutes
                            )
                        )

                        setTextViewText(R.id.week_completed_text_widget, weekCompleted.toString())
                        setTextViewText(
                            R.id.week_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                weekFocusHours,
                                weekFocusMinutes
                            )
                        )

                        setTextViewText(R.id.month_completed_text_widget, monthCompleted.toString())
                        setTextViewText(
                            R.id.month_focus_time_text_widget,
                            context.getString(
                                R.string.stats_widget_focus_time,
                                monthFocusHours,
                                monthFocusMinutes
                            )
                        )
                        setImageViewResource(R.id.productivity_image_widget, productivityImage)
                        setOnClickPendingIntent(R.id.statistics_widget_container, pendingIntent)
                    }
                //endregion

                /*
                maximum widths and heights allowed for that view
                 */
                val viewMapping: Map<SizeF, RemoteViews> = mapOf(
                    SizeF(80f, 100f) to smallView,
                    SizeF(80f, 220f) to mediumView,
                    SizeF(80f, 340f) to largeView,
                    SizeF(80f, 460f) to extraLargeView
                )
                val remoteViews = RemoteViews(viewMapping)

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            }
        }
    }

    companion object {
        const val WIDGET_TYPE = "WIDGET_TYPE"
        const val WIDGET_TYPE_STATS = "WIDGET_TYPE_STATS"
    }
}