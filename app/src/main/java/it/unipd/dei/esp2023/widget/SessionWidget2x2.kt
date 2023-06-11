package it.unipd.dei.esp2023.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.navigation.NavDeepLinkBuilder
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment

class SessionWidget2x2 : AppWidgetProvider() {
    companion object{
        lateinit var intent: Intent
        var id: Int = 0
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    )
    {
        id = appWidgetIds[0]
        //Log.d("my_debug", "Il mio widget id è $id")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.session_widget2x2)
            //Remove all items in case app has been forced closed
            remoteViews.removeAllViews(R.id.SessionWidget2x2ID_List)

            remoteViews.setRemoteAdapter(R.id.SessionWidget2x2ID_List, Intent(context, ListWidgetService::class.java))

            /*
            Here I specify the general template for the clicking action intent
            Inside the MyRemoteViewsFactory I will particularize it for the specific text clicked
             */
            intent = Intent(context, SessionWidget2x2::class.java)
            intent.action = "clicking_item"
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            remoteViews.setPendingIntentTemplate(R.id.SessionWidget2x2ID_List, pendingIntent)

            val createSessionPendingIntent = NavDeepLinkBuilder(context).setGraph(R.navigation.navigation_graph).setDestination(R.id.create_new_session_dialog).createPendingIntent()
            remoteViews.setOnClickPendingIntent(R.id.create_new_session_widget_button, createSessionPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
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

        //Log.d("my_debug", "Received! ${intent.action}")

        if(intent?.action == "clicking_item") {
            val name = intent.getStringExtra("name")
            val id = intent.getLongExtra("id", -1L)
            //Log.d("my_debug", "with name $name and id $id \n")

            val bundle = Bundle()
            bundle.putLong(SessionDetailsFragment.ARGUMENT_SESSION_ID, id)
            bundle.putString(SessionDetailsFragment.ARGUMENT_SESSION_NAME, name)

            NavDeepLinkBuilder(context)
                .setGraph(R.navigation.navigation_graph)
                .setDestination(R.id.sessionDetails)
                .setArguments(bundle)
                .createPendingIntent()
                .send()
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val remoteViews = RemoteViews(context.packageName, R.layout.session_widget2x2)
    remoteViews.removeAllViews(R.id.SessionWidget2x2ID_List)
    remoteViews.setRemoteAdapter(R.id.SessionWidget2x2ID_List, Intent(context, ListWidgetService::class.java))

    SessionWidget2x2.intent = Intent(context, SessionWidget2x2::class.java)
    SessionWidget2x2.intent.action = "clicking_item"
    val pendingIntent = PendingIntent.getBroadcast(context, 0, SessionWidget2x2.intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    remoteViews.setPendingIntentTemplate(R.id.SessionWidget2x2ID_List, pendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}


