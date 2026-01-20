package com.example.unitedpoultry.Authentications.fragments

import android.content.Context
import android.content.Intent
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
import com.example.unitedpoultry.R
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.Authentications.login.model.LoginRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.Authentications.login.viewmodel.LoginViewModel
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.databinding.FragmentLoginBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModel()
    private val sessionManager: SessionManager by inject()

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

        setClickListeners()
    }

    private fun setClickListeners() {
        with(binding) {

            tvSignUpClick.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_createAccount)
            }

            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_forgot)
            }

            btnContinueEmail.setOnClickListener {
                if (validateInputs()) {
                    callLoginApi()
                }
            }

            rememberLayout.setOnClickListener {
                isRememberChecked = !isRememberChecked
                imgCheckbox.isSelected = isRememberChecked
                imgCheckbox.refreshDrawableState()
            }

            imgTogglePassword.setOnClickListener {
                togglePasswordVisibility()
            }
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.imgTogglePassword.setImageResource(R.drawable.eyevectorfinal) // eye closed
        } else {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.imgTogglePassword.setImageResource(R.drawable.hidepasswordsvg) // eye open
        }
        isPasswordVisible = !isPasswordVisible
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun validateInputs(): Boolean {
        var valid = true
        binding.etEmailError.visibility = View.GONE
        binding.etPasswordError.visibility = View.GONE

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.text = "Email required"
            valid = false
        }

        if (password.isEmpty()) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.text = "Password required"
            valid = false
        } else if (password.length < 6) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.text = "Password must be greater than 6"
            valid = false
        }

        return valid
    }

    private fun callLoginApi() {
        val loginRequest = LoginRequestModel(

            binding.etEmail.text.toString().trim(),
            binding.etPassword.text.toString().trim()
        )

        viewModel.login(loginRequest).observe(viewLifecycleOwner) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> { AppUtil.startLoader(requireContext()) }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val retrofitResponse = apiResponse.data

                    if (retrofitResponse != null) {

                        if (retrofitResponse.isSuccessful) {
                            // ✅ 2xx response
                            val baseResponse = retrofitResponse.body()

                            if (baseResponse != null) {

                                Toast.makeText(requireActivity(), baseResponse.message, Toast.LENGTH_SHORT).show()

                                if (baseResponse.result == "success") {

                                    baseResponse.data?.let { loginResponse ->

                                        sessionManager.saveToken(AppConstants.Bearer + " " + baseResponse.token)

                                        AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()

                                        sessionManager.userInfo(Gson().toJson(loginResponse))

                                        AppConstants.userData = Gson().fromJson(
                                            sessionManager.getUserInfo(),
                                            LoginResponseModel::class.java
                                        )

                                        navigateToDashboard(loginResponse)
                                    }
                                }

                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "Empty response from server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            // ❗ HTTP error but API responded (401, 422, 500)
                            val errorMessage = try {
                                val errorBody = retrofitResponse.errorBody()?.string()
                                if (!errorBody.isNullOrEmpty()) {
                                    val baseResponse =
                                        Gson().fromJson(errorBody, BaseResponse::class.java)
                                    baseResponse.message
                                } else {
                                    "Login failed"
                                }
                            } catch (e: Exception) {
                                "Login failed"
                            }

                            Toast.makeText(
                                requireActivity(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "No response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(
                        requireActivity(),
                        apiResponse.message ?: "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }



    private fun navigateToDashboard(user: LoginResponseModel) {
        // Use role_id to check user type
        if (user.role_id == 1) {
            startActivity(Intent(requireContext(), AdminDashBoardActivity::class.java))
        } else {
            startActivity(Intent(requireContext(), RiderDashBoardActivity::class.java))
        }
        requireActivity().finish() // prevent back to login
    }

    private fun getRoleIdFromUserType(): Int {
        val prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val userType = prefs.getString("USER_TYPE", "user") ?: "user"

        return if (userType == "admin") 1 else 2 // 1 = admin, 2 = rider/user
    }


}
