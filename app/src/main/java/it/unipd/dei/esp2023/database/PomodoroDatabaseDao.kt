package it.unipd.dei.esp2023.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PomodoroDatabaseDao {

    @Insert
    suspend fun insertSession(session: Session): Unit

    @Query("SELECT * FROM session WHERE name = :myName")
    suspend fun getSession(myName: String): Session?
}