package it.unipd.dei.esp2023.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.pomodoroDurationSlider.addOnChangeListener { _, value, _ ->
            viewModel.setPomodoroDuration(value.toInt())
        }
        binding.shortBreakDurationSlider.addOnChangeListener { _, value, _ ->
            viewModel.setShortBreakDuration(value.toInt())
        }
        binding.longBreakDurationSlider.addOnChangeListener { _, value, _ ->
            viewModel.setLongBreakDuration(value.toInt())
        }

        /*
        loading settings that were last saved in SharedPreferences
         */
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        viewModel.setPomodoroDuration(preferences.getInt(POMODORO_DURATION, DEFAULT_POMODORO_DURATION))
        viewModel.setShortBreakDuration(preferences.getInt(SHORT_BREAK_DURATION, DEFAULT_SHORT_BREAK_DURATION))
        viewModel.setLongBreakDuration(preferences.getInt(LONG_BREAK_DURATION, DEFAULT_LONG_BREAK_DURATION))

        return binding.root
    }

    /*
    Saving settings to persistent memory
     */
    override fun onPause() {
        super.onPause()
        val preferencesEditor = requireActivity().getPreferences(Context.MODE_PRIVATE).edit()
        viewModel.pomodoroDuration.value?.let {
            preferencesEditor.putInt(
                POMODORO_DURATION,
                it
            )
        }
        viewModel.shortBreakDuration.value?.let {
            preferencesEditor.putInt(SHORT_BREAK_DURATION,
                it
            )
        }
        viewModel.longBreakDuration.value?.let {
            preferencesEditor.putInt(LONG_BREAK_DURATION,
                it
            )
        }
        preferencesEditor.apply()
    }

    companion object {
        const val POMODORO_DURATION = "pomodoro_duration"
        const val SHORT_BREAK_DURATION = "short_break_duration"
        const val LONG_BREAK_DURATION = "long_break_duration"
        const val DEFAULT_POMODORO_DURATION = 1
        const val DEFAULT_SHORT_BREAK_DURATION = 1
        const val DEFAULT_LONG_BREAK_DURATION = 1
    }

}