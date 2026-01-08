package com.example.unitedpoultry.Authentications.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentForgetPasswordBinding


class ForgetPasswordFragment : Fragment() {


    private lateinit var binding : FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendOtp.setOnClickListener {

        }

        binding.btnSendOtp.setOnClickListener {
            if (validateInputs()) {

                findNavController().navigate(R.id.action_forgot_to_verification)
            }

        }

        binding.tvSignInClick.setOnClickListener {
            findNavController().navigate(R.id.action_forgot_to_login)
        }

    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etEmailError.visibility = View.GONE

        val email = binding.etEmail.text.toString().trim()


        if (email.isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.setText ("Email or Username required")

            valid = false
        }

        return valid
    }

}
