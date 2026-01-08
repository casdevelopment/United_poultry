package com.example.unitedpoultry.Authentications.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.databinding.FragmentLoginBinding
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private var isRememberChecked = false
    private var isPasswordVisible = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClickListners()


    }

    // ------------------------
    // USER TYPE CHECK
    // ------------------------
    private fun navigateToDashboard() {

        val prefs = requireActivity()
            .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        val userType = prefs.getString("USER_TYPE", "user")

        if (userType == "admin") {
            startActivity(
                Intent(requireContext(), AdminDashBoardActivity::class.java)
            )
        } else {
            startActivity(
                Intent(requireContext(), RiderDashBoardActivity::class.java)
            )
        }

        // Optional: prevent going back to login
        requireActivity().finish()
    }

    private fun setClickListners(){
        with(binding) {

            tvSignUpClick.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_createAccount)
            }

            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_forgot)
            }

            btnContinueEmail.setOnClickListener {
                if (validateInputs()) {

                    navigateToDashboard()
                }

            }

            rememberLayout.setOnClickListener {
                isRememberChecked = !isRememberChecked

                binding.imgCheckbox.isSelected = isRememberChecked
                binding.imgCheckbox.refreshDrawableState()
            }

            imgTogglePassword.setOnClickListener {

                if (isPasswordVisible) {
                    // HIDE password
                    etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    imgTogglePassword.setImageResource(R.drawable.eyevectorfinal) // eye closed
                    isPasswordVisible = false
                } else {
                    // SHOW password
                    etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    imgTogglePassword.setImageResource(R.drawable.hidepasswordsvg) // eye open
                    isPasswordVisible = true
                }

                // Keep cursor at end (VERY IMPORTANT)
                etPassword.setSelection(etPassword.text.length)
            }


        }
    }


    private fun validateInputs(): Boolean {
        var valid = true

        binding.etEmailError.visibility = View.GONE
        binding.etPasswordError.visibility = View.GONE

        val password = binding.etPassword.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()


        if (email.isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.setText ("Email required")

            valid = false
        }

        if (password.isEmpty()) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.setText ("password required")
            valid = false
        } else if (password.length < 6) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.setText ("Password must greater then 6")
            valid = false
        }

        return valid
    }


}
