package it.unipd.dei.esp2023.widget

import android.content.Intent
import android.widget.RemoteViewsService


//Questa classe è un Service che permette all'adapter remoto di richiedere l'oggetto di
// tipo RemoteView
class ListWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        TODO("Not yet implemented")
    }
}