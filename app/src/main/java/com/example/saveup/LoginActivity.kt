package com.example.saveup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saveup.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, resources.getString(R.string.infoLoggedIn), Toast.LENGTH_LONG)
                .show()
            finish()
        }

        binding.etEmail.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldEmail))
        binding.etPassword.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldPassword))

        binding.btSwitchToRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btLogin.setOnClickListener {
            if (validateFormData()) {
                enableForm(false)
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(
                            this,
                            resources.getString(R.string.infoLoggedIn),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        enableForm(true)
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    private fun validateFormData(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        var isFormValid = true
        if (password.isBlank()) {
            binding.outlinedTextFieldPassword.error = resources.getString(R.string.errCampoVacio)
            binding.etPassword.requestFocus()
            isFormValid = false
        }
        if (email.isBlank()) {
            binding.outlinedTextFieldEmail.error = resources.getString(R.string.errCampoVacio)
            binding.etEmail.requestFocus()
            isFormValid = false
        }
        return isFormValid
    }

    private fun enableForm(isEnabled: Boolean) {
        binding.outlinedTextFieldEmail.isEnabled = isEnabled
        binding.outlinedTextFieldPassword.isEnabled = isEnabled
        binding.btLogin.isEnabled = isEnabled
        binding.btSwitchToRegister.isEnabled = isEnabled
        binding.progressBar.visibility = if (isEnabled) View.GONE else View.VISIBLE
    }

    private class ValidationTextWatcher(private val etLayout: TextInputLayout) :
        TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            etLayout.error = null
        }
    }
}