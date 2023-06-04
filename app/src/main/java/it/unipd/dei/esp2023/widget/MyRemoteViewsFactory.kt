package it.unipd.dei.esp2023.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.view.ContentInfoCompat.Flags
import it.unipd.dei.esp2023.MainActivity
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.SessionsContentProvider
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.Session


//Questa classe funge da Adapter tra il dataset, nel nostro caso un Cursore,
//e la lista ListView del widget
class MyRemoteViewsFactory(private val context: Context, intent: Intent):
    RemoteViewsService.RemoteViewsFactory
{
    private var myCursor: Cursor? = null


    override fun onCreate() {
        val contentResolver = context.contentResolver
        val uri =  Uri.parse(SessionsContentProvider.URI)
        myCursor = contentResolver.query(uri, null, null, null, null )
    }

    /*
    This function is triggered in the SessionsFragment.kt, inside the observation
    of LiveData variable "viewModel.sessionList"
     */
    override fun onDataSetChanged() {
        val contentResolver = context.contentResolver
        val uri =  Uri.parse(SessionsContentProvider.URI)
        myCursor = contentResolver.query(uri, null, null, null, null )
    }

    override fun onDestroy() {
        return
    }

    override fun getCount(): Int {
        return myCursor!!.count
    }

    override fun getViewAt(position: Int): RemoteViews {
        myCursor!!.moveToPosition(position)
        //name column
        val name = myCursor!!.getString(1)

        //Equivalente del viewHolder
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_list_item)
        remoteViews.setTextViewText(R.id.widget_list_item_text, name)

        val intent = Intent(context, SessionWidget2x2::class.java)
        intent.action = "clicking_item"
        intent.putExtra("pos", position)
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