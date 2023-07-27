package com.kursatmemis.e_ticaret_app.activities

import android.os.Bundle
import com.kursatmemis.e_ticaret_app.databinding.ActivityResetPasswordBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager

class ResetPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetPasswordButton.setOnClickListener {
            showProgressBar(binding.progressBar)
            closeKeyboard(it)
            val email = binding.emailEditText.text.toString()
            if (email.isNotEmpty()) {
                resetPasswordWithFirebase(email)
            } else {
                val message = "Please fill in the field."
                showSnackBar(binding.root, message)
                hideProgressBar(binding.progressBar)
            }

        }

    }

    private fun resetPasswordWithFirebase(email: String) {
        FirebaseManager.resetPassword(email, object : FirebaseManager.CallBack<Any?> {

            override fun onSuccess(data: Any?) {
                val message = "We sent your email address a password reset link."
                showSnackBar(binding.root, message)
                hideProgressBar(binding.progressBar)
            }

            override fun onFailure(errorMessage: String) {
                showSnackBar(binding.root, errorMessage)
                hideProgressBar(binding.progressBar)
            }

        })
    }

}