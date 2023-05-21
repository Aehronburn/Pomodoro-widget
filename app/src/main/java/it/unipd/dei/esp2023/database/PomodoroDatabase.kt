package it.unipd.dei.esp2023.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Session::class, Task::class, CompletedPomodoro::class], version = 1, exportSchema = false)
abstract class PomodoroDatabase : RoomDatabase() {

    abstract val databaseDao: PomodoroDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE : PomodoroDatabase? = null

        fun getInstance(context: Context) : PomodoroDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, PomodoroDatabase::class.java, "pomodoro_database").build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}