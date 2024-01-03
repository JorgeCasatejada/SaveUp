package com.example.saveup.view.group

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.saveup.R

class GroupDialog : DialogFragment() {

    companion object {
        private const val ARG_CASE = "_case"

        fun newInstance(isSpecialCase: Boolean): GroupDialog {
            val fragment = GroupDialog()
            val args = Bundle()
            args.putBoolean(ARG_CASE, isSpecialCase)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Recuperar el valor de la condición de los argumentos
        val case = arguments?.getBoolean(ARG_CASE) ?: false

        return activity?.let {
            // Resto del código del diálogo permanece igual
            val builder = AlertDialog.Builder(it)

            val messageResId = if (case) {
                R.string.case_group_deleted
            } else {
                R.string.case_deleted_from_group
            }

            builder.setMessage(messageResId)
                .setPositiveButton(R.string.aceptar) { dialog, id ->
                    // START THE GAME!
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}