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
import com.google.android.material.transition.MaterialFadeThrough
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStatisticsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)

        binding.todayAction.setOnClickListener {
            findNavController().navigate(R.id.action_statistics_fragment_to_sessions_fragment)
        }

        /*
        set x axis labels of week chart to week day (1, 2, 3, ...) -> ("Mon", "Tue", "Wed", ...)
         */
        (binding.weekChart.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>).valueFormatter =
            AxisValueFormatter { x, _ ->
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[x.toInt() - 1]
            }

        /*
        today's statistics
         */
        viewModel.todayStats.observe(viewLifecycleOwner) {
            viewModel.setTodayCompleted(it.numCompleted)
            viewModel.setTodayFocusTime(it.focusTime)

            binding.todayProductivityImage.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    getProductivityImage(it.numCompleted),
                    context?.theme
                )
            )
        }

        /*
        week's statistics
         */
        viewModel.weekStats.observe(viewLifecycleOwner) {
            val totalCompletedPomodoros =
                it.fold(0) { sum, singleStat -> sum + singleStat.numCompleted }
            viewModel.setWeekCompleted(totalCompletedPomodoros)

            val totalFocusTime = it.fold(0) { sum, singleStat -> sum + singleStat.focusTime }
            viewModel.setWeekFocusTime(totalFocusTime)

            val eachDayCompletedPomodoros = it.map { singleStat ->
                FloatEntry(
                    singleStat.dayNumber.toFloat(),
                    singleStat.numCompleted.toFloat()
                )
            }
            val entries = entryModelOf(eachDayCompletedPomodoros)
            binding.weekChart.setModel(entries)

            /*
            set number of y axis ticks to max value of the completed pomodoros in the period
             */
            val max = eachDayCompletedPomodoros.maxOfOrNull { entry -> entry.y.toInt() } ?: 0
            (binding.weekChart.startAxis as VerticalAxis<AxisPosition.Vertical.Start>).maxLabelCount =
                max
        }

        /*
        month's statistics
         */
        viewModel.monthStats.observe(viewLifecycleOwner) {
            val totalCompletedPomodoros =
                it.fold(0) { sum, singleStat -> sum + singleStat.numCompleted }
            viewModel.setMonthCompleted(totalCompletedPomodoros)

            val totalFocusTime = it.fold(0) { sum, singleStat -> sum + singleStat.focusTime }
            viewModel.setMonthFocusTime(totalFocusTime)

            val eachDayCompletedPomodoros = it.map { singleStat ->
                FloatEntry(
                    singleStat.dayNumber.toFloat(),
                    singleStat.numCompleted.toFloat()
                )
            }
            val entries = entryModelOf(eachDayCompletedPomodoros)
            binding.monthChart.setModel(entries)

            val max = eachDayCompletedPomodoros.maxOfOrNull { entry -> entry.y.toInt() } ?: 0
            (binding.monthChart.startAxis as VerticalAxis<AxisPosition.Vertical.Start>).maxLabelCount = max
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    companion object {
        fun getProductivityImage(numCompleted: Int) : Int {
            return when (numCompleted) {
                0 -> R.drawable.pomodoro_artwork_sleeping
                in 1..4 -> R.drawable.pomodoro_artwork_working
                else -> R.drawable.pomodoro_artwork_completed
            }
        }
    }

}