package it.unipd.dei.esp2023.sessions

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unipd.dei.esp2023.R

class SessionsFragment : Fragment() {

    private lateinit var viewModel: SessionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SessionsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_sessions, container, false)
    }

}