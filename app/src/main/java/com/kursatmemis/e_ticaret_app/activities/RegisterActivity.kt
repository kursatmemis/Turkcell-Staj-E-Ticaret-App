package com.kursatmemis.e_ticaret_app.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.databinding.ActivityRegisterBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.shashank.sony.fancytoastlib.FancyToast

class RegisterActivity : BaseActivity() {

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
                showProgressBar(binding.progressBar)
                register(email, password)
            } else {
                val message = "Please fill in the email and password fields."
                showSnackBar(binding.root, message)
            }
        }

        binding.resendVerificationTextView.setOnClickListener {
            showResendEmailDialog()
        }

    }

    private fun showResendEmailDialog() {
        var message: String
        var fancyType: Int
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setTitle("Resend Email Verification")

        val resendEmailDialog = layoutInflater.inflate(R.layout.resend_email_dialog, null)
        val progressBar = resendEmailDialog.findViewById<ProgressBar>(R.id.progressBar)
        val emailEditText = resendEmailDialog.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = resendEmailDialog.findViewById<EditText>(R.id.passwordEditText)
        val sendButton = resendEmailDialog.findViewById<Button>(R.id.sendButton)
        builder.setView(resendEmailDialog)

        sendButton.setOnClickListener {
            showProgressBar(progressBar)
            closeKeyboard(it)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithFirebase(email, password, object : FirebaseManager.CallBack<Any?>{

                    override fun onSuccess(data: Any?) {
                        message = "We sent you a new mail for verification."
                        fancyType = FancyToast.SUCCESS
                        this@RegisterActivity.showFancyToast(message, fancyType)
                        FirebaseManager.auth.currentUser?.sendEmailVerification()
                        hideProgressBar(progressBar)
                    }

                    override fun onFailure(errorMessage: String) {
                        fancyType = FancyToast.ERROR
                        this@RegisterActivity.showFancyToast(errorMessage, fancyType)
                        hideProgressBar(progressBar)
                    }

                }, true)
            } else {
                fancyType = FancyToast.WARNING
                this.showFancyToast("Please fill in the fields.", fancyType)
                hideProgressBar(progressBar)
            }
        }

        builder.show()
    }

    private fun register(email: String, password: String) {
        FirebaseManager.createUser(email, password, object : FirebaseManager.CallBack<Any?> {
            override fun onSuccess(data: Any?) {
                val message = "Registraction is successful. Please verify your email and login on login page."
                saveEmailToDatabase(email)
                FirebaseManager.auth.signOut()
                showSnackBar(binding.root, message)
                hideProgressBar(binding.progressBar)
            }

            override fun onFailure(errorMessage: String) {
                showSnackBar(binding.root, errorMessage)
                hideProgressBar(binding.progressBar)
            }
        })
    }

    private fun saveEmailToDatabase(email: String) {
        val database = FirebaseManager.database
        val auth = FirebaseManager.auth
        val ref = database.child("profile").child(auth.uid.toString()).child("email")
        FirebaseManager.saveData(ref, email)
    }

}