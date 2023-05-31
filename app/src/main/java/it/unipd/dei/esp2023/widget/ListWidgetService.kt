package it.unipd.dei.esp2023.widget

import android.content.Intent
import android.widget.RemoteViewsService


//Questa classe è un Service che serve solo a ritornare la Factory
class ListWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return MyRemoteViewsFactory(applicationContext, intent!!)
    }
}