package it.unipd.dei.esp2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
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

        viewModel.newSessionName.observe(this) { name -> if(name.isNotEmpty()) Toast.makeText(this, name, Toast.LENGTH_LONG).show() }
    }
}