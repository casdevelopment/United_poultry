package com.example.unitedpoultry.RiderDashBoard

import android.os.Bundle
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
    private var selectedTabId = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRiderDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )

        selectedTabId = savedInstanceState?.getInt("tab") ?: R.id.nav_home

        loadFragment(getFragmentByMenuId(selectedTabId))
        binding.bottomNavigation.selectedItemId = selectedTabId

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId != selectedTabId) {
                selectedTabId = item.itemId
                loadFragment(getFragmentByMenuId(item.itemId))
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getFragmentByMenuId(menuId: Int): Fragment {
        return when (menuId) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_address -> AddressFragment()
            R.id.nav_history -> HistoryFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> HomeFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("tab", selectedTabId)
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager

        // 1. If there are fragments in the back stack (like AreaFragment), pop them first
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return
        }

        // 2. Check current bottom navigation selected fragment
        val selectedId = binding.bottomNavigation.selectedItemId
        if (selectedId != R.id.nav_home) {
            // Go to HomeFragment instead of exiting
            binding.bottomNavigation.selectedItemId = R.id.nav_home
            loadFragment(HomeFragment())
        } else {
            // Already on HomeFragment, exit normally
            super.onBackPressed()
        }
    }

}
