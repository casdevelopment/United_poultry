package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val sessionManager: SessionManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Initialize toggle state
//        binding.toggleStatus.isChecked = false
//        updateStatusUI(false)
//
//        // Listen for toggle changes
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

        binding.cardChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.cardMyPerformance.setOnClickListener {
            val intent = Intent(requireContext(), MyPerformanceActivity::class.java)
            startActivity(intent)
        }

    }

}
