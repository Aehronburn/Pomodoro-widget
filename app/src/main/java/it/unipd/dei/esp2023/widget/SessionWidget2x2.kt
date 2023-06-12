package it.unipd.dei.esp2023.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
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
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    )
    {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
            val minWidth = info.minWidth
            val minHeight = info.minHeight
            val scale = context.resources.displayMetrics.density
            val minWidthDp = (minWidth / scale + 0.5f)
            val minHeightDp = (minHeight / scale + 0.5f)

            Log.d("my_debug", "minWidth is $minWidthDp")
            Log.d("my_debug", "minHeight is $minHeightDp")


            if(minWidthDp < 200) {

            }
            else{
                val remoteViews = RemoteViews(context.packageName, R.layout.session_widget2x2)
                //Remove all items in case app has been forced closed
                remoteViews.removeAllViews(R.id.SessionWidget2x2ID_List)

                remoteViews.setRemoteAdapter(
                    R.id.SessionWidget2x2ID_List,
                    Intent(context, ListWidgetService::class.java)
                )

                intent = Intent(context, SessionWidget2x2::class.java)
                intent.action = "clicking_item"
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent,
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                remoteViews.setPendingIntentTemplate(R.id.SessionWidget2x2ID_List, pendingIntent)

                val createSessionPendingIntent =
                    NavDeepLinkBuilder(context).setGraph(R.navigation.navigation_graph)
                        .setDestination(R.id.create_new_session_dialog).createPendingIntent()

                remoteViews.setOnClickPendingIntent(
                    R.id.create_new_session_widget_button,
                    createSessionPendingIntent
                )

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            }
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
        if(intent?.action == "clicking_item") {
            val name = intent.getStringExtra("name")
            val id = intent.getLongExtra("id", -1L)

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

    val createSessionPendingIntent =
        NavDeepLinkBuilder(context).
        setGraph(R.navigation.navigation_graph).
        setDestination(R.id.create_new_session_dialog).
        createPendingIntent()

    remoteViews.setOnClickPendingIntent(R.id.create_new_session_widget_button, createSessionPendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}


