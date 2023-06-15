package it.unipd.dei.esp2023.sessions_widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.SessionsContentProvider


//Questa classe funge da Adapter tra il dataset, nel nostro caso un Cursore,
//e la lista ListView del widget
class SessionsRemoteViewsFactory(private val context: Context):
    RemoteViewsService.RemoteViewsFactory
{
    private var sessionList: Cursor? = null

    override fun onCreate() {
        val contentResolver = context.contentResolver
        val uri =  Uri.parse(SessionsContentProvider.URI)
        sessionList = contentResolver.query(uri, null, null, null, null )
    }

    /*
    This function is triggered in the SessionsFragment.kt, inside the observation
    of LiveData variable "viewModel.sessionList"
     */
    override fun onDataSetChanged() {
        val contentResolver = context.contentResolver
        val uri =  Uri.parse(SessionsContentProvider.URI)
        sessionList = contentResolver.query(uri, null, null, null, null )

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, SessionWidget2x2::class.java)) ?: intArrayOf(-1)
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
        //"id" column
        val id = sessionList!!.getLong(0)
        //"name" column
        val name = sessionList!!.getString(1)

        //Equivalente del viewHolder
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_list_item)
        remoteViews.setTextViewText(R.id.widget_list_item_text, name)

        /*
        It's important to refer to the same intent of the SessionWidget2x2 class, to be able to modify
        its extras and pass the name of the current session
         */

        val intent = SessionWidget2x2.intent
        intent.putExtra("name", name)
        intent.putExtra("id", id)
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