package com.kursatmemis.e_ticaret_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.kursatmemis.e_ticaret_app.databinding.ActivityRegisterBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            closeKeyboard(it)
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar()
                register(email, password)
            } else {
                val message = "Please fill in the email and password fields."
                showSnackBar(message)
            }
        }

    }

    private fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hintProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun register(email: String, password: String) {
        FirebaseManager.createUser(email, password, object : FirebaseManager.CallBack {
            override fun onSuccess() {
                val message = "Registraction is successful. Please login on login page."
                saveEmailToDatabase(email)
                showSnackBar(message)
                FirebaseManager.signOut()
                hintProgressBar()
            }

            override fun onFailure(exceptionMsg: String) {
                showSnackBar(exceptionMsg)
                hintProgressBar()
            }
        })

    }

    private fun saveEmailToDatabase(email: String) {
        val database = FirebaseManager.database
        val auth = FirebaseManager.auth
        val ref = database.child("profile").child(auth.uid.toString()).child("email")
        FirebaseManager.saveData(ref, email)
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}