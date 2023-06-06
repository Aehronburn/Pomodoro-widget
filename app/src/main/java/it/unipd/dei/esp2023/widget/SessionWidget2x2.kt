package it.unipd.dei.esp2023.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import it.unipd.dei.esp2023.R

class SessionWidget2x2 : AppWidgetProvider() {
    companion object{
        lateinit var intent: Intent
    }
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

            /*
            Here I specify the general template for the clicking action intent
            Inside the MyRemoteViewsFactory I will particularize it for the specific text clicked
             */
            intent = Intent(context, SessionWidget2x2::class.java)
            intent.action = "clicking_item"
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)

            views.setPendingIntentTemplate(R.id.SessionWidget2x2ID_List, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Toast.makeText(context!!, "Thanks for using our widget", Toast.LENGTH_LONG).show()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("my_debug", "Received! ${intent.action}")

        if(intent?.action == "clicking_item") {
            val name = intent.getStringExtra("name")
            Log.d("my_debug", "with name $name")
        }
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

