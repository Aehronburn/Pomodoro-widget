package it.unipd.dei.esp2023.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface PomodoroDatabaseDao {

    @Insert
    suspend fun insertSession(session: Session): Unit
}