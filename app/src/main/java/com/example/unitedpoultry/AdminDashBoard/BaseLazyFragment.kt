package com.example.unitedpoultry.AdminDashBoard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseLazyFragment : Fragment() {

    private var isViewCreated = false
    private var isLazyLoadPending = false

    // Called whenever fragment becomes visible
    fun onFragmentSelected() {
        if (isViewCreated) {
            onLazyLoad()
        } else {
            // If view not yet created, defer lazy load
            isLazyLoadPending = true
        }
    }

    // Fragment-specific lazy load
    abstract fun onLazyLoad()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true

        // If lazy load was requested before view was created
        if (isLazyLoadPending) {
            isLazyLoadPending = false
            onLazyLoad()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        isLazyLoadPending = false
    }
}
