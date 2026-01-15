package com.example.unitedpoultry.Authentications.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.unitedpoultry.Authentications.verifyotp.OtpVerificationViewModel
import com.example.unitedpoultry.Authentications.verifyotp.VerifyOtpRequestModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentVerificationCodeBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerificationCodeFragment : Fragment() {

    private lateinit var binding: FragmentVerificationCodeBinding
    private val viewModel: OtpVerificationViewModel by viewModel()
    private val args: VerificationCodeFragmentArgs by navArgs() // get email from previous fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpInputs()
        setClickListeners()
    }

    private fun setClickListeners() {
        with(binding) {
            btnVerify.setOnClickListener {
                val email = args.email
                val otp = getOtpFromInputs()

                if (otp.length == 4) {

//                    val action = VerificationCodeFragmentDirections
//                        .actionVerificationToReset(
//                            email = email,
//                            otp = otp
//                        )
//                    findNavController().navigate(action)
                    callVerifyOtpApi(email, otp)
                } else {
                    Toast.makeText(requireContext(), "Please enter complete OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getOtpFromInputs(): String {
        return binding.etOtp1.text.toString().trim() +
                binding.etOtp2.text.toString().trim() +
                binding.etOtp3.text.toString().trim() +
                binding.etOtp4.text.toString().trim()
    }

    private fun setupOtpInputs() {
        val otp1 = binding.etOtp1
        val otp2 = binding.etOtp2
        val otp3 = binding.etOtp3
        val otp4 = binding.etOtp4

        otp1.addTextChangedListener(OtpTextWatcher(otp1, null, otp2))
        otp2.addTextChangedListener(OtpTextWatcher(otp2, otp1, otp3))
        otp3.addTextChangedListener(OtpTextWatcher(otp3, otp2, otp4))
        otp4.addTextChangedListener(OtpTextWatcher(otp4, otp3, null))
    }

    private class OtpTextWatcher(
        private val currentView: EditText,
        private val previousView: EditText?,
        private val nextView: EditText?
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (s?.length == 1) nextView?.requestFocus()
        }

        init {
            currentView.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    currentView.text.isEmpty()
                ) {
                    previousView?.requestFocus()
                }
                false
            }
        }
    }

    private fun callVerifyOtpApi(email: String, otp: String) {
        val request = VerifyOtpRequestModel(
            email = email,
            otp = otp
        )

        viewModel.verifyOtp(request).observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(requireContext())
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val response = apiResponse.data
                    if (response != null && response.isSuccessful) {
                        val baseResponse = response.body()
                        Toast.makeText(
                            requireContext(),
                            baseResponse?.message ?: "Success",
                            Toast.LENGTH_LONG
                        ).show()

                        if (baseResponse?.result == "success") {
                            val action = VerificationCodeFragmentDirections
                                .actionVerificationToReset(
                                    email = email,
                                    otp = otp
                                )
                            findNavController().navigate(action)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(requireContext(), apiResponse.message ?: "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}
