package it.unipd.dei.esp2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.*
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

        setOnApplyWindowInsetsListener(findViewById(R.id.fragmentContainerView)) { view, insets ->
            val windowInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePaddingRelative(top = windowInsets.top, bottom = windowInsets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}