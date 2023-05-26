package it.unipd.dei.esp2023.timer

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentTimerBinding
import it.unipd.dei.esp2023.session_details.SessionDetailsFragment
import it.unipd.dei.esp2023.settings.SettingsFragment

class TimerFragment : Fragment() {

    private val viewModel: TimerViewModel by viewModels()

    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentTimerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        val sessionId = requireArguments().getLong(SessionDetailsFragment.ARGUMENT_SESSION_ID)

        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        viewModel.setPhasesDurations(
            preferences.getInt(SettingsFragment.POMODORO_DURATION, SettingsFragment.DEFAULT_POMODORO_DURATION),
            preferences.getInt(SettingsFragment.SHORT_BREAK_DURATION, SettingsFragment.DEFAULT_SHORT_BREAK_DURATION),
            preferences.getInt(SettingsFragment.LONG_BREAK_DURATION, SettingsFragment.DEFAULT_LONG_BREAK_DURATION)
        )

        activityViewModel.getTaskExtList(sessionId).observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                viewModel.createPhasesList(it)
                viewModel.updateCurrentPhase()
                Log.d("debug", viewModel.currentPhase.value.toString())
            }
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

}