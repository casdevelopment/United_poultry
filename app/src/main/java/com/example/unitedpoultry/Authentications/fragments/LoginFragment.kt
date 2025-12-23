package com.example.unitedpoultry.Authentications.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.FragmentLoginBinding



class LoginFragment : Fragment() {


    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignUpClick.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_createAccount)
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }

        binding.btnContinueEmail.setOnClickListener {
            val intent = Intent(requireContext(), RiderDashBoardActivity::class.java)
            startActivity(intent)
        }

        binding.btnContinueGoogle.setOnClickListener {
            val intent = Intent(requireContext(), RiderDashBoardActivity::class.java)
            startActivity(intent)
        }

//        binding.btnNext.setOnClickListener {
//
//            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
//            startActivity(intent)
//        }

    }

}
