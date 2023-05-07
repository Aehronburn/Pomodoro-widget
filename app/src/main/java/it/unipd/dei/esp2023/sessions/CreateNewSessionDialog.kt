package it.unipd.dei.esp2023.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import it.unipd.dei.esp2023.R

class CreateNewSessionDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(
            R.layout.fragment_dialog_create_new_session,
            container, false
        )

        val cancelBtn: Button = rootView.findViewById<Button>(R.id.cancelBtn)
        val submitBtn: Button = rootView.findViewById<Button>(R.id.submitBtn)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        submitBtn.setOnClickListener {
            val textInput = rootView.findViewById<View>(R.id.textInput) as EditText
            val newName: String = textInput.getText().toString()
            if (!newName.equals("")){
                //TODO: Implement insertion into database
                Toast.makeText(context, newName, Toast.LENGTH_LONG).show()
                dismiss()
            }
        }

        return rootView
    }
}