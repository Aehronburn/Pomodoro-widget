package it.unipd.dei.esp2023.sessions_widget

import android.content.Intent
import android.widget.RemoteViewsService


//Questa classe è un Service che serve solo a ritornare la Factory
class SessionsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        // TODO
        // mi pare vagamente che ci fosse qualche problema a usare applicationContext da qualche parte...
        // me lo segno così mi ricordo di controllare qualche caso limite
        return SessionsRemoteViewsFactory(applicationContext)
    }
}