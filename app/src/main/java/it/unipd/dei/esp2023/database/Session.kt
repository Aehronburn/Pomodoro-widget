package it.unipd.dei.esp2023.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.time.LocalDate

data class Session(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "creation_date")
    var creationDate: String = LocalDate.now().toString()
)
