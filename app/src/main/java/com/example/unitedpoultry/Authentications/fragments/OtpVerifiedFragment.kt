package com.example.unitedpoultry.Authentications.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentForgetPasswordBinding
import com.example.unitedpoultry.databinding.FragmentOtpVerifiedBinding
import com.example.unitedpoultry.databinding.FragmentVerificationCodeBinding


class OtpVerifiedFragment : Fragment() {


    private lateinit var binding : FragmentOtpVerifiedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerifiedBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_verified_to_dailyRate)
        }

//        binding.tvSignInClick.setOnClickListener {
//            findNavController().navigate(R.id.action_forgot_to_login)
//        }

    }

}
