package com.kursatmemis.e_ticaret_app.activities

import android.content.Intent
import android.os.Bundle
import com.kursatmemis.e_ticaret_app.databinding.ActivitySplashBinding
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.managers.SharedPrefManager
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Calendar

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val elapsedTime = getElapsedTime()
        if (elapsedTime < (60 * 10 * 1000)) {
            showProgressBar(binding.progressBar)
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
            loginWithService(user, object : CallBack<UserData> {
                override fun onSuccess(data: UserData) {
                    goToMainActivity(data.id.toString(), true)
                }

                override fun onFailure(errorMessage: String) {
                    goToLoginActivity()
                    showFancyToast(errorMessage, FancyToast.WARNING)
                    hideProgressBar(binding.progressBar)
                }

            })
        } else {
            loginWithFirebase(username, password, object : CallBack<Any?> {

                override fun onSuccess(data: Any?) {
                    goToMainActivity(FirebaseManager.auth.currentUser!!.uid, false)
                }

                override fun onFailure(errorMessage: String) {
                    goToLoginActivity()
                    showFancyToast(errorMessage, FancyToast.WARNING)
                }
            }, false)
        }
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