package com.kursatmemis.e_ticaret_app.activities

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import com.kursatmemis.e_ticaret_app.managers.FirebaseManager
import com.kursatmemis.e_ticaret_app.managers.RetrofitManager
import com.kursatmemis.e_ticaret_app.models.CallBack
import com.kursatmemis.e_ticaret_app.models.User
import com.kursatmemis.e_ticaret_app.models.UserData
import com.shashank.sony.fancytoastlib.FancyToast

open class BaseActivity : AppCompatActivity() {

    open fun showProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
    }

    open fun hideProgressBar(progressBar: ProgressBar) {
        progressBar.visibility = View.INVISIBLE
    }

    open fun isServiceLogin(username: String): Boolean {
        return !username.contains("@")
    }


    open fun showFancyToast(message: String, fancyToastType: Int) {
        FancyToast.makeText(this, message, FancyToast.LENGTH_LONG, fancyToastType, false).show()
    }

    open fun showSnackBar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    open fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun loginWithFirebase(
        email: String,
        password: String,
        callback: CallBack<Any?>,
        isToSendVerification: Boolean = false
    ) {
        FirebaseManager.login(email, password, callback, isToSendVerification)
    }

    open fun loginWithService(user: User, callback: CallBack<UserData>) {
        RetrofitManager.login(user, callback)
    }



}