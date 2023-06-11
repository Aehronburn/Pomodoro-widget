package it.unipd.dei.esp2023

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import it.unipd.dei.esp2023.widget.SessionWidget2x2


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

        /*
        bottomNavigationBarView is visible only on the three main screens(Statistics, Sessions, Settings) and hidden everywhere else
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.sessions_fragment -> bottomNavigationBarView.visibility = View.VISIBLE
                R.id.settings_fragment -> bottomNavigationBarView.visibility = View.VISIBLE
                R.id.statistics_fragment -> bottomNavigationBarView.visibility = View.VISIBLE
                else -> bottomNavigationBarView.visibility = View.GONE
            }
        }

        /*
        when (Statistics, Sessions, Settings) are navigated using bottomNavigationView, toolbar title is updated accordingly
         */
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

        appBar.setNavigationOnClickListener { navController.navigateUp() }

    }

    /*
    override fun onDestroy() {
        val intent = Intent(this, SessionWidget2x2::class.java)
        intent.action = "forced_quit"
        sendBroadcast(intent)
        super.onDestroy()
    }

    */

}