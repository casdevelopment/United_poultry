package com.example.unitedpoultry.AdminDashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.AdminDashBoard.fragments.AreasAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.HomeAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.ReportsAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.RiderAdminFragment
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminDashBoardBinding

class AdminDashBoardActivity : BaseActivity() {

    companion object {
        private const val KEY_SELECTED_TAB = "selected_tab"
    }

    private lateinit var binding: ActivityAdminDashBoardBinding

    private lateinit var homeFragment: HomeAdminFragment
    private lateinit var ridersFragment: RiderAdminFragment
    private lateinit var areasFragment: AreasAdminFragment
    private lateinit var reportsFragment: ReportsAdminFragment

    private lateinit var activeFragment: Fragment
    private var selectedTabId = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )

        selectedTabId = savedInstanceState?.getInt(KEY_SELECTED_TAB) ?: R.id.nav_home

        setupFragments()
        setupBottomNavigation()
    }

    private fun setupFragments() {
        val fm = supportFragmentManager

        // 🔑 Restore existing fragments if they already exist
        homeFragment = fm.findFragmentByTag("home") as? HomeAdminFragment ?: HomeAdminFragment()
        ridersFragment = fm.findFragmentByTag("riders") as? RiderAdminFragment ?: RiderAdminFragment()
        areasFragment = fm.findFragmentByTag("areas") as? AreasAdminFragment ?: AreasAdminFragment()
        reportsFragment = fm.findFragmentByTag("reports") as? ReportsAdminFragment ?: ReportsAdminFragment()

        if (fm.fragments.isEmpty()) {
            // First launch only
            fm.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "home")
                .add(R.id.fragment_container, ridersFragment, "riders").hide(ridersFragment)
                .add(R.id.fragment_container, areasFragment, "areas").hide(areasFragment)
                .add(R.id.fragment_container, reportsFragment, "reports").hide(reportsFragment)
                .commit()
        }

        activeFragment = getFragmentByMenuId(selectedTabId)

        fm.beginTransaction()
            .hide(homeFragment)
            .hide(ridersFragment)
            .hide(areasFragment)
            .hide(reportsFragment)
            .show(activeFragment)
            .commit()

        if (activeFragment is BaseLazyFragment) {
            (activeFragment as BaseLazyFragment).onFragmentSelected()
        }

        binding.bottomNavigation.selectedItemId = selectedTabId

    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            selectedTabId = item.itemId
            switchFragment(getFragmentByMenuId(item.itemId))
            true
        }
    }

    private fun switchFragment(target: Fragment) {

        if (activeFragment == target) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(target)
            .commit()

        activeFragment = target

        if (target is BaseLazyFragment) {
            target.onFragmentSelected()
        }
    }


    private fun getFragmentByMenuId(menuId: Int): Fragment {
        return when (menuId) {
            R.id.nav_home -> homeFragment
            R.id.nav_rider -> ridersFragment
            R.id.nav_areas -> areasFragment
            R.id.nav_report -> reportsFragment
            else -> homeFragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_TAB, selectedTabId)
    }
}
