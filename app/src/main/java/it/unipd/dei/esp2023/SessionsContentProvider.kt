package it.unipd.dei.esp2023

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import it.unipd.dei.esp2023.database.Session

class SessionsContentProvider: ContentProvider() {
    lateinit var database: PomodoroDatabaseDao

    override fun onCreate(): Boolean {
        //Pick database from application context because it already exists
        database = PomodoroDatabase.getInstance(context!!).databaseDao
        return true
    }

    //Il cursore non è aggiornato autmaticamente, è solamente un'istantanea di
    //un dato in quel preciso momento
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        //TODO: Continua a pescare lista NULL da getSessionsList().value
        //val myList = database.getSessionList()

        val myList = database.getSessionListNoLive()

        return createCursorFromList(myList)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        //This function is not available at the moment
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        //This function is not available at the moment
        return -1
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        //This function is not available at the moment
        return -1
    }

    override fun getType(uri: Uri): String? {
        //This function is not available at the moment
        return null
    }

    private fun createCursorFromList(myList: List<Session>): Cursor{
        val cursor = MatrixCursor(arrayOf("id", "name", "creation_date"))
            if(myList.isEmpty()){
                Log.d("Il_mio_tag", "Lista vuota")
            }

            myList.forEach{
                cursor.addRow(arrayOf(it.id, it.name, it.creationDate))
            }
     return cursor
    }
}