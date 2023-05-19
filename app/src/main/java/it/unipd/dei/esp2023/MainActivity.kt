package it.unipd.dei.esp2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        Displays content edge to edge https://developer.android.com/develop/ui/views/layout/edge-to-edge
         */
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationBarView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationBarView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.sessions_fragment -> bottomNavigationBarView.visibility = View.VISIBLE
                R.id.settings_fragment -> bottomNavigationBarView.visibility = View.VISIBLE
                R.id.statistics_fragment -> bottomNavigationBarView.visibility = View.VISIBLE
                else -> bottomNavigationBarView.visibility = View.GONE
            }
        }

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
        /*
        TODO navigating up by pressing back icon with this method does not provide animations, in contrast with using back gesture
         */
        appBar.setNavigationOnClickListener { navController.navigateUp() }

    }

}