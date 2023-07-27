package com.kursatmemis.e_ticaret_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kursatmemis.e_ticaret_app.R
import com.kursatmemis.e_ticaret_app.databinding.ActivityMainBinding
import com.kursatmemis.e_ticaret_app.managers.SharedPrefManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    companion object {
        var userId: String? = null
        var isServiceLogin: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("mKm - firebase", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("mKm - firebase", token)
        })

        userId = intent.getStringExtra("userId")
        isServiceLogin = intent.getBooleanExtra("isService", false)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chance_mode -> {
                // Mevcut gece modu durumunu al
                val nightMode = AppCompatDelegate.getDefaultNightMode()

                if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    // Eğer karanlık moddaysa, açık moda geç
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    // Eğer açık moddaysa, karanlık moda geç
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                return true
            }

            R.id.action_logout -> {
                SharedPrefManager.clearSharedPreference(this)
                val auth: FirebaseAuth = Firebase.auth
                if (auth.currentUser != null) {
                    auth.signOut()
                }
                backToLogin()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun backToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("logout", true)
        startActivity(intent)
        finish()
    }

    private fun setupViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        NavigationUI.setupWithNavController(binding.bottomNav, navHostFragment.navController)
        setSupportActionBar(binding.toolbar)
    }

}