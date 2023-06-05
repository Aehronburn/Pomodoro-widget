package it.unipd.dei.esp2023.statistics_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.content_providers.StatisticsContentProvider

class StatisticsAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->

            val contentResolver = context.contentResolver
            val todayStats = contentResolver.query(
                Uri.parse("content://" + StatisticsContentProvider.AUTHORITY + "/" + StatisticsContentProvider.TODAY_PATH),
                null, null, null, null)

            println("today stats " + todayStats?.columnNames?.component1() + " " + todayStats?.columnNames?.component2())
            todayStats?.close()

            val views = RemoteViews(context.packageName, R.layout.statistics_widget)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

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
        println("SIZES " + newOptions.getParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java))
        println(" ")

    }
}