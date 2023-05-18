package it.unipd.dei.esp2023.sessions

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import it.unipd.dei.esp2023.MainViewModel
import it.unipd.dei.esp2023.R
import it.unipd.dei.esp2023.database.Session
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate

class CreateNewSessionDialog: DialogFragment() {

    private val viewModel: SessionsViewModel by activityViewModels()

    lateinit var dialogView : View

    var sessionName: String = ""

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        dialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState)
        builder.setView(dialogView)
        builder.setTitle(R.string.create_new_session_title)
        return builder.create()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dialog_create_new_session, container, false)

        val textInput = view.findViewById<TextInputEditText>(R.id.new_session_input_edit_text)
        textInput.doOnTextChanged { text, _, _, _ ->
            sessionName = text.toString()
            Log.d("TextChanged", sessionName)
        }

        val cancelButton = view.findViewById<Button>(R.id.cancel_session_button)
        cancelButton.setOnClickListener {
            dismiss()
        }

        val createButton = view.findViewById<Button>(R.id.create_session_button)
        createButton.setOnClickListener {
            viewModel.setNewSessionName(sessionName)

            lifecycleScope.launch() {
                viewModel.database.insertSession(Session(0L, sessionName, LocalDate.now().toString()))
                viewModel.mySessionList = viewModel.database.getSessionList()

                lifecycleScope.cancel()
            }

            dismiss()
        }

        return view
    }

    override fun getView(): View {
        return dialogView
    }
}