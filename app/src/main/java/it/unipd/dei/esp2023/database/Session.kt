package it.unipd.dei.esp2023.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "session")
data class Session(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "creation_date")
    var creationDate: String = LocalDate.now().toString()
)
