package it.unipd.dei.esp2023.statistics_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.navigation.NavDeepLinkBuilder
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.content_providers.StatisticsContentProvider
import it.unipd.dei.esp2023.control_widget.ControlWidgetProvider
import it.unipd.dei.esp2023.statistics.StatisticsFragment
import it.unipd.dei.esp2023.statistics.StatisticsViewModel

class StatisticsAppWidgetProvider : AppWidgetProvider() {

    private var todayCompleted = 0
    private var todayFocusTime = 0

    private var weekCompleted = 0
    private var weekFocusTime = 0

    private var monthCompleted = 0
    private var monthFocusTime = 0

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        println("AAAAAAAAAA onReceive pazzo sgravato epico esagerato\n\n\n")
        if(intent?.action == Intent.ACTION_DATE_CHANGED || intent?.action == Intent.ACTION_TIME_CHANGED) {
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context!!, StatisticsAppWidgetProvider::class.java))
            onUpdate(context, AppWidgetManager.getInstance(context), ids)
        } else {
            /*
            if the update was referring to ControlWidgetProvider do nothing
             */
            if(intent != null &&
                intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE &&
                intent.getStringExtra(ControlWidgetProvider.WIDGET_TYPE) == ControlWidgetProvider.WIDGET_TYPE_CONTROL)
                return
            super.onReceive(context, intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
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
            todayCompleted = todayStats.getInt(2)
            todayFocusTime = todayStats.getInt(3)
            todayStats.close()
            val (todayFocusHours, todayFocusMinutes) = StatisticsViewModel.timeToHhMm(todayFocusTime)
            val productivityImage = StatisticsFragment.getProductivityImage(todayCompleted)

            /*
            week stats
             */
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
                SizeF(80f, 50f) to smallView,
                SizeF(80f, 120f) to mediumView,
                SizeF(80f, 225f) to largeView,
                SizeF(80f, 460f) to extraLargeView
            )

            val remoteViews = RemoteViews(viewMapping)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    /*
    Just for printing device's cell sizes
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        println("MIN_WIDTH " + newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH))
        println("MAX_WIDTH " + newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH))
        println("MIN_HEIGHT " + newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT))
        println("MAX_HEIGHT " + newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT))
        println(
            "SIZES " + newOptions.getParcelableArrayList(
                AppWidgetManager.OPTION_APPWIDGET_SIZES,
                SizeF::class.java
            )
        )
        println(" ")
    }
     */
}