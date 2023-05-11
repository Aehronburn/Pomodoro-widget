package it.unipd.dei.esp2023.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel

        binding.pomodoroDurationSlider.addOnChangeListener { _, value, _ ->
            mainViewModel.setPomodoroDuration(value.toInt())
        }
        binding.shortBreakDurationSlider.addOnChangeListener { _, value, _ ->
            mainViewModel.setShortBreakDuration(value.toInt())
        }
        binding.longBreakDurationSlider.addOnChangeListener { _, value, _ ->
            mainViewModel.setLongBreakDuration(value.toInt())
        }

        return binding.root
    }

}