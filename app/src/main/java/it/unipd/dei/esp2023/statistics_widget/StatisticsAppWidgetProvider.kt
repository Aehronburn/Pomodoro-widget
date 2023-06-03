package it.unipd.dei.esp2023.statistics_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import it.unipd.dei.esp2023.R

class StatisticsAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
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

        /*
        1x1
        MIN_WIDTH 80
        MAX_WIDTH 170
        MIN_HEIGHT 52
        MAX_HEIGHT 103
        SIZES [80.0x103.61905, 80.0x103.61905, 170.66667x52.57143, 170.66667x52.57143]
         */

        /*
        1x2
        MIN_WIDTH 80
        MAX_WIDTH 170
        MIN_HEIGHT 121
        MAX_HEIGHT 223
        SIZES [80.0x223.2381, 80.0x223.2381, 170.66667x121.14286, 170.66667x121.14286]
         */

        /*
        1x3
        MIN_WIDTH 80
        MAX_WIDTH 170
        MIN_HEIGHT 189
        MAX_HEIGHT 342
        SIZES [80.0x342.85715, 80.0x342.85715, 170.66667x189.71428, 170.66667x189.71428]
         */

        /*
        1x4
        MIN_WIDTH 80
        MAX_WIDTH 170
        MIN_HEIGHT 258
        MAX_HEIGHT 462
        SIZES [80.0x462.4762, 80.0x462.4762, 170.66667x258.2857, 170.66667x258.2857]
         */

        /*
        1x5
        MIN_WIDTH 80
        MAX_WIDTH 170
        MIN_HEIGHT 326
        MAX_HEIGHT 582
        SIZES [80.0x582.0952, 80.0x582.0952, 170.66667x326.85715, 170.66667x326.85715]
         */

        /*
        1x2
        MIN_WIDTH 176
        MAX_WIDTH 357
        MIN_HEIGHT 52
        MAX_HEIGHT 103
        SIZES [176.0x103.61905, 176.0x103.61905, 357.33334x52.57143, 357.33334x52.57143]
         */

        /*
        1x3
        MIN_WIDTH 272
        MAX_WIDTH 544
        MIN_HEIGHT 52
        MAX_HEIGHT 103
        SIZES [272.0x103.61905, 272.0x103.61905, 544.0x52.57143, 544.0x52.57143]
         */

        /*
        MIN_WIDTH 368
        MAX_WIDTH 730
        MIN_HEIGHT 52
        MAX_HEIGHT 103
        SIZES [368.0x103.61905, 368.0x103.61905, 730.6667x52.57143, 730.6667x52.57143]
         */
    }
}