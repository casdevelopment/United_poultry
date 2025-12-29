package com.example.unitedpoultry.RiderDashBoard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RiderDashBoard.fragments.AddressFragment
import com.example.unitedpoultry.RiderDashBoard.fragments.HistoryFragment
import com.example.unitedpoultry.RiderDashBoard.fragments.HomeFragment
import com.example.unitedpoultry.RiderDashBoard.fragments.ProfileFragment
import com.example.unitedpoultry.databinding.ActivityRiderDashBoardBinding



class RiderDashBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityRiderDashBoardBinding

    // Keep fragment instances
    private val homeFragment = HomeFragment()
    private val addressFragment = AddressFragment()
    private val historyFragment = HistoryFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiderDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )


        // Add all fragments, show only home
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment)
            .add(R.id.fragment_container, historyFragment, "history").hide(historyFragment)
            .add(R.id.fragment_container, addressFragment, "address").hide(addressFragment)
            .add(R.id.fragment_container, homeFragment, "home")
            .commit()

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(homeFragment)
                R.id.nav_address -> switchFragment(addressFragment)
                R.id.nav_history -> switchFragment(historyFragment)
                R.id.nav_profile -> switchFragment(profileFragment)
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
