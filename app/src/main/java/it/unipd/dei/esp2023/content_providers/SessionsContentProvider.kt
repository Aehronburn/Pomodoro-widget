package it.unipd.dei.esp2023.content_providers

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao

class SessionsContentProvider: ContentProvider() {
    lateinit var database: PomodoroDatabaseDao

    override fun onCreate(): Boolean {
        database = PomodoroDatabase.getInstance(context!!).databaseDao
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        if(uri != Uri.parse(URI)) {
            throw IllegalArgumentException()
        }
        return database.getSessionListCursor()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException()
    }

    companion object {
        const val URI = "content://it.unipd.dei.esp2023.content_providers.SessionsContentProvider/sessions"
    }
}