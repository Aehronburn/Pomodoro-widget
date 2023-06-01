package it.unipd.dei.esp2023.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStatisticsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)

        binding.todayAction.setOnClickListener {
            findNavController().navigate(R.id.action_statistics_fragment_to_sessions_fragment)
        }

        /*
        today's statistics
         */
        viewModel.todayStats.observe(viewLifecycleOwner) {
            binding.todayPomodorosCompleted.text = getString(R.string.pomodoros_completed, it.numCompleted)

            val (focusTimeHours, focusTimeMinutes) = timeToHhMm(it.focusTime)
            binding.todayProductivityTime.text = getString(R.string.productivity_time, focusTimeHours, focusTimeMinutes)

            val productivityImage = when(it.numCompleted) {
                0 -> R.drawable.pomodoro_artwork_sleeping
                in 1..4 -> R.drawable.pomodoro_artwork_working
                else -> R.drawable.pomodoro_artwork_completed
            }
            binding.todayProductivityImage.setImageDrawable(ResourcesCompat.getDrawable(resources, productivityImage, context?.theme))
        }

        viewModel.weeksStats.observe(viewLifecycleOwner) {
            val totalCompletedPomodoros = it.fold(0) { sum, singleStat ->  sum + singleStat.numCompleted}
            binding.weekPomodorosCompleted.text = getString(R.string.pomodoros_completed, totalCompletedPomodoros)

            val totalFocusTime = it.fold(0) { sum, singleStat -> sum + singleStat.focusTime}
            val (focusTimeHours, focusTimeMinutes) = timeToHhMm(totalFocusTime)
            binding.weekProductivityTime.text = getString(R.string.productivity_time, focusTimeHours, focusTimeMinutes)
        }

        viewModel.monthStats.observe(viewLifecycleOwner) {
            val totalCompletedPomodoros = it.fold(0) { sum, singleStat ->  sum + singleStat.numCompleted}
            binding.monthPomodorosCompleted.text = getString(R.string.pomodoros_completed, totalCompletedPomodoros)

            val totalFocusTime = it.fold(0) { sum, singleStat -> sum + singleStat.focusTime}
            val (focusTimeHours, focusTimeMinutes) = timeToHhMm(totalFocusTime)
            binding.monthProductivityTime.text = getString(R.string.productivity_time, focusTimeHours, focusTimeMinutes)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    companion object {

        /*
        Given time in minutes, returns the number of hours and number of minutes that do not make into an hour
         */
        private fun timeToHhMm(time: Int) : Pair<Int, Int> {
            return Pair(time / 60, time % 60)
        }
    }

}