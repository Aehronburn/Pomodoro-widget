package it.unipd.dei.esp2023.sessions_widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.content_providers.SessionsContentProvider
import kotlinx.coroutines.*

class SessionsRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private var sessionList: Cursor? = null

    override fun onCreate() {
        runBlocking {
            loadSessionList()
        }
    }

    private suspend fun loadSessionList() {
        withContext(Dispatchers.IO) {
            launch {
                val contentResolver = context.contentResolver
                val uri = Uri.parse(SessionsContentProvider.URI)
                sessionList = contentResolver.query(uri, null, null, null, null)
            }
        }
    }

    /*
    This function is triggered in the SessionsFragment.kt, inside the observation
    of LiveData variable "viewModel.sessionList"
     */
    override fun onDataSetChanged() {
        val contentResolver = context.contentResolver
        val uri = Uri.parse(SessionsContentProvider.URI)
        /*
        * From onDataSetChanged documentation
        *
        * "Note: expensive tasks can be safely performed synchronously within this method.
        * In the interim, the old data will be displayed within the widget."
        *
        * Hence, coroutines are not used here
        * */
        sessionList = contentResolver.query(uri, null, null, null, null)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids =
            appWidgetManager.getAppWidgetIds(ComponentName(context, SessionsWidget::class.java))
        ids.forEach { id ->
            updateAppWidget(context, appWidgetManager, id)
        }
    }

    override fun onDestroy() {
        sessionList?.close()
        return
    }

    override fun getCount(): Int {
        return sessionList!!.count
    }

    override fun getViewAt(position: Int): RemoteViews {
        sessionList!!.moveToPosition(position)
        val id = sessionList!!.getLong(0)
        val name = sessionList!!.getString(1)

        val remoteViews = RemoteViews(context.packageName, R.layout.sessions_widget_list_item)
        remoteViews.setTextViewText(R.id.widget_list_item_text, name)

        /*
        It's important to refer to the same intent of the SessionWidget2x2 class, to be able to modify
        its extras and pass the name of the current session
         */

        val intent = SessionsWidget.intent
        intent.putExtra(SessionsWidget.INTENT_EXTRA_SESSION_NAME, name)
        intent.putExtra(SessionsWidget.INTENT_EXTRA_SESSION_ID, id)
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item_text, intent)

        return remoteViews
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun hasStableIds(): Boolean {
        return false
    }

}