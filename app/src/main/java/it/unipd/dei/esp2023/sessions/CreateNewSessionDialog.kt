package it.unipd.dei.esp2023.sessions

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import it.unipd.dei.esp2023.R

class CreateNewSessionDialog: DialogFragment() {

    internal lateinit var listener: SessionDialogListener

    lateinit var dialogView : View

    var sessionName: String = ""

    interface SessionDialogListener {
        fun onDialogPositiveClick(sessionName: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SessionDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        dialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState)
        builder.setView(dialogView)
        builder.setTitle(R.string.create_new_session_title)
        builder.setPositiveButton(R.string.create_new_session_positive) { _, _ ->
            listener.onDialogPositiveClick(sessionName)
        }
        builder.setNegativeButton(R.string.create_new_session_negative
        ) { _, _ -> listener.onDialogNegativeClick(this) }
        return builder.create()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dialog_create_new_session, container, false)
        val textInput = view.findViewById<TextInputEditText>(R.id.new_session_input_edit_text)
        textInput.doOnTextChanged { text, start, before, count ->
            sessionName = text.toString()
        }
        return view
    }

    override fun getView(): View {
        return dialogView
    }
}