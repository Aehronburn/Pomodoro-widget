package it.unipd.dei.esp2023.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "task",
        foreignKeys = [
            ForeignKey(entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["session"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
            ]
        )
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "session")
    var session: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String="",

    @ColumnInfo(name = "task_order")
    var order: Int=0,

    @ColumnInfo(name = "total_pomodoros")
    var totalPomodoros: Int=1,
)
