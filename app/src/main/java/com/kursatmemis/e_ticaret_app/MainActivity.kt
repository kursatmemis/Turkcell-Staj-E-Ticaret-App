package com.kursatmemis.e_ticaret_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.kursatmemis.e_ticaret_app.configs.ApiClient
import com.kursatmemis.e_ticaret_app.services.DummyService

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    companion object {
        val dummyService: DummyService = ApiClient.getClient().create(DummyService::class.java)
        var userId: Long = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.navController)

        userId = intent.getLongExtra("userId", -1)
    }

    private fun bindViews() {
        bottomNav = findViewById(R.id.bottomNav)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
    }
}