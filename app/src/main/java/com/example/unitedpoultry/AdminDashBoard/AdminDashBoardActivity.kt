package com.example.unitedpoultry.AdminDashBoard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.AdminDashBoard.fragments.AreasAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.HomeAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.ReportsAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.RiderAdminFragment
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminDashBoardBinding


class AdminDashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashBoardBinding

    // Keep fragment instances
    private val homeFragment = HomeAdminFragment()
    private val ridersFragment = RiderAdminFragment()
    private val areasFragment = AreasAdminFragment()
    private val reportsFragment = ReportsAdminFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Add all fragments, show only home
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, reportsFragment, "profile").hide(reportsFragment)
            .add(R.id.fragment_container, areasFragment, "history").hide(areasFragment)
            .add(R.id.fragment_container, ridersFragment, "address").hide(ridersFragment)
            .add(R.id.fragment_container, homeFragment, "home")
            .commit()

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(homeFragment)
                R.id.nav_rider -> switchFragment(ridersFragment)
                R.id.nav_areas -> switchFragment(areasFragment)
                R.id.nav_report -> switchFragment(reportsFragment)
            }
            true
        }
    }

    private fun switchFragment(target: Fragment) {
        if (activeFragment != target) {
            supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit()
            activeFragment = target
        }
    }
}
