package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Notification.NotificationActivity
import com.example.unitedpoultry.Profile.ChangePasswordActivity
import com.example.unitedpoultry.Profile.MyPerformanceActivity
import com.example.unitedpoultry.Profile.MyProfileActivity
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.Splash.SplashActivity
import com.example.unitedpoultry.Welcome.WelcomeActivity
import com.example.unitedpoultry.databinding.FragmentHomeBinding
import com.example.unitedpoultry.databinding.FragmentProfileBinding
import com.example.unitedpoultry.status_check.UserStatusChecker
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppConstants.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val sessionManager: SessionManager by inject()
    private val ViewModel4: UserStatusViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onclick()

    }

    override fun onResume() {
        super.onResume()
        checkStatus()
        showData()
    }

    private fun showData(){

        binding.tvName.text = userData?.username ?: "User Name"

        binding.tvInitials.text = getInitials(userData?.username ?: "User Name")

    }

    private fun checkStatus(){

        UserStatusChecker.check(
            lifecycleOwner = viewLifecycleOwner,
            viewModel = ViewModel4,

            onActive = {

                binding.capsuleText.text = "Active"
            },

            onInactive = {

                binding.capsuleText.text = "Inactive"
            },

            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun  onclick(){

        binding.logoutCard.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // 🔥 THIS LINE FIXES THE EDGES
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
            val btnLogout = dialogView.findViewById<MaterialButton>(R.id.btnLogout)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnLogout.setOnClickListener {

                dialog.dismiss()

                sessionManager.logout()

                val intent = Intent(requireContext(), SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }

            dialog.show()
        }

        binding.myProfileCard.setOnClickListener {
            val intent = Intent(requireContext(), MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changePasswordCard.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.assignedAreas.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.nav_address
        }

    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return "U" // Default initial
        val words = name.trim().split(" ")
        return when {
            words.size >= 2 -> "${words[0][0]}${words[1][0]}".uppercase()
            words.isNotEmpty() -> "${words[0][0]}".uppercase()
            else -> "U"
        }
    }


}
