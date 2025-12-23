package com.example.unitedpoultry.Authentications.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentCreateAccountBinding




class CreateAccountFragment : Fragment() {


    private lateinit var binding : FragmentCreateAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLoginClick.setOnClickListener {
            findNavController().navigate(R.id.action_createAccount_to_login)
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_createAccount_to_forgot)
        }

//        binding.btnNext.setOnClickListener {
//
//            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
//            startActivity(intent)
//        }

    }

}
