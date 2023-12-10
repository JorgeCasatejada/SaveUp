package com.example.saveup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saveup.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.etEmail.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldEmail))
        binding.etPassword.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldPassword))
        binding.etPasswordRepeat.addTextChangedListener(ValidationTextWatcher(binding.outlinedTextFieldPasswordRepeat))

        binding.btSwitchToLogin.setOnClickListener { finish() }
        binding.btSignUp.setOnClickListener { signUp() }
    }

    private fun signUp() {
        if (validateFormData()) {
            enableForm(false)
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("FirebaseAuth", resources.getString(R.string.infoCreatedUser))
                    saveUserInFirestore(auth.currentUser)
                    Toast.makeText(
                        this,
                        resources.getString(R.string.infoCreatedUser),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    enableForm(true)
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveUserInFirestore(currentUser: FirebaseUser?) {
        if (currentUser == null) return
        val user = hashMapOf("email" to currentUser.email)
        db.collection("users").document(currentUser.uid).set(user)
            .addOnSuccessListener { Log.d("Firestore", "DocumentSnapshot User successfully written!") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error writing User document", e) }
        auth.signOut()
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