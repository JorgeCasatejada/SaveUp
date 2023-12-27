package com.example.saveup.view.group

import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.saveup.R
import com.google.android.material.textfield.TextInputLayout

class AddGroupActivity : AppCompatActivity() {

    private lateinit var btAddParticipant: Button
    private lateinit var btCancel: Button
    private lateinit var btAdd: Button
    private lateinit var listView: ListView
    private lateinit var etIdParticipant: EditText
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var title: TextInputLayout
    private lateinit var budget: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        listView = findViewById(R.id.listParticipants)
        adapter = ArrayAdapter(this, R.layout.list_item)
        listView.adapter = adapter

        listView.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            // Handle ListView touch events.
            v.onTouchEvent(event)
            true
        }

        btAddParticipant = findViewById(R.id.btAddParticipant)
        btAddParticipant.setOnClickListener {
            etIdParticipant = findViewById(R.id.etIdParticipant)
            if (etIdParticipant.text.toString().isNotEmpty()) {
                adapter.add(etIdParticipant.text.toString())
                etIdParticipant.setText("")
            }
        }

        title = findViewById(R.id.outlinedTextFieldTitle)
        budget = findViewById(R.id.outlinedTextFieldBudget)

        btCancel = findViewById(R.id.btCancel)
        btCancel.setOnClickListener {
            finish()
        }

        btAdd = findViewById(R.id.btAdd)
        btAdd.setOnClickListener {
            val group = validateGroup()
        }
    }

    private fun validateGroup(): com.example.saveup.model.Group? {
        val title = validateTextField(title) ?: return null
        val budget = validateTextField(budget) ?: return null
        return com.example.saveup.model.Group(title, budget.toDouble(), )
    }

    private fun validateTextField(etLayout: TextInputLayout): String? {
        val str = etLayout.editText!!.text.toString().trim { it <= ' ' }
        if (str.isEmpty()) {
            etLayout.error = resources.getString(R.string.errCampoVacio)
            etLayout.editText!!.requestFocus()
            return null
        }
        return str
    }
}