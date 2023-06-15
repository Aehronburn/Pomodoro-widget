package it.unipd.dei.esp2023.content_providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import it.unipd.dei.esp2023.database.PomodoroDatabase
import it.unipd.dei.esp2023.database.PomodoroDatabaseDao
import kotlin.IllegalArgumentException

class StatisticsContentProvider : ContentProvider() {

    lateinit var database: PomodoroDatabaseDao

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, TODAY_PATH, 1)
        addURI(AUTHORITY, WEEK_PATH, 2)
        addURI(AUTHORITY, MONTH_PATH, 3)
    }

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
        return when (uriMatcher.match(uri)) {
            1 -> database.getTodayStatisticsCursor()
            2 -> database.getCurrentWeekStatisticsCursor()
            3 -> database.getCurrentMonthStatisticsCursor()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException()
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

    companion object {
        const val AUTHORITY = "it.unipd.dei.esp2023.content_providers.StatisticsContentProvider"
        const val TODAY_PATH = "today"
        const val WEEK_PATH = "week"
        const val MONTH_PATH = "month"
    }
}