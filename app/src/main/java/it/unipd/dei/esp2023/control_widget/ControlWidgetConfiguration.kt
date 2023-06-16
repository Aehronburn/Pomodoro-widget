package it.unipd.dei.esp2023.control_widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import it.unipd.dei.esp2023.R

// https://www.youtube.com/watch?v=C7IW49jejUY
// https://gist.github.com/codinginflow/884991d7db005b9a50c8332cdd8cb7ec

class ControlWidgetConfiguration : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var toggleSwitch: MaterialSwitch

    /*
    * Commit must be used in place of apply for saving shared preferences since settings must be read,
    * immediately after they are written, outside of the main thread
    * */
    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_widget_configuration)

        val extras: Bundle? = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        val cancelResult = Intent()
        cancelResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_CANCELED, cancelResult)

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        toggleSwitch = findViewById(R.id.transparentBgSwitch)
        findViewById<MaterialButton>(R.id.addWidgetBtn).setOnClickListener {
            val prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(SHARED_PREFERENCES_KEY_PREFIX + appWidgetId, toggleSwitch.isChecked)
            editor.commit()

            val addWidgetResult = Intent()
            addWidgetResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, addWidgetResult)

            // https://stackoverflow.com/a/14991479
            val intent = Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this,
                ControlWidgetProvider::class.java
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
            sendBroadcast(intent)

            finish()
        }
    }

    companion object {
        const val SHARED_PREFERENCES_NAME = "ControlWidgetTransparentBgPreferences"
        const val SHARED_PREFERENCES_KEY_PREFIX = "TransparentBgKey"
        const val DEFAULT_TRANSPARENCY_VALUE = false
    }
}