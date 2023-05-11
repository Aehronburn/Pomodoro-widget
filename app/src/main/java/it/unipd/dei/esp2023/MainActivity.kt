package it.unipd.dei.esp2023

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import it.unipd.dei.esp2023.database.Session
import it.unipd.dei.esp2023.settings.SettingsFragment
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationBarView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationBarView.setupWithNavController(navController)

        val appBar = findViewById<MaterialToolbar>(R.id.app_bar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.statistics_fragment,
                R.id.sessions_fragment,
                R.id.settings_fragment
            )
        )
        appBar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(appBar)

        viewModel.newSessionName.observe(this) {
            name -> if(name.isNotEmpty()) Toast.makeText(this, name, Toast.LENGTH_LONG).show()
        }

        val preferences = getPreferences(Context.MODE_PRIVATE)
        viewModel.setPomodoroDuration(preferences.getInt(SettingsFragment.POMODORO_DURATION, 1))
        viewModel.setShortBreakDuration(preferences.getInt(SettingsFragment.SHORT_BREAK_DURATION, 1))
        viewModel.setLongBreakDuration(preferences.getInt(SettingsFragment.LONG_BREAK_DURATION, 1))

        //Prova
        lifecycleScope.launch {
            setDefault()
        }
    }

    //Prova
    suspend fun setDefault(){
        //Insert one default Session
        val defaultSession: Session = Session(0L,"Hello", LocalDate.now().toString())
        viewModel.database.insertSession(defaultSession)
    }

    override fun onPause() {
        super.onPause()
        val preferencesEditor = getPreferences(Context.MODE_PRIVATE).edit()
        viewModel.pomodoroDuration.value?.let {
            preferencesEditor.putInt(
                SettingsFragment.POMODORO_DURATION,
                it
            )
        }
        viewModel.shortBreakDuration.value?.let {
            preferencesEditor.putInt(SettingsFragment.SHORT_BREAK_DURATION,
                it
            )
        }
        viewModel.longBreakDuration.value?.let {
            preferencesEditor.putInt(SettingsFragment.LONG_BREAK_DURATION,
                it
            )
        }
        preferencesEditor.apply()
    }
}