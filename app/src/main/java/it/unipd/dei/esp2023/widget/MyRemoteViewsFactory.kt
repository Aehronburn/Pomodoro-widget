package it.unipd.dei.esp2023.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.SessionsContentProvider
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.Session


//Questa classe funge da Adapter tra il dataset, nel nostro caso un Cursore,
//e la lista ListView del widget
class MyRemoteViewsFactory(private val context: Context, intent: Intent):
    RemoteViewsService.RemoteViewsFactory
{
    private val database = PomodoroDatabase.getInstance(context).databaseDao

    private var myList: List<Session> = emptyList()

    override fun onCreate() {
        val contentResolver = context.contentResolver
        val uri =  Uri.parse(SessionsContentProvider.URI)
        val myCursor = contentResolver.query(uri, null, null, null, null )
        while (myCursor!!.moveToNext()){
            Log.d("debug_1", "${myCursor.count}")
        }
    }

    override fun onDataSetChanged() {
        return
    }

    override fun onDestroy() {
        return
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getViewAt(position: Int): RemoteViews {
        //Equivalente del viewHolder
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_list_item)

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