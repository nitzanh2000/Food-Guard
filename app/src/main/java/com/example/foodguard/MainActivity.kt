package com.example.foodguard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }



        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // Setup BottomNavigationView
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Navigate to the home fragment when the home button is clicked
                    navController.navigate(R.id.postsListFragment)
                    Toast.makeText(this, "home", Toast.LENGTH_SHORT)
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
                // Add other cases for different items in the bottom navigation
                else -> false
            }
        }
//
//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController: NavController = navHostFragment.navController
//
//        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
//
//        bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.menu_profile -> {
//                    // Handle profile action
//                    navController.navigate(R.id.reviewsListFragment)
//                    true
//                }
//                R.id.menu_home -> {
//                    // Handle home action
//                    true
//                }
//                R.id.menu_add -> {
//                    // Handle add action
//                    true
//                }
//                else -> false
//            }
//        }


    }
}