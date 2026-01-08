package com.example.unitedpoultry.Authentications.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentVerificationCodeBinding

class VerificationCodeFragment : Fragment() {

    private lateinit var binding: FragmentVerificationCodeBinding

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

        binding.btnVerify.setOnClickListener {
            findNavController().navigate(R.id.action_verification_to_Verified)
        }
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
            if (s?.length == 1) {
                nextView?.requestFocus()
            }
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
}
