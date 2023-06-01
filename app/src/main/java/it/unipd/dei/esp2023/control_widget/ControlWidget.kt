package it.unipd.dei.esp2023.control_widget

import it.unipd.dei.esp2023.R
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews


class ControlWidget(): AppWidgetProvider() {

    /*
    * Called in response to the AppWidgetManager#ACTION_APPWIDGET_UPDATE and AppWidgetManager#ACTION_APPWIDGET_RESTORED
    * broadcasts when this AppWidget provider is being asked to provide RemoteViews for a set of AppWidgets. Override
    * this method to implement your own AppWidget functionality.
    *
    * context	Context: The Context in which this receiver is running
    *
    * appWidgetManager	AppWidgetManager: A AppWidgetManager object you can call AppWidgetManager.updateAppWidget(ComponentName, RemoteViews) on
    *
    * appWidgetIds	int: The appWidgetIds for which an update is needed. Note that this may be all of the AppWidget instances for this provider, or just a subset of them.
    *
    * */
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if(context == null || appWidgetManager == null || appWidgetIds == null){
            return;
        }
        for (appWidgetId in appWidgetIds) {
            updateControlWidget(context, appWidgetManager, appWidgetId)
        }
    }
    private fun updateControlWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int){
        val views = RemoteViews(context.packageName, R.layout.control_widget)
        appWidgetManager.updateAppWidget(widgetId, views);
    }
}