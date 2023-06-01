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
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentStatisticsBinding
import java.math.RoundingMode

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
        set x axis labels of week chart to week day
         */
        (binding.weekChart.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>).valueFormatter = weekDayAxisFormatter

        /*
        format y axis of chart with integers instead of floats
        with(binding.weekChart.startAxis as VerticalAxis<AxisPosition.Vertical.Start>) {
            //valueFormatter = startAxisFormatter
            maxLabelCount = 7
        }
        with(binding.monthChart.startAxis as VerticalAxis<AxisPosition.Vertical.Start>) {
            //valueFormatter = startAxisFormatter
            maxLabelCount = 7
        }

         */

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

        /*
        week's statistics
         */
        viewModel.weeksStats.observe(viewLifecycleOwner) {
            val totalCompletedPomodoros = it.fold(0) { sum, singleStat ->  sum + singleStat.numCompleted}
            binding.weekPomodorosCompleted.text = getString(R.string.pomodoros_completed, totalCompletedPomodoros)

            val totalFocusTime = it.fold(0) { sum, singleStat -> sum + singleStat.focusTime}
            val (focusTimeHours, focusTimeMinutes) = timeToHhMm(totalFocusTime)
            binding.weekProductivityTime.text = getString(R.string.productivity_time, focusTimeHours, focusTimeMinutes)

            val eachDayCompletedPomodoros = it.map { singleStat -> FloatEntry(singleStat.dayNumber.toFloat(), singleStat.numCompleted.toFloat()) }
            val entries = entryModelOf(eachDayCompletedPomodoros)
            binding.weekChart.setModel(entries)
        }

        /*
        month's statistics
         */
        viewModel.monthStats.observe(viewLifecycleOwner) {
            val totalCompletedPomodoros =
                it.fold(0) { sum, singleStat -> sum + singleStat.numCompleted }
            binding.monthPomodorosCompleted.text =
                getString(R.string.pomodoros_completed, totalCompletedPomodoros)

            val totalFocusTime = it.fold(0) { sum, singleStat -> sum + singleStat.focusTime }
            val (focusTimeHours, focusTimeMinutes) = timeToHhMm(totalFocusTime)
            binding.monthProductivityTime.text =
                getString(R.string.productivity_time, focusTimeHours, focusTimeMinutes)

            val eachDayCompletedPomodoros = it.map { singleStat ->
                FloatEntry(
                    singleStat.dayNumber.toFloat(),
                    singleStat.numCompleted.toFloat()
                )
            }
            val entries = entryModelOf(eachDayCompletedPomodoros)
            binding.monthChart.setModel(entries)
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

        /*
        associate week day in number returned by database to string: (1, 2, 3...) -> ("Mon", "Tue", "Wed"...)
         */
        private val weekDayAxisFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ ->
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[x.toInt() - 1]
        }

        /*
        convert float y tick value to integer(pattern "#"), rounding to ceiling

        private val startAxisFormatter = DecimalFormatAxisValueFormatter<AxisPosition.Vertical.Start>("#", RoundingMode.CEILING)
         */
    }

}