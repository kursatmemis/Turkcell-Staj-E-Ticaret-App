package com.kursatmemis.e_ticaret_app.activities

import android.content.Intent
import android.os.Bundle
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.databinding.ActivityLoginBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.SharedPrefManager
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.GetAllUserResponse
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Calendar
import kotlin.random.Random

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            disableButtonClick()
            closeKeyboard(it)
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar(binding.progressBar)
                if (isServiceLogin(username)) {
                    val user = User(username, password)
                    loginWithService(user, object : CallBack<UserData> {
                        override fun onSuccess(data: UserData) {
                            saveLastLoginTime(user.username, user.password)
                            goToMainActivity(data.id.toString(), true)
                        }

                        override fun onFailure(errorMessage: String) {
                            showFancyToast(errorMessage, FancyToast.WARNING)
                            hideProgressBar(binding.progressBar)
                            enableButtonClick()
                        }

                    })
                } else {
                    loginWithFirebase(username, password, object : CallBack<Any?> {

                        override fun onSuccess(data: Any?) {
                            saveLastLoginTime(username, password)
                            goToMainActivity(FirebaseManager.auth.currentUser!!.uid, false)
                        }

                        override fun onFailure(errorMessage: String) {
                            showFancyToast(errorMessage, FancyToast.WARNING)
                            enableButtonClick()
                            hideProgressBar(binding.progressBar)
                        }
                    })
                }

            } else {
                val message = "Please fill in the username and password fields."
                showFancyToast(message, FancyToast.INFO)
                enableButtonClick()
            }
        }

        binding.loginRandomUserButton.setOnClickListener {
            disableButtonClick()
            closeKeyboard(it)
            loginRandomUser()
        }

        binding.registerTextView.setOnClickListener {
            disableButtonClick()
            goToRegisterActivity()
        }

        binding.resetPasswordButton.setOnClickListener {
            goToResetPasswordActivity()
        }
    }

    private fun goToResetPasswordActivity() {
        val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun enableButtonClick() {
        binding.loginButton.isClickable = true
        binding.loginRandomUserButton.isClickable = true
        binding.registerTextView.isClickable = true
    }

    private fun disableButtonClick() {
        binding.loginButton.isClickable = false
        binding.loginRandomUserButton.isClickable = false
        binding.registerTextView.isClickable = false
    }

    private fun saveLastLoginTime(username: String, password: String) {
        val lastLoginTime = Calendar.getInstance().timeInMillis
        SharedPrefManager.setSharedPreference(
            this@LoginActivity,
            "lastLoginTime",
            lastLoginTime.toString()
        )
        SharedPrefManager.setSharedPreference(this@LoginActivity, "username", username)
        SharedPrefManager.setSharedPreference(this@LoginActivity, "password", password)
    }

    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        enableButtonClick()
    }

    private fun loginRandomUser() {
        showProgressBar(binding.progressBar)
        RetrofitManager.getAllUser(object : CallBack<GetAllUserResponse> {
            override fun onSuccess(data: GetAllUserResponse) {
                if (data.users.isNotEmpty()) {
                    val randomNumber = generateRandomNumber(data.users.size)
                    val username = data.users[randomNumber].username
                    val password = data.users[randomNumber].password
                    val user = User(username, password)
                    loginWithService(user, object : CallBack<UserData> {
                        override fun onSuccess(data: UserData) {
                            saveLastLoginTime(user.username, user.password)
                            goToMainActivity(data.id.toString(), true)
                        }

                        override fun onFailure(errorMessage: String) {
                            showFancyToast(errorMessage, FancyToast.WARNING)
                            hideProgressBar(binding.progressBar)
                            enableButtonClick()
                        }

                    })
                    hideProgressBar(binding.progressBar)
                } else {
                    showFancyToast("Error!", FancyToast.WARNING)
                    hideProgressBar(binding.progressBar)
                    enableButtonClick()
                }
            }

            override fun onFailure(errorMessage: String) {
                showFancyToast(errorMessage, FancyToast.WARNING)
                hideProgressBar(binding.progressBar)
                enableButtonClick()
            }

        })
    }

    private fun generateRandomNumber(size: Int): Int {
        return Random.nextInt(0, size)
    }

    private fun goToMainActivity(userId: String, isService: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("isService", isService)
        startActivity(intent)
        finish()
    }

}