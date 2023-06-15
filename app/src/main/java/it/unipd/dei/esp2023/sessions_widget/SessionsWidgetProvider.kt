package it.unipd.dei.esp2023.sessions_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.navigation.NavDeepLinkBuilder
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment

class SessionsWidget : AppWidgetProvider() {
    companion object {
        lateinit var intent: Intent
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            createRemoteViews(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "clicking_item") {
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
    createRemoteViews(context, appWidgetManager, appWidgetId)
}


fun createRemoteViews(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val remoteViews = RemoteViews(context.packageName, R.layout.sessions_widget)
    remoteViews.removeAllViews(R.id.sessions_widget_list)
    remoteViews.setRemoteAdapter(
        R.id.sessions_widget_list,
        Intent(context, SessionsWidgetService::class.java)
    )

    SessionsWidget.intent = Intent(context, SessionsWidget::class.java)
    SessionsWidget.intent.action = "clicking_item"
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, SessionsWidget.intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    remoteViews.setPendingIntentTemplate(R.id.sessions_widget_list, pendingIntent)

    val createSessionPendingIntent =
        NavDeepLinkBuilder(context).setGraph(R.navigation.navigation_graph)
            .setDestination(R.id.create_new_session_dialog).createPendingIntent()

    remoteViews.setOnClickPendingIntent(
        R.id.create_new_session_widget_button,
        createSessionPendingIntent
    )

    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}


