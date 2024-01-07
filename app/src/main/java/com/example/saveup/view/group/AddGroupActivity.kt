package com.example.saveup.view.group

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.saveup.R
import com.example.saveup.databinding.ActivityAddGroupBinding
import com.example.saveup.model.Group
import com.google.android.material.textfield.TextInputLayout

class AddGroupActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var binding: ActivityAddGroupBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ArrayAdapter(this, R.layout.list_item)
        binding.listParticipants.adapter = adapter

        binding.listParticipants.setOnTouchListener { v, event ->
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

        binding.btAddParticipant.setOnClickListener {
            val participant = binding.etIdParticipant.text.toString()
            if (participant.isNotEmpty()) {
                adapter.add(participant)
                binding.etIdParticipant.setText("")
            }
        }

        binding.btCancel.setOnClickListener {
            finish()
        }

        binding.imageViewGroupImage.setOnClickListener {
            selectImage()
        }

        binding.btAdd.setOnClickListener {
            val group = validateGroup()
            if (group != null) {
                val data = Intent()
                data.putExtra(CREATED_GROUP, group)

                val participantsList = ArrayList<String>()
                for (i in 0 until adapter.count) {
                    val item = adapter.getItem(i)
                    if (item != null) {
                        participantsList.add(item)
                    }
                }

                data.putExtra(PARTICIPANTS, participantsList)
                data.putExtra(IMAGE_URI, imageUri)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }


        binding.etIdParticipant.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.btAddParticipant.callOnClick()
                true
            } else {
                false
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, INTENT_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == INTENT_SELECT_IMAGE) {
            if (data != null) {
                imageUri = data.data
                Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(binding.imageViewGroupImage)
            }
        }
    }

    private fun validateGroup(): Group? {
        val title = validateTextField(binding.outlinedTextFieldTitle) ?: return null
        val budget = validateTextField(binding.outlinedTextFieldBudget) ?: return null
        val description = validateTextField(binding.tfGroupDescription) ?: return null
        // TODO: Añadir imagen

        return Group(
            "", title, budget.toDouble(), budget.toDouble(), description,
            "", mutableListOf(), mutableListOf()
        )
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

    companion object {
        // Constants
        const val CREATED_GROUP = "created_group"
        const val PARTICIPANTS = "participants"
        const val IMAGE_URI = "image_uri"
        const val INTENT_SELECT_IMAGE = 1
    }
}