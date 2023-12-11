package com.example.saveup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.databinding.ActivitySignupBinding
import com.example.saveup.repositorios.TransactionsRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignUpViewModel
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = TransactionsRepository()
        val viewModelFactory = SignUpViewModelProviderFactory(repo)
        viewModel = ViewModelProvider(this, viewModelFactory)[SignUpViewModel::class.java]

        binding.etEmail.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldEmail))
        binding.etPassword.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldPassword))
        binding.etPasswordRepeat.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldPasswordRepeat))

        binding.btSwitchToLogin.setOnClickListener { finish() }
        binding.btSignUp.setOnClickListener { signUp() }

        viewModel.completedUserCreation.observe(this) {
            if (it) {
                finalize()
            }
        }
    }

    private fun signUp() {
        if (validateFormData()) {
            enableForm(false)
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("FirebaseAuth", resources.getString(R.string.infoCreatedUser))
                    viewModel.saveUserInFirestore(it.result.user)
                } else {
                    enableForm(true)
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun finalize() {
        Log.d("SignUpActivity", "Se va a cerrar la activity")
        Toast.makeText(
            applicationContext,
            resources.getString(R.string.infoCreatedUser),
            Toast.LENGTH_SHORT
        ).show()
        auth.signOut()
        finish()
    }

    private fun validateFormData(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val passwordRepeat = binding.etPasswordRepeat.text.toString()
        var etFocus: TextInputEditText? = null

        if (email.isBlank()) {
            binding.outlinedTextFieldEmail.error = resources.getString(R.string.errCampoVacio)
            etFocus = binding.etEmail
        }
        val minLen = 4
        if (password.length < minLen) {
            binding.outlinedTextFieldPassword.error =
                resources.getString(R.string.errMinLengthPassword, minLen)
            if (etFocus == null) etFocus = binding.etPassword
        }
        if (password.isBlank()) {
            binding.outlinedTextFieldPassword.error = resources.getString(R.string.errCampoVacio)
            if (etFocus == null) etFocus = binding.etPassword
        }
        if (passwordRepeat != password) {
            binding.outlinedTextFieldPasswordRepeat.error =
                resources.getString(R.string.errMismatchingPasswords)
            if (etFocus == null) etFocus = binding.etPasswordRepeat
        }
        etFocus?.requestFocus()
        return etFocus == null
    }

    private fun enableForm(isEnabled: Boolean) {
        binding.outlinedTextFieldEmail.isEnabled = isEnabled
        binding.outlinedTextFieldPassword.isEnabled = isEnabled
        binding.outlinedTextFieldPasswordRepeat.isEnabled = isEnabled
        binding.btSignUp.isEnabled = isEnabled
        binding.btSwitchToLogin.isEnabled = isEnabled
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