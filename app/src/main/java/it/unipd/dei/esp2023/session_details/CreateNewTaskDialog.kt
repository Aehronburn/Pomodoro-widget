package it.unipd.dei.esp2023.session_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import it.unipd.dei.esp2023.R

class CreateNewTaskDialog : DialogFragment() {

    /*
    The view of the dialog fragment we are manually creating inside onCreateDialog
     */
    private lateinit var dialogView: View

    private var taskName: String = ""
    private var pomodorosNumber: Int = 0

    /*
    In order to build a dialog with Material 3 theming, it is required to create it using
    MaterialAlertDialogBuilder inside the onCreateDialog method, and inflate the view using its
    setView method. The drawback is that we are required to setup the listeners inside
    OnCreateDialog, since onCreateView is ignored if onCreateDialog is overridden.
    To keep the code clean and enable view binding the solution is to inflate the layout inside
    onCreateView as usual and invoke it manually it inside onCreateDialog, and then overriding
    getView.
    Since Android Studio expects us to inflate the view in onCreateView using the inflater passed as
    parameter, we are suppressing the linter warning.
    */
    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        dialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState)
        builder.setView(dialogView)
        builder.setTitle(R.string.create_new_task_title)
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_new_task_dialog, container, false)

        val nameInput = view.findViewById<TextInputEditText>(R.id.new_task_input_name_edit_text)
        nameInput.doOnTextChanged { text, _, _, _ ->
            taskName = text.toString()
        }

        val numberInput = view.findViewById<TextInputEditText>(R.id.new_task_input_number_edit_text)
        numberInput.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                pomodorosNumber = text.toString().toInt()
            }
        }

        val cancelButton = view.findViewById<Button>(R.id.cancel_task_button)
        cancelButton.setOnClickListener { dismiss() }

        val createButton = view.findViewById<Button>(R.id.create_task_button)
        createButton.setOnClickListener {
            //TODO invocare funzione dal SessionDetailsViewModel
            dismiss()
        }

        return view
    }

    /*
    Since onCreateView is ignored when onCreateDialog is overridden, a call to getView returns null,
    so we must override getView to return the view we inflated manually inside onCreateDialog
     */
    override fun getView(): View {
        return dialogView
    }

}