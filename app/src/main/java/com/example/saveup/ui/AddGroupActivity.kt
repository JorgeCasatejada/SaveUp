package com.example.saveup.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.saveup.R

class AddGroupActivity : AppCompatActivity() {

    private lateinit var btAddParticipant: Button
    private lateinit var btCancel: Button
    private lateinit var btAdd: Button
    private lateinit var listView: ListView
    private lateinit var etIdParticipant: EditText
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        listView = findViewById(R.id.listParticipants)
        adapter = ArrayAdapter(this, R.layout.list_item)
        listView.adapter = adapter

        btAddParticipant = findViewById(R.id.btAddParticipant)
        btAddParticipant.setOnClickListener {
            etIdParticipant = findViewById(R.id.etIdParticipant)
            adapter.add(etIdParticipant.text.toString())
            etIdParticipant.setText("")
            // TODO: añadir a la lista de participantes del grupo
        }

        btCancel = findViewById(R.id.btCancel)
        btCancel.setOnClickListener {
            finish()
        }

        btAdd = findViewById(R.id.btAdd)
        btAdd.setOnClickListener {
            // TODO: crear grupo y añadirlo
        }
    }
}