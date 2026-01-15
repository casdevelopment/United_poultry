package com.example.unitedpoultry.Authentications.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.unitedpoultry.Authentications.resetpassword.ResetPasswordRequestModel
import com.example.unitedpoultry.Authentications.resetpassword.ResetPasswordViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentNewPasswordBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPasswordFragment : Fragment() {

    private lateinit var binding: FragmentNewPasswordBinding
    private val viewModel: ResetPasswordViewModel by viewModel() // Your API ViewModel
    private val args: NewPasswordFragmentArgs by navArgs() // Safe Args

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = args.email
        val otp = args.otp

        binding.btnSave.setOnClickListener {
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(password, confirmPassword)) {
                callResetPasswordApi(email, otp, password)
            }
        }

        binding.imgTogglePassword.setOnClickListener {

            if (isPasswordVisible) {
                // HIDE password
                binding.etPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.imgTogglePassword.setImageResource(R.drawable.eyevectorfinal) // eye closed
                isPasswordVisible = false
            } else {
                // SHOW password
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.imgTogglePassword.setImageResource(R.drawable.hidepasswordsvg) // eye open
                isPasswordVisible = true
            }

            // Keep cursor at end (VERY IMPORTANT)
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.imgToggleConfirmPassword.setOnClickListener {

            if (isConfirmPasswordVisible) {
                // HIDE password
                binding.etConfirmPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.imgToggleConfirmPassword.setImageResource(R.drawable.eyevectorfinal) // eye closed
                isConfirmPasswordVisible = false
            } else {
                // SHOW password
                binding.etConfirmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.imgToggleConfirmPassword.setImageResource(R.drawable.hidepasswordsvg) // eye open
                isConfirmPasswordVisible = true
            }

            // Keep cursor at end (VERY IMPORTANT)
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
        }
    }

    private fun validateInputs(password: String, confirmPassword: String): Boolean {
        var valid = true
        binding.etPasswordError.visibility = View.GONE
        binding.etConfirmPasswordError.visibility = View.GONE

        if (password.isEmpty()) {
            binding.etPasswordError.text = "Password required"
            binding.etPasswordError.visibility = View.VISIBLE
            valid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPasswordError.text = "Confirm password required"
            binding.etConfirmPasswordError.visibility = View.VISIBLE
            valid = false
        }

        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            binding.etConfirmPasswordError.text = "Passwords do not match"
            binding.etConfirmPasswordError.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

    private fun callResetPasswordApi(email: String, otp: String, password: String) {
        val request = ResetPasswordRequestModel(email, otp, password,password) // Your API request model

        viewModel.resetPassword(request).observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(requireContext())
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val response = apiResponse.data
                    if (response != null && response.isSuccessful) {
                        val baseResponse = response.body()
                        Toast.makeText(requireContext(), baseResponse?.message ?: "Success", Toast.LENGTH_LONG).show()

                        if (baseResponse?.result == "success") {
                            // Navigate to next fragment (e.g., OtpVerifiedFragment)
                            findNavController().navigate(R.id.action_reset_to_Verified)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(requireContext(), apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}
