package it.unipd.dei.esp2023.sessions

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.unipd.dei.esp2023.R

class SessionsFragment : Fragment() {

    private lateinit var viewModel: SessionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SessionsViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        val extendedFloatingActionButton = view.findViewById<ExtendedFloatingActionButton>(R.id.create_new_session_fab)
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.sessions_scroll_view)
        nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(scrollY > oldScrollY) {
                extendedFloatingActionButton.shrink()
            } else {
                extendedFloatingActionButton.extend()
            }
        }
        return view
    }

}