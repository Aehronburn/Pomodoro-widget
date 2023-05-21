package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R
import androidx.fragment.app.Fragment

class SessionsFragment : Fragment() {


    private val viewModel: SessionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        val extendedFloatingActionButton = view.findViewById<ExtendedFloatingActionButton>(R.id.create_new_session_fab)
        extendedFloatingActionButton.setOnClickListener{
            CreateNewSessionDialog().show(parentFragmentManager, "CreateNewSessionDialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_id)
        val adapter = SessionsAdapter()
        recyclerView.adapter = adapter

        recyclerView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if(scrollY > oldScrollY) extendedFloatingActionButton.shrink()
            else extendedFloatingActionButton.extend()
        }

        viewModel.sessionList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        return view
    }

}