package it.unipd.dei.esp2023.statistics_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.Uri
import android.widget.RemoteViews
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.content_providers.StatisticsContentProvider
import it.unipd.dei.esp2023.statistics.StatisticsViewModel

class StatisticsAppWidgetProvider : AppWidgetProvider() {

    private var todayCompleted = 0
    private var todayFocusTime = 0

    private var weekCompleted = 0
    private var weekFocusTime = 0

    private var monthCompleted = 0
    private var monthFocusTime = 0

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->

            val contentResolver = context.contentResolver

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

            val views = RemoteViews(context.packageName, R.layout.statistics_widget).apply {
                setTextViewText(R.id.today_completed_text_widget, todayCompleted.toString())
                setTextViewText(R.id.today_focus_time_text_widget, context.getString(R.string.stats_widget_focus_time, todayFocusHours, todayFocusMinutes))

                setTextViewText(R.id.week_completed_text_widget, weekCompleted.toString())
                setTextViewText(R.id.week_focus_time_text_widget, context.getString(R.string.stats_widget_focus_time, weekFocusHours, weekFocusMinutes))

                setTextViewText(R.id.month_completed_text_widget, monthCompleted.toString())
                setTextViewText(R.id.month_focus_time_text_widget, context.getString(R.string.stats_widget_focus_time, monthFocusHours, monthFocusMinutes))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
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