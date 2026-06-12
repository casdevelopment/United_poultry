package com.example.unitedpoultry.AdminDashBoard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.AdminDashBoard.fragments.AdminRateManagementFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.AreasAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.HomeAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.ReportsAdminFragment
import com.example.unitedpoultry.AdminDashBoard.fragments.RiderAdminFragment
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminDashBoardBinding

class AdminDashBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminDashBoardBinding
    private var selectedTabId = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(isLightBackground = false, colorResId = R.color.primary)

        //configureStatusBar(false, R.color.primary)

        selectedTabId = savedInstanceState?.getInt("tab") ?: R.id.nav_home

        loadFragment(getFragmentByMenuId(selectedTabId))

        binding.bottomNavigation.selectedItemId = selectedTabId

        setupBottomNavigation()

        setupKeyboardListener()
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
            R.id.nav_home -> HomeAdminFragment()
            R.id.nav_rate -> AdminRateManagementFragment()
            R.id.nav_rider -> RiderAdminFragment()
            R.id.nav_areas -> AreasAdminFragment()
            R.id.nav_report -> ReportsAdminFragment()
            else -> HomeAdminFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("tab", selectedTabId)
    }


    private fun setupKeyboardListener() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            binding.bottomNavigation.visibility =
                if (keypadHeight > screenHeight * 0.15) View.GONE else View.VISIBLE
        }
    }


    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return
        }

        if (binding.bottomNavigation.selectedItemId != R.id.nav_home) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
            loadFragment(HomeAdminFragment())
        } else {
            super.onBackPressed()
        }


    }
}
