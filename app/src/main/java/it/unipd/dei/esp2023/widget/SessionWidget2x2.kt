package it.unipd.dei.esp2023.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import it.unipd.dei.esp2023.R

class SessionWidget2x2 : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    )
    {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.session_widget2x2)
            views.setRemoteAdapter(R.id.SessionWidget2x2ID_List, Intent(context, ListWidgetService::class.java))
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Toast.makeText(context!!, "Thanks for using our widget", Toast.LENGTH_LONG).show()
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_title)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.session_widget2x2)
    views.setTextViewText(R.id.appwidget_title, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}