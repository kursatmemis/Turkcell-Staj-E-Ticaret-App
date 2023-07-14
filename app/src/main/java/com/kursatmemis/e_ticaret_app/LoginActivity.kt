package com.kursatmemis.e_ticaret_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.kursatmemis.e_ticaret_app.configs.ApiClient
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.kursatmemis.e_ticaret_app.services.DummyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private val dummyService = ApiClient.getClient().create(DummyService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindViews()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = User(username, password)
                login(user)
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please fill in the username and password fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    /**
     * It provides to go to MainActivity.
     * It sends 'response.body' to MainActivity by using Intent.
     */
    private fun goToMainActivity(userId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    /**
     * It will send a request to the server for login.
     * If response isn't null, it will call method goToMainActivity
     *
     * @user: An object containing the username and password.
     */
    private fun login(user: User) {
        dummyService.login(user).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                val body = response.body()
                if (body != null) {
                    Toast.makeText(this@LoginActivity, "Login is successful.", Toast.LENGTH_SHORT).show()
                    val userId = body.id.toString()
                    goToMainActivity(userId)
                    finish()
                } else {
                    Log.w("mKm - login", "Body is null.")
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.w("mKm - login", "onFailure: $t")
            }
        })
    }

    /**
     * Binds the views in layout file to the code.
     */
    private fun bindViews() {
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
    }
}