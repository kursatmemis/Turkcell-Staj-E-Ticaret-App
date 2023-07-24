package com.kursatmemis.e_ticaret_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kursatmemis.e_ticaret_app.databinding.ActivitySplashBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.managers.SharedPrefManager
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Calendar

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logout = SharedPrefManager.getSharedPreference(this, "logout", false.toString())
        Log.w("ddds", logout.toString())
        if (logout.toBoolean()) {
            SharedPrefManager.clearSharedPreference(this)
        }

        val elapsedTime = getElapsedTime()
        if (elapsedTime < 5 * 60 * 1000) {
            showProgressBar()
            autoLogin()
        } else {
            goToLoginActivity()
        }


    }

    private fun goToLoginActivity() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun autoLogin() {
        val username = SharedPrefManager.getSharedPreference(this, "username", "")
        val password = SharedPrefManager.getSharedPreference(this, "password", "")
        val user = User(username!!, password!!)

        if (isServiceLogin(username)) {
            loginWithService(user)
        } else {
            loginWithFirebase(username, password)
        }
    }

    private fun isServiceLogin(username: String): Boolean {
        return !username.contains("@")
    }

    private fun loginWithFirebase(email: String, password: String) {

        FirebaseManager.auth(email, password, object : FirebaseManager.CallBack {
            override fun onSuccess() {
                goToMainActivity(FirebaseManager.auth.currentUser!!.uid, false)
            }

            override fun onFailure(exceptionMsg: String) {
                showToast(exceptionMsg, FancyToast.WARNING)
            }
        })
    }

    private fun loginWithService(user: User) {

        RetrofitManager.login(user, object : RetrofitManager.CallBack<UserData> {
            override fun onSuccess(data: UserData) {
                goToMainActivity(data.id.toString(), true)
            }

            override fun onFailure() {
                showToast("Error!", FancyToast.WARNING)
                hideProgressBar()
            }

        })

    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showToast(msg: String, fancyToastType: Int) {
        FancyToast.makeText(this, msg, FancyToast.LENGTH_LONG, fancyToastType, false).show()
    }

    private fun getElapsedTime(): Long {
        val currentTime = Calendar.getInstance().timeInMillis
        val lastLoginTime = SharedPrefManager.getSharedPreference(this, "lastLoginTime", "0")
        return currentTime - lastLoginTime!!.toLong()
    }


    private fun goToMainActivity(userId: String, isService: Boolean) {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("isService", isService)
        startActivity(intent)
        finish()
    }

}