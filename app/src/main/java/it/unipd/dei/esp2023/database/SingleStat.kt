package it.unipd.dei.esp2023.database

import java.io.FileDescriptor

data class SingleStat(
    var dayNumber: Int,
    var dayDescription: String,
    var numCompleted: Int,
    var focusTime: Int,
)
