package com.example.foodguard.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController
import com.example.foodguard.R
import com.example.foodguard.data.PostViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels<PostViewModel> { ViewModelProvider.NewInstanceFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            Log.d("MainActivity", "Loading posts...")
            showLoading(true)
            viewModel.refreshPostsFromRemote()
            Log.d("", "Posts loaded")
            showLoading(false)
        }


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    navController.navigate(R.id.postsListFragment)
                    Toast.makeText(this, "home", Toast.LENGTH_SHORT)
                    lifecycleScope.launch {
                        Log.d("MainActivity", "Loading posts...")
                        showLoading(true)
                        viewModel.refreshPostsFromRemote()
                        Log.d("", "Posts loaded")
                        showLoading(false)
                    }
                    true
                }
                R.id.menu_add -> {
                    Toast.makeText(this, "add", Toast.LENGTH_SHORT)

                    navController.navigate(R.id.uploadPostFragment)
                    true
                }
                R.id.menu_profile -> {
                    Toast.makeText(this, "profile", Toast.LENGTH_SHORT)

                    navController.navigate(R.id.profilePageFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val progressBar : ProgressBar = findViewById(R.id.loading_spinner)
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}