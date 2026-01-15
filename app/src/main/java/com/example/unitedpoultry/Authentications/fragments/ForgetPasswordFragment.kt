package com.example.unitedpoultry.Authentications.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.Authentications.forgetpassword.ForgetPasswordViewModel
import com.example.unitedpoultry.Authentications.forgetpassword.ForgotPasswordRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginRequestModel
import com.example.unitedpoultry.Authentications.login.viewmodel.LoginViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentForgetPasswordBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForgetPasswordFragment : Fragment() {


    private lateinit var binding : FragmentForgetPasswordBinding
    private val viewModel: ForgetPasswordViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()



    }


    private fun setClickListeners() {
        with(binding) {

            btnSendOtp.setOnClickListener {

            }

            btnSendOtp.setOnClickListener {

                if (validateInputs()) {

                   // val email = binding.etEmail.text.toString().trim()

                    // Safe Args navigation
//                    val action = ForgetPasswordFragmentDirections
//                        .actionForgotToVerification(email)
//                    findNavController().navigate(action)

                    callForgetPasswordApi()
                }

            }

            tvSignInClick.setOnClickListener {
                findNavController().navigate(R.id.action_forgot_to_login)
            }

        }
    }


    private fun validateInputs(): Boolean {
        var valid = true

        binding.etEmailError.visibility = View.GONE

        val email = binding.etEmail.text.toString().trim()


        if (email.isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.setText ("Email required")

            valid = false
        }

        return valid
    }

    private fun callForgetPasswordApi() {

        val request = ForgotPasswordRequestModel(
            email = binding.etEmail.text.toString().trim()
        )

        viewModel.forgetPassword(request).observe(viewLifecycleOwner) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(requireContext())
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data

                    if (response != null && response.isSuccessful) {

                        val baseResponse = response.body()

                        // ✅ ALWAYS show server message
                        Toast.makeText(
                            requireContext(),
                            baseResponse?.message ?: "Success",
                            Toast.LENGTH_LONG
                        ).show()

                        // ✅ Navigate ONLY on success
                        if (baseResponse?.result == "success") {
                            val email = binding.etEmail.text.toString().trim()

                            // Safe Args navigation
                            val action = ForgetPasswordFragmentDirections
                                .actionForgotToVerification(email)
                            findNavController().navigate(action)
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(
                        requireContext(),
                        apiResponse.message ?: "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

}


