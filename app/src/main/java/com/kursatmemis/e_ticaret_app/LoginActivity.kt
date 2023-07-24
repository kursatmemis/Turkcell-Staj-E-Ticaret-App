package com.kursatmemis.e_ticaret_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.databinding.ActivityLoginBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.SharedPrefManager
import com.kursatmemis.e_ticaret_app.models.GetAllUserResponse
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Calendar
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

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
                showProgressBar()
                if (isServiceLogin(username)) {
                    val user = User(username, password)
                    loginWithService(user)
                } else {
                    loginWithFirebase(username, password)
                }

            } else {
                val message = "Please fill in the username and password fields."
                showToast(message, FancyToast.INFO)
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

    private fun isServiceLogin(username: String): Boolean {
        return !username.contains("@")
    }

    private fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun loginWithFirebase(email: String, password: String) {

        FirebaseManager.auth(email, password, object : FirebaseManager.CallBack {
            override fun onSuccess() {
                saveLastLoginTime(email, password)
                goToMainActivity(FirebaseManager.auth.currentUser!!.uid, false)
            }

            override fun onFailure(exceptionMsg: String) {
                showToast(exceptionMsg, FancyToast.WARNING)
                enableButtonClick()
            }
        })
    }

    private fun loginWithService(user: User) {

        RetrofitManager.login(user, object : RetrofitManager.CallBack<UserData> {
            override fun onSuccess(data: UserData) {
                saveLastLoginTime(user.username, user.password)
                goToMainActivity(data.id.toString(), true)
            }

            override fun onFailure() {
                showToast("Error!", FancyToast.WARNING)
                hideProgressBar()
                enableButtonClick()
            }

        })

    }

    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        enableButtonClick()
    }

    private fun loginRandomUser() {
        showProgressBar()

        RetrofitManager.getAllUser(object : RetrofitManager.CallBack<GetAllUserResponse> {
            override fun onSuccess(data: GetAllUserResponse) {
                if (data.users.isNotEmpty()) {
                    val randomNumber = generateRandomNumber(data.users.size)
                    val username = data.users[randomNumber].username
                    val password = data.users[randomNumber].password
                    val user = User(username, password)
                    loginWithService(user)
                    hideProgressBar()
                } else {
                    showToast("Error!", FancyToast.WARNING)
                    hideProgressBar()
                    enableButtonClick()
                }
            }

            override fun onFailure() {
                showToast("Error!", FancyToast.WARNING)
                hideProgressBar()
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

    private fun showToast(msg: String, fancyToastType: Int) {
        FancyToast.makeText(this, msg, FancyToast.LENGTH_LONG, fancyToastType, false).show()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseManager.auth.currentUser
        if (currentUser != null) {
            goToMainActivity(currentUser.uid, false)
        }
    }

}