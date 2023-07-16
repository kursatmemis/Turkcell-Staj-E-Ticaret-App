package com.kursatmemis.e_ticaret_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.kursatmemis.e_ticaret_app.configs.ApiClient
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.kursatmemis.e_ticaret_app.services.DummyService
import com.shashank.sony.fancytoastlib.FancyToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private val dummyService = ApiClient.getClient().create(DummyService::class.java)
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindViews()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar()
                val user = User(username, password)
                login(user)
            } else {
                FancyToast.makeText(
                    this, "Please fill in the username and password fields.",
                    FancyToast.LENGTH_LONG,
                    FancyToast.INFO, false
                ).show()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    private fun login(user: User) {
        dummyService.login(user).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                val body = response.body()
                if (body != null) {
                    userId = body.id
                    loginSuccessful()
                } else {
                    loginFailed()
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                loginFailed()
            }
        })
    }

    private fun loginSuccessful() {
        val msg = "Login is successful."
        showToast(msg, true)
        goToMainActivity()
    }

    private fun loginFailed() {
        hintProgressBar()
        val msg = "An error occurred. Please try again later."
        showToast(msg, false)
    }

    private fun showToast(msg: String, isSuccessful: Boolean) {
        if (isSuccessful) {
            FancyToast.makeText(this, msg, FancyToast.LENGTH_LONG, FancyToast.INFO, false).show()
        }else {
            FancyToast.makeText(this, msg, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hintProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun bindViews() {
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.progressBar)
    }
}