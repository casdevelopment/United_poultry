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
import com.example.unitedpoultry.databinding.FragmentCreateAccountBinding
import com.example.unitedpoultry.databinding.FragmentDailyRateEntryBinding


class DailyRateEntryFragment : Fragment() {


    private lateinit var binding : FragmentDailyRateEntryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyRateEntryBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            val intent = Intent(requireContext(), RiderDashBoardActivity::class.java)
            startActivity(intent)
        }

//        binding.tvForgotPassword.setOnClickListener {
//            findNavController().navigate(R.id.action_createAccount_to_forgot)
//        }

//        binding.btnNext.setOnClickListener {
//
//            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
//            startActivity(intent)
//        }

    }

}
