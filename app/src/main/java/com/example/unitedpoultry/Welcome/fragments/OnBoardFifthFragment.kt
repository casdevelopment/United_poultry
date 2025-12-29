package com.example.unitedpoultry.Welcome.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentOnBoardFifthBinding


class OnBoardFifthFragment : Fragment() {


    private lateinit var binding : FragmentOnBoardFifthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardFifthBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_fifth_to_forth)
        }

        binding.btnNext.setOnClickListener {

            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)
        }

        binding.btnContinueEmail.setOnClickListener {

            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)
        }

    }

}
