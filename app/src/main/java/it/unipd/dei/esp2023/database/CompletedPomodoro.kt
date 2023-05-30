package it.unipd.dei.esp2023.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
        tableName = "completed_pomodoro",
        foreignKeys = [
            ForeignKey(entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["task"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE)
            ]
        )
data class CompletedPomodoro(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "task")
    var task: Long? = null,

    @ColumnInfo(name = "completion_date")
    var completionDate: String= LocalDate.now().toString(),

    @ColumnInfo(name = "duration")
    var duration: Int=10,
)
