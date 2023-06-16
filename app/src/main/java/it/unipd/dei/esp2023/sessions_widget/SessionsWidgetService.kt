package it.unipd.dei.esp2023.sessions_widget

import android.content.Intent
import android.widget.RemoteViewsService


//Questa classe è un Service che serve solo a ritornare la Factory
class SessionsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return SessionsRemoteViewsFactory(applicationContext)
    }
}