package com.example.unitedpoultry.Welcome.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentOnBoardForthBinding
import com.example.unitedpoultry.util.AppConstants.TYPE

class OnBoardForthFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardForthBinding

    // Will store selected type
    private var selectedUserType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardForthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)

        // Optional: Change status bar icons to dark if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = 0 // light icons: 0, dark icons: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // User option click
        binding.optionRider.setOnClickListener {
            selectUserType("rider")
            TYPE="seller"
        }

        // Admin option click
        binding.optionAdmin.setOnClickListener {
            TYPE="admin"
            selectUserType("admin")
        }

        // Continue button
        binding.btnContinue.setOnClickListener {

            // VALIDATION
            if (selectedUserType == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select User or Admin",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            saveUserType(selectedUserType!!)

            // Navigate to next (Welcome / Auth graph)
            findNavController().navigate(R.id.action_forth_to_fifth)
        }
    }

    // Highlight selection
    private fun selectUserType(type: String) {
        selectedUserType = type

        if (type == "rider") {
            binding.optionRider.setBackgroundResource(R.drawable.border_selected)
            binding.optionAdmin.setBackgroundResource(R.drawable.bg_admin_unselected)
        } else {
            binding.optionAdmin.setBackgroundResource(R.drawable.bg_admin_selected)
            binding.optionRider.setBackgroundResource(R.drawable.border_unselected)
        }
    }

    private fun saveUserType(type: String) {
        val prefs = requireActivity()
            .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        prefs.edit()
            .putString("USER_TYPE", type)
            .apply()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}
