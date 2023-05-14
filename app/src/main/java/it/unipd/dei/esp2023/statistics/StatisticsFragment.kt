package it.unipd.dei.esp2023.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.todayAction.setOnClickListener { v ->
            findNavController().navigate(R.id.action_statistics_fragment_to_sessions_fragment)
        }
        return binding.root
    }

}