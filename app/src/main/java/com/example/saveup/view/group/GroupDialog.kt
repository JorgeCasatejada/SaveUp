package com.example.saveup.view.group

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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
                    // Cerrar el fragmento
                    closeFragment()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    override fun onCancel(dialog: DialogInterface) {
        // Se llama cuando se cancela el diálogo (por ejemplo, haciendo clic fuera de él)
        super.onCancel(dialog)
        closeFragment()
    }


    private fun closeFragment() {
        requireActivity().supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}