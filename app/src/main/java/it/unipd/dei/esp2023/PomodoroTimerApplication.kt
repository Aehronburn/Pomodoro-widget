package it.unipd.dei.esp2023

import android.app.Application
import com.google.android.material.color.DynamicColors

class PomodoroTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}