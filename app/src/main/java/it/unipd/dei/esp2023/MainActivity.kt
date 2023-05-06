package it.unipd.dei.esp2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import androidx.core.view.*
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationBarView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationBarView.setupWithNavController(navController)

        val appBar = findViewById<MaterialToolbar>(R.id.app_bar)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.statistics_fragment, R.id.sessions_fragment, R.id.settings_fragment))
        appBar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(appBar)

        setOnApplyWindowInsetsListener(findViewById(R.id.app_bar_layout)) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePaddingRelative(top = insets.top, bottom = insets.bottom)
            windowInsets
        }
    }
}